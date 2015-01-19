package server.plugin.listener;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

import server.plugin.ServerPlugin;
import server.plugin.combat.AbilityManager;
import server.plugin.combat.abilities.Ability;
import server.plugin.common.EnumHelper;
import server.plugin.common.PlayerHelper;
import server.plugin.event.DebugEvent;
import server.plugin.event.PlayerEnterRegionEvent;
import server.plugin.event.PlayerJumpInAirEvent;
import server.plugin.event.PlayerLeaveRegionEvent;
import server.plugin.event.PlayerStartRunningEvent;
import server.plugin.event.PlayerStopRunningEvent;
import server.plugin.event.ServerPlayerMoveEvent;
import server.plugin.manager.ChatChannelManager;
import server.plugin.manager.CommandManager;
import server.plugin.manager.ConfigManager;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.manager.RegionManager;
import server.plugin.types.ChatChannel;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.Enums.StatisticType;
import server.plugin.types.ServerPlayer;
import server.plugin.types.ServerRegion;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

public class ServerListener implements Listener {

    private static ServerListener instance = new ServerListener();

    public static ServerListener getInstance(){
        return instance;
    }

    public void init(){
        onPacket();
    }

    // Event handlers

    public void onPacket(){
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        chatSend(protocolManager);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        playerGainBlockBreakStatistic(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        playerGainBlockPlaceStatistic(event);
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event){
        // checkDisguiseInCommandArguments(event);
        CommandManager.getInstance().CommandPreprocesser(event);
    }

    @EventHandler
    public void onDebugMessage(DebugEvent event){
        checkForDebugListeningPlayers(event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        playerGainDamageDealtStatistic(event);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        playerGainDamageTakenStatistic(event);
        if(event.getEntity().getMetadata("ability").size() > 0 && event.getEntity().getMetadata("ability").get(0).asString() != null && event.getEntity().getMetadata("immune").size() > 0 && event.getEntity().getMetadata("immune").get(0).asString() != null) {
            Ability ability = AbilityManager.getInstance().getAbilityFromName(event.getEntity().getMetadata("ability").get(0).asString());
            if(event.getEntity().getMetadata("immune").get(0).asString().equalsIgnoreCase("damage")) {
                if(ability.onHit(event.getEntity()))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event){
        playerGainKillStatistic(event);
    }

    @EventHandler
    public void onEntityTame(EntityTameEvent event){
        playerGainTameStatistic(event);
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event){
        checkDisguiseOnLogout(event);
        removePlayerFromManagerOnLogout(event);
        removeChatChannels(event);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event){
        configurePlayerOnLogin(event);
        configureNameOnLogin(event);
        checkDisguiseOnLogin(event);
        stupidLepper(event);
        addChatChannels(event);

        event.getPlayer().setAllowFlight(true); // this is used to detect a
                                                // double jump. it is cancelled
                                                // if they aren't in creative
        event.getPlayer().setNoDamageTicks(0);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        checkDisguisePlayerDeath(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        watchForServerPlayerMoveEvent(event);
        watchForRunningEvents(event);
    }

    @EventHandler
    public void onServerPlayerMove(ServerPlayerMoveEvent event){
        checkPlayerEnterRegion(event);
        checkPlayerLeaveRegion(event);
    }

    @EventHandler
    public void tag(PlayerReceiveNameTagEvent event){
        checkNameTag(event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            AbilityManager.getInstance().onAbilityUse(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event){
        AbilityManager.getInstance().onAbilityUse(event);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if(event.getEntity().getPassenger() != null) {
            if(event.getEntity().getPassenger().getMetadata("ability").size() > 0 && event.getEntity().getPassenger().getMetadata("ability").get(0).asString() != null) {
                Ability ability = AbilityManager.getInstance().getAbilityFromName(event.getEntity().getPassenger().getMetadata("ability").get(0).asString());
                ability.onHit(event.getEntity());
            } else {
                if(event.getEntity().getMetadata("ability").size() > 0 && event.getEntity().getMetadata("ability").get(0).asString() != null) {
                    Ability ability = AbilityManager.getInstance().getAbilityFromName(event.getEntity().getMetadata("ability").get(0).asString());
                    ability.onHit(event.getEntity());
                }
            }
        } else {
            if(event.getEntity().getMetadata("ability").size() > 0 && event.getEntity().getMetadata("ability").get(0).asString() != null) {
                Ability ability = AbilityManager.getInstance().getAbilityFromName(event.getEntity().getMetadata("ability").get(0).asString());
                ability.onHit(event.getEntity());
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event){
        if(event.getReason().equalsIgnoreCase("flying is not enabled on this server")) {
            if(event.getPlayer().getMetadata("ability").size() > 0 && event.getPlayer().getMetadata("ability").get(0).asString() != null && event.getPlayer().getMetadata("allowed").size() > 0 && event.getPlayer().getMetadata("allowed").get(0).asString() != null) {
                if(event.getPlayer().getMetadata("allowed").get(0).asString().equalsIgnoreCase("flight")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        ChatChannelManager.getInstance().handleChat(event);
    }

    @EventHandler
    public void detectJumpInAirEvent(PlayerToggleFlightEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerJumpInAirEvent(event.getPlayer()));
            event.setCancelled(true);
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler
    public void onJumpInAir(PlayerJumpInAirEvent event){
        Vector jump = event.getPlayer().getLocation().getDirection().multiply(0.2).setY(1.1);
        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(jump));
        event.getPlayer().setVelocity(jump);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event){
        System.out.println(event.getCommand().getBytes()[0]);
    }

    // @EventHandler
    // public void onProjectileLaunch(ProjectileLaunchEvent event){
    // if(event.getEntity().getMetadata("ability") != null){
    // Ability ability =
    // AbilityManager.getInstance().getAbilityFromName(event.getEntity().getMetadata("ability").get(0).asString());
    // ability.onHit(event.getEntity());
    // }
    // }

    // Event functions

    // private void checkDisguiseInCommandArguments(PlayerCommandPreprocessEvent
    // event){
    // String message = event.getMessage().toLowerCase();
    //
    // if (
    // message.startsWith("/msg") ||
    // message.startsWith("/message") ||
    // message.startsWith("/w") ||
    // message.startsWith("/whisper") ||
    // message.startsWith("/tpa") ||
    // message.startsWith("/tpahere") ||
    // message.startsWith("/perm")
    // ){
    //
    // if (message.startsWith("/msg"))
    // event.setMessage(message.replaceFirst("/msg", "/message"));
    //
    // String[] split = event.getMessage().split(" ");
    // if(split.length > 1){
    // for(ServerPlayer player :
    // PlayerManager.getInstance().getOnlinePlayers()){
    // if(player.getString("disguise") != null){
    // if(Bukkit.getPlayer(split[1]) == player.getBukkitPlayer()){
    // if(getOtherPlayer(player, split[1]) != null){
    // event.setMessage(message.replaceFirst(split[1].toLowerCase(),
    // getOtherPlayer(player, split[1]).getName()));
    // return;
    // } else {
    // event.setCancelled(true);
    // MessageManager.getInstance().sendMessage(event.getPlayer(), "Player '" +
    // split[1] + "' does not exist!", MessageType.SEVERE, false);
    // return;
    // }
    // }
    // if(player.getString("disguise").toLowerCase().startsWith(split[1].toLowerCase())){
    // if(Bukkit.getPlayer(split[1]) != null){
    // if(player.getString("disguise").length() <
    // Bukkit.getPlayer(split[1].toLowerCase()).getName().length()){
    // event.setMessage(message.replaceFirst(split[1].toLowerCase(),
    // player.getName()));
    // return;
    // }
    // }
    // }
    // if(player.getString("disguise").toLowerCase().startsWith(split[1].toLowerCase())){
    // event.setMessage(event.getMessage().replaceFirst(split[1],
    // player.getName()));
    // }
    // }
    // }
    // }
    // if(split.length > 3 && message.startsWith("/perm")){
    // for(ServerPlayer player :
    // PlayerManager.getInstance().getOnlinePlayers()){
    // if(player.getString("disguise") != null){
    // if(Bukkit.getPlayer(split[2]) == player.getBukkitPlayer()){
    // if(getOtherPlayer(player, split[2]) != null){
    // event.setMessage(message.replaceFirst(split[2].toLowerCase(),
    // getOtherPlayer(player, split[2]).getName()));
    // return;
    // } else {
    // event.setCancelled(true);
    // MessageManager.getInstance().sendMessage(event.getPlayer(), "Player '" +
    // split[2] + "' does not exist!", MessageType.SEVERE, false);
    // return;
    // }
    // }
    // if(player.getString("disguise").toLowerCase().startsWith(split[2].toLowerCase())){
    // if(Bukkit.getPlayer(split[2]) != null){
    // if(player.getString("disguise").length() <
    // Bukkit.getPlayer(split[2].toLowerCase()).getName().length()){
    // event.setMessage(message.replaceFirst(split[2].toLowerCase(),
    // player.getName()));
    // return;
    // }
    // }
    // } //msg name /perm set name
    // if(player.getString("disguise").toLowerCase().startsWith(split[2].toLowerCase())){
    // Bukkit.getServer().broadcastMessage("Pass");
    // event.setMessage(event.getMessage().replaceFirst(split[2],
    // player.getName()));
    // }
    // }
    // }
    // }
    // return;
    // }
    // }

    private void checkNameTag(PlayerReceiveNameTagEvent event){
        //if(ConfigManager.getInstance().getConfig("Config Files.misc").getBoolean("disguise_visible_for_us")) { // Makes
                                                                                                               // it
                                                                                                               // so
                                                                                                               // we
                                                                                                               // see
                                                                                                               // ourselves
                                                                                                               // normally
            if(Permission.JESTER.isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(event.getPlayer()))) { // Makes
                                                                                                              // it
                                                                                                              // so
                                                                                                              // we
                                                                                                              // see
                                                                                                              // ourselves
                                                                                                              // normally
                for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
                    if(event.getNamedPlayer().getName().equals(p.getName())) { // if
                                                                               // the
                                                                               // player
                                                                               // that's
                                                                               // sending
                                                                               // the
                                                                               // information
                                                                               // is
                                                                               // the
                                                                               // player
                                                                               // we're
                                                                               // checking
                        Permission type = PlayerHelper.getPlayerPermission(p.getBukkitPlayer());
                        event.setTag(type.color + p.getName()); // set their tag
                                                                // to this

                    }
                }
                return;
            }
        //}
        String configType = ConfigManager.getInstance().getConfig("Config Files.permission").getString("disguise_permission");
        Permission type = EnumHelper.getPlayerType(configType);
        for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
            if(!p.getStringTemp("disguise").equals("")) {
                type = EnumHelper.getPlayerType(configType);
                if(event.getNamedPlayer().getName().equals(p.getName())) // if
                                                                         // the
                                                                         // player
                                                                         // that's
                                                                         // sending
                                                                         // the
                                                                         // information
                                                                         // is
                                                                         // the
                                                                         // player
                                                                         // we're
                                                                         // checking
                    event.setTag(type.color + p.getStringTemp("disguise")); // set
                                                                            // their
                                                                            // tag
                                                                            // to
                                                                            // this
            } else {
                type = PlayerHelper.getPlayerPermission(p.getBukkitPlayer());
                if(event.getNamedPlayer().getName().equals(p.getName())) // if
                                                                         // the
                                                                         // player
                                                                         // that's
                                                                         // sending
                                                                         // the
                                                                         // information
                                                                         // is
                                                                         // the
                                                                         // player
                                                                         // we're
                                                                         // checking
                    event.setTag(type.color + p.getName()); // set their tag to
                                                            // this
            }
        }
    }

    private void checkDisguiseOnLogin(PlayerJoinEvent event){
        ServerPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer());

        for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
            if(!p.getStringTemp("disguise").equals("") && event.getJoinMessage().contains(p.getName())) {
                String message = event.getJoinMessage();
                String newMessage = message.replaceAll(player.getName(), p.getStringTemp("disguise"));

                event.setJoinMessage(newMessage);

                PlayerHelper.refreshPlayerName(p.getBukkitPlayer());

                MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.BLUE + "" + ChatColor.ITALIC + "You are disguised as '" + p.getStringTemp("disguise") + "'");
                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, "Undisguise with /disguise");
            }
        }
    }

    private void checkDisguiseOnLogout(PlayerQuitEvent event){
        String message = event.getQuitMessage();
        String newMessage = "";
        for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
            if(!p.getStringTemp("disguise").equals("") && event.getQuitMessage().contains(p.getName())) {
                newMessage = message.replaceAll(p.getName(), p.getStringTemp("disguise"));
            }
        }
        if(!newMessage.equals(""))
            event.setQuitMessage(newMessage);
    }

    private void checkDisguisePlayerDeath(PlayerDeathEvent event){
        String newMessage = "";
        String message = event.getDeathMessage();
        for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
            if(!p.getStringTemp("disguise").equals("") && event.getDeathMessage().contains(p.getName())) {
                if(newMessage.equals("")) {
                    newMessage = message.replaceAll(p.getName(), p.getStringTemp("disguise"));
                } else {
                    newMessage = newMessage.replaceAll(p.getName(), p.getStringTemp("disguise"));
                }
            }
        }
        if(!newMessage.equals(""))
            event.setDeathMessage(newMessage);
    }

    private void checkForDebugListeningPlayers(DebugEvent event){
        if(!(PlayerManager.getInstance().getOnlinePlayers() == null)) {
            for(ServerPlayer player : PlayerManager.getInstance().getOnlinePlayers()) {
                if(player.getDebugListenTypeList().contains(event.getDebugType())) {
                    MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, event.getMessage());
                }
            }
        }
    }

    private void configurePlayerOnLogin(PlayerJoinEvent event){
        Player pl = event.getPlayer();
        if(PlayerManager.isNewPlayer(pl)) {
            PlayerManager.initNewPlayer(pl);
        }
        PlayerManager.getInstance().addPlayer(pl);
    }

    private void checkPlayerEnterRegion(ServerPlayerMoveEvent event){
        Player player = event.getPlayer();
        ServerRegion[] from = RegionManager.getInstance().getLocationInWorldRegions(event.getFrom());
        ServerRegion[] to = RegionManager.getInstance().getLocationInWorldRegions(event.getTo());

        if(from == null || to == null)
            return;

        HashMap<String, ServerRegion> new_regions = new HashMap<String, ServerRegion>();

        for(ServerRegion t : to) {
            for(ServerRegion f : from)
             // if we're already in that region, skip it
                if(f.name.equalsIgnoreCase(t.name))
                    continue;
            new_regions.put(t.name, t);
        }

        for(String regionName : new_regions.keySet()) {
            PlayerEnterRegionEvent e = new PlayerEnterRegionEvent(player, event.getTo(), event.getFrom(), event.getPreviousLocation(), regionName, new_regions.get(regionName));
            Bukkit.getServer().getPluginManager().callEvent(e);
        }
    }

    private void checkPlayerLeaveRegion(ServerPlayerMoveEvent event){
        Player player = event.getPlayer();
        ServerRegion[] from = RegionManager.getInstance().getLocationInWorldRegions(event.getFrom());
        ServerRegion[] to = RegionManager.getInstance().getLocationInWorldRegions(event.getTo());

        if(from == null || to == null)
            return;

        HashMap<String, ServerRegion> new_regions = new HashMap<String, ServerRegion>();

        for(ServerRegion f : from) {
            for(ServerRegion t : to)
             // if we're already in that region, skip it
                if(t.name.equalsIgnoreCase(f.name))
                    continue;
            new_regions.put(f.name, f);
        }

        for(String regionName : new_regions.keySet()) {
            PlayerLeaveRegionEvent e = new PlayerLeaveRegionEvent(player, event.getTo(), event.getFrom(), event.getPreviousLocation(), regionName, new_regions.get(regionName));
            Bukkit.getServer().getPluginManager().callEvent(e);
        }
    }

    private void configureNameOnLogin(PlayerJoinEvent event){
        PlayerHelper.refreshPlayerName(event.getPlayer());
    }

    private void playerGainBlockBreakStatistic(BlockBreakEvent event){
        StatisticType type = StatisticType.findStatisticTypeByListenType(event.getBlock().getType());
        String name = event.getPlayer().getName();
        if(type != null)
            type.increaseStat(name, 1);
    }

    private void playerGainBlockPlaceStatistic(BlockPlaceEvent event){
        StatisticType.BLOCKS_PLACED.increaseStat(event.getPlayer().getName(), 1);
    }

    private void playerGainDamageDealtStatistic(EntityDamageByEntityEvent event){
        if(event.getDamager().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getDamager();
            StatisticType.DAMAGE_DEALT.increaseStat(player.getName(), event.getDamage());
        }
    }

    private void playerGainDamageTakenStatistic(EntityDamageEvent event){
        if(event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            StatisticType.DAMAGE_TAKEN.increaseStat(player.getName(), event.getDamage());
        }
    }

    private void playerGainKillStatistic(EntityDeathEvent event){
        if(event.getEntity().getKiller() instanceof Player) {

            String killerName = event.getEntity().getKiller().getName();
            Entity entity = event.getEntity();
            EntityType eType = event.getEntityType();
            StatisticType type = StatisticType.findStatisticTypeByListenType(eType);

            if(type == null)
                return;

            if(type.listenFor == EntityType.ZOMBIE || type.listenFor == EntityType.SKELETON) {
                switch(eType){
                    case ZOMBIE:
                        Zombie zombie = (Zombie) event.getEntity();
                        if(zombie.isVillager() && type.name.equalsIgnoreCase("Zombie Villagers Killed"))
                            type.increaseStat(killerName, 1);
                        else
                            type.increaseStat(killerName, 1);
                        return;
                    case SKELETON:
                        Skeleton skeleton = (Skeleton) entity;
                        if(skeleton.getSkeletonType() == SkeletonType.WITHER && type.name.equalsIgnoreCase("Wither Skeletons Killed"))
                            type.increaseStat(killerName, 1);
                        else
                            type.increaseStat(killerName, 1);
                        return;
                    default:
                        break;
                }
            } else {
                type.increaseStat(killerName, 1);
            }
        }
    }

    private void playerGainTameStatistic(EntityTameEvent event){
        if(event.getOwner() instanceof Player) {

            Player player = (Player) event.getOwner();
            StatisticType type = StatisticType.findStatisticTypeByListenType(event.getEntityType());

            if(type != null)
                type.increaseStat(player.getName(), 1);
        }
    }

    private void removePlayerFromManagerOnLogout(PlayerQuitEvent event){
        PlayerManager.getInstance().removePlayer(event.getPlayer());
    }

    private void stupidLepper(PlayerJoinEvent event){
        if(PlayerHelper.getPlayerPermission(event.getPlayer()) == Permission.LEPPER) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, 0), true);
            event.getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
        }
    }

    private void addChatChannels(PlayerJoinEvent event){
        ChatChannel channel = new ChatChannel(event.getPlayer().getName().toLowerCase() + ".whisper");
        channel.addPlayer(event.getPlayer());
        ChatChannelManager.getInstance().addChannel(channel);

        channel = new ChatChannel(event.getPlayer().getName().toLowerCase() + ".local");
        ChatChannelManager.getInstance().addChannel(channel);

        channel = PlayerManager.getInstance().getPlayer(event.getPlayer()).getCurrentChannel();
        channel.addPlayer(event.getPlayer());
    }

    private void removeChatChannels(PlayerQuitEvent event){
        ChatChannelManager.getInstance().removeChannel(event.getPlayer().getName().toLowerCase() + ".whisper");
        ChatChannelManager.getInstance().removeChannel(event.getPlayer().getName().toLowerCase() + ".local");
    }

    private void chatSend(ProtocolManager protocolManager){
       final Thread mainThread = Thread.currentThread();
        protocolManager.getAsynchronousManager().registerAsyncHandler(new PacketAdapter(ServerPlugin.getInstance(), ConnectionSide.SERVER_SIDE, Packets.Server.CHAT) {
            @Override
            public void onPacketSending(final PacketEvent event){
                try {
                    if(Thread.currentThread().getId() == mainThread.getId()) {
                        PacketContainer packet = event.getPacket();
                        String message = packet.getStrings().read(0);
                        if(PlayerManager.getInstance().getPlayer(event.getPlayer()).isCensoringMessages()) {
                            if(ServerPlugin.blacklist != null) {
                                for(String s : ServerPlugin.blacklist) {
                                    packet = event.getPacket().shallowClone();
                                    event.setPacket(packet);
                                    if(StringUtils.containsIgnoreCase(message, s)) {
                                        String censor = "";
                                        for(int i = 0;i < s.length();i++)
                                            censor += "*";
                                        message = message.replaceAll("(?i)" + s, censor);
                                        packet.getStrings().write(0, message);
                                    }
                                }
                            }
                        }
                    }
                } catch(FieldAccessException e) {
                    e.printStackTrace();
                }
            }
        }).syncStart();
    }

    // private void preventFromEnteringRegion(PlayerEnterRegionEvent event,
    // String name){
    // if ( event.getRegionName().equalsIgnoreCase( name ) )
    // event.setCancelled( true );
    // }
    //
    // private void preventFromLeavingRegion(PlayerLeaveRegionEvent event,
    // String name){
    // if ( event.getRegionName().equalsIgnoreCase( name ) )
    // event.setCancelled( true );
    // }

    private void watchForServerPlayerMoveEvent(PlayerMoveEvent event){
        // get the player
        ServerPlayer player = PlayerManager.getInstance().getPlayer(event.getPlayer());

        // test the new block location
        if(PlayerHelper.isNewBlockLocation(player, event.getTo())) {

            ServerPlayerMoveEvent e = new ServerPlayerMoveEvent(player.getBukkitPlayer(), event.getFrom(), event.getTo());

            Bukkit.getServer().getPluginManager().callEvent(e);

            if(!e.isCancelled())
                PlayerHelper.updateBlockLocation(player);
        }
    }

    private void watchForRunningEvents(PlayerMoveEvent event){
        ServerPlayer sp = PlayerManager.getInstance().getPlayer(event.getPlayer());
        if(sp.isRunning() != event.getPlayer().isSprinting()) {
            if(sp.isRunning()) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerStopRunningEvent(event.getPlayer()));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerStartRunningEvent(event.getPlayer()));
            }
        }
    }
}
