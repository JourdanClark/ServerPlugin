package server.plugin.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.event.ServerPlayerTeleportEvent;
import server.plugin.types.Enums.ServerTeleportCause;

public class TeleportRequest {

    private String              player, playerTo;
    private ServerTeleportCause cause = ServerTeleportCause.ACCEPT_COMMAND;

    public TeleportRequest(Player player, Player playerTo){
        this.player = player.getName();
        this.playerTo = playerTo.getName();
    }

    public Player getPlayer(){
        return Bukkit.getServer().getPlayerExact(player);
    }

    public Player getPlayerTo(){
        return Bukkit.getServer().getPlayerExact(playerTo);
    }

    public Location getFrom(){
        return getPlayer().getLocation();
    }

    public Location getTo(){
        return getPlayerTo().getLocation();
    }

    public Location getTPATo(){
        return new Location(getPlayerTo().getWorld(), getPlayerTo().getLocation().getX(), getPlayerTo().getLocation().getY(), getPlayerTo().getLocation().getZ(), getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch());
    }

    public void fireTeleport(){
        if(getPlayer() == null || getPlayerTo() == null) {
            return;
        }
        Bukkit.getServer().getPluginManager().callEvent(new ServerPlayerTeleportEvent(getPlayer(), getPlayerTo(), getFrom(), getTPATo(), cause));
    }
}
