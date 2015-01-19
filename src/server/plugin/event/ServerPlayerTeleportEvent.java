package server.plugin.event;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.common.HelperFunctions;
import server.plugin.manager.ConfigManager;
import server.plugin.manager.CooldownManager;
import server.plugin.manager.MessageManager;
import server.plugin.types.Cooldown;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.ServerTeleportCause;

public class ServerPlayerTeleportEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private ServerTeleportCause       cause     = ServerTeleportCause.UNKNOWN;
    private HashMap<String, Object[]> locations = new HashMap<String, Object[]>();
    private String                    playerToName, playerName;

    private boolean                   cancel    = false;

    public ServerPlayerTeleportEvent(Player player, Location from, Location to){
        this.playerName = player.getName();

        locations.put("from", HelperFunctions.getArrayFromLocation(from, 4));
        locations.put("to", HelperFunctions.getArrayFromLocation(to, 4));

        teleportToDestination();
    }

    public ServerPlayerTeleportEvent(Player player, Location from, Location to, ServerTeleportCause cause){
        this.playerName = player.getName();
        this.cause = cause;

        locations.put("from", HelperFunctions.getArrayFromLocation(from, 4));
        locations.put("to", HelperFunctions.getArrayFromLocation(to, 4));

        teleportToDestination();
    }

    public ServerPlayerTeleportEvent(Player player, Player playerTo, Location from, Location to){
        this.playerName = player.getName();
        this.playerToName = playerTo.getName();

        locations.put("from", HelperFunctions.getArrayFromLocation(from, 4));
        locations.put("to", HelperFunctions.getArrayFromLocation(to, 4));

        teleportToDestination();
    }

    public ServerPlayerTeleportEvent(Player player, Player playerTo, Location from, Location to, ServerTeleportCause cause){
        this.playerName = player.getName();
        this.playerToName = player.getName();
        this.cause = cause;

        locations.put("from", HelperFunctions.getArrayFromLocation(from, 4));
        locations.put("to", HelperFunctions.getArrayFromLocation(to, 4));

        teleportToDestination();
    }

    /**
     * @return Returns the ServerTeleportCause for the event. 'UNKNOWN' if no
     *         cause was given.
     */
    public ServerTeleportCause getCause(){
        return cause;
    }

    /**
     * @return Returns the Location before the event.
     */
    public Location getFrom(){
        return HelperFunctions.createLocationFromPoints(locations.get("from"), getPlayer());
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * @return Returns the Player involved in the event.
     */
    public Player getPlayer(){
        return Bukkit.getServer().getPlayerExact(playerName);
    }

    /**
     * @return Returns the Player being teleported to in the event if the event
     *         was triggered by a tpa.
     */
    public Player getPlayerTo(){
        return Bukkit.getServer().getPlayerExact(playerToName);
    }

    /**
     * @return Returns the Location after the event.
     */
    public Location getTo(){
        return HelperFunctions.createLocationFromPoints(locations.get("to"), getPlayer());
    }

    /**
     * @return Returns true if the event was cancelled.
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * Cancels the event if true.
     * 
     * @param cancel Whether or not to cancel the event.
     */
    public void setCancelled(boolean cancel){
        this.cancel = cancel;
    }

    /**
     * Teleports the player to their destination if the event wasn't cancelled.
     */
    private void teleportToDestination(){
        if(!cancel) {
            if(cause == ServerTeleportCause.ACCEPT_COMMAND) {
                Cooldown cooldown = CooldownManager.getInstance().getCooldown(getPlayer().getName(), "tpa");
                if(cooldown != null) {
                    MessageManager.getInstance().sendMessage(getPlayer(), MessageType.SEVERE, "[Teleport] You cannot tpa for " + cooldown.toString());
                    return;
                }
                CooldownManager.getInstance().addCooldown(getPlayer(), "tpa", ConfigManager.getInstance().getConfig("Config Files.misc").getLong("tpa.cooldown"));
            }
            getPlayer().teleport(getTo());
        }
    }

}
