package server.plugin.event;

import net.minecraft.server.v1_6_R3.Packet62NamedSoundEffect;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.manager.PlayerManager;

public class PlayerStopRunningEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String                   player;

    public PlayerStopRunningEvent(Player player){
        this.player = player.getName();
        PlayerManager.getInstance().getPlayer(getPlayer()).setRunning(false);
        Packet62NamedSoundEffect packet = new Packet62NamedSoundEffect("custom.nikki", getPlayer().getLocation().getBlockX(), getPlayer().getLocation().getBlockY(), getPlayer().getLocation().getBlockZ(), 1.0F, 1.0F);
        ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
    }

    public Player getPlayer(){
        return Bukkit.getPlayerExact(player);
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

}
