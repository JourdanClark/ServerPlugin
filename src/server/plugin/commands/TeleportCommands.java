package server.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.PlayerHelper;
import server.plugin.event.ServerPlayerTeleportEvent;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.manager.TeleportManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.Enums.ServerTeleportCause;
import server.plugin.types.ServerPlayer;
import server.plugin.types.TeleportRequest;

public class TeleportCommands extends NormalCommandExecutor {

    private String tag                = "[Teleport] ";
    private String cannotTpSelf       = tag + "You can not teleport to yourself";
    private String mustSpecifiyPlayer = tag + "You must specify a player";
    private String noPending          = tag + "You do not have any pending requests";

    public TeleportCommands(){
        commands.add(new tpa());
        commands.add(new tpahere());
        commands.add(new tpaccept());
        commands.add(new tpdeny());
        commands.add(new tp());
        commands.add(new tphere());
    }

    private class tpa extends BaseCommand {

        public tpa(){
            this.name = "tpa";
            this.description = "Request to teleport to a player";
            this.arguments = "<name>";
            this.permission = Permission.TRAVELER;
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, mustSpecifiyPlayer);
                return;
            }

            ServerPlayer target = PlayerManager.getInstance().getPlayer(args[0]);

            if(target == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            if(target.getName() == player.getName()) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, cannotTpSelf);
                return;
            }

            TeleportManager.getInstance().addTPARequest(target.getBukkitPlayer(), new TeleportRequest(player, target.getBukkitPlayer()));

            MessageManager.getInstance().sendMessage(target, MessageType.GOOD, tag + PlayerHelper.checkDisguise(player) + " would like to teleport to you");
            MessageManager.getInstance().sendMessage(target, MessageType.SUBINFO, tag + ChatColor.GREEN + "Accept " + ChatColor.WHITE + "with: /tpaccept");
            MessageManager.getInstance().sendMessage(target, MessageType.SUBINFO, tag + ChatColor.RED + "Decline " + ChatColor.WHITE + "with: /tpdeny");

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "Request sent to " + PlayerHelper.checkDisguise(target.getName()));
        }

    }

    private class tpahere extends BaseCommand {

        public tpahere(){
            this.name = "tpahere";
            this.description = "Request a player to teleport to you";
            this.arguments = "<name>";
            this.permission = Permission.TRAVELER;
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, mustSpecifiyPlayer);
                return;
            }

            // Player target = Bukkit.getPlayer( args[0] );
            ServerPlayer target = PlayerManager.getInstance().getPlayer(args[0]);

            if(target == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            if(target.getName() == player.getName()) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, cannotTpSelf);
                return;
            }

            TeleportManager.getInstance().addTPARequest(target.getBukkitPlayer(), new TeleportRequest(target.getBukkitPlayer(), player));

            MessageManager.getInstance().sendMessage(target, MessageType.GOOD, tag + PlayerHelper.checkDisguise(player) + " would like you to teleport to them");
            MessageManager.getInstance().sendMessage(target, MessageType.SUBINFO, tag + ChatColor.GREEN + "Accept " + ChatColor.WHITE + "with: /tpaccept");
            MessageManager.getInstance().sendMessage(target, MessageType.SUBINFO, tag + ChatColor.RED + "Decline " + ChatColor.WHITE + "with: /tpdeny");

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "Request sent to " + PlayerHelper.checkDisguise(target.getName()));
        }

    }

    private class tpaccept extends BaseCommand {

        public tpaccept(){
            this.name = "tpaccept";
            this.description = "Accept a teleport request";
            this.permission = Permission.LEPPER;
        }

        public void onCommand(Player player, String[] args){

            if(TeleportManager.getInstance().getTPARequest(player) == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, noPending);
                return;
            }

            TeleportRequest request = TeleportManager.getInstance().getTPARequest(player);
            Player p = request.getPlayer();
            Player pTo = request.getPlayerTo();

            request.fireTeleport();

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "Teleport request accepted");
            MessageManager.getInstance().sendMessage(p, MessageType.CUSTOM, ChatColor.GRAY + tag + "You have been teleported to " + PlayerHelper.checkDisguise(pTo.getName()));
            MessageManager.getInstance().sendMessage(pTo, MessageType.CUSTOM, ChatColor.GRAY + tag + PlayerHelper.checkDisguise(p.getName()) + " has been teleported to you");

            TeleportManager.getInstance().removeTPARequest(player);
        }

    }

    private class tpdeny extends BaseCommand {

        public tpdeny(){
            this.name = "tpdeny";
            this.description = "Deny a teleport request";
            this.permission = Permission.TRAVELER;
        }

        public void onCommand(Player player, String[] args){
            if(TeleportManager.getInstance().getTPARequest(player) == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, noPending);
                return;
            }

            TeleportManager.getInstance().removeTPARequest(player);
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "Teleport request denied");
        }

    }

    private class tp extends BaseCommand {

        public tp(){
            this.name = "tp";
            this.description = "Teleports you to a player without sending a request";
            this.permission = Permission.KNIGHT;
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must specify a player to teleport");
                return;
            }

            if(args.length == 2) { // if they do /tp bob jeff
                ServerPlayer bp = PlayerManager.getInstance().getPlayer(args[0]); // bob
                if(bp != null) {
                    String[] newArgs = {args[1]}; // jeff
                    ServerPlayer target = PlayerManager.getInstance().getPlayer(newArgs[0]);
                    if(target != null) {
                        onCommand(bp.getBukkitPlayer(), newArgs);
                        return;
                    } else {
                        MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(newArgs[0]));
                        return;
                    }
                } else {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
                    return;
                }
            }

            ServerPlayer target = PlayerManager.getInstance().getPlayer(args[0]);

            if(target == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            if(target.getName() == player.getName()) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, cannotTpSelf);
                return;
            }

            Player p = target.getBukkitPlayer();

            Location loc = (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
            Bukkit.getServer().getPluginManager().callEvent(new ServerPlayerTeleportEvent(player, p.getLocation(), loc, ServerTeleportCause.DEMAND_COMMAND));
            MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.GRAY + tag + "You have teleported to " + PlayerHelper.checkDisguise(p));
        }

    }

    private class tphere extends BaseCommand {

        public tphere(){
            this.name = "tphere";
            this.description = "Teleports a player to you without sending a request";
            this.permission = Permission.KNIGHT;
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must specify a player to teleport");
                return;
            }
            ServerPlayer target = PlayerManager.getInstance().getPlayer(args[0]);

            if(target == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            if(target.getName() == player.getName()) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, cannotTpSelf);
                return;
            }

            Player p = target.getBukkitPlayer();

            Location loc = (new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
            Bukkit.getServer().getPluginManager().callEvent(new ServerPlayerTeleportEvent(p, player.getLocation(), loc, ServerTeleportCause.DEMAND_COMMAND));
            MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.GRAY + tag + PlayerHelper.checkDisguise(target.getName()) + " has teleported to you");
        }

    }
}
