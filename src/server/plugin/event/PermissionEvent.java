package server.plugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.kitteh.tag.TagAPI;

import server.plugin.common.CommonMessages;
import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class PermissionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private String     name;
    private Permission type;

    private boolean    cancel = false;

    public PermissionEvent(Player player, Permission type){
        name = player.getName();
        this.type = type;

        setPermission();
    }

    public PermissionEvent(String playerName, Permission type){
        name = playerName;
        this.type = type;

        setPermission();
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public String getPlayerName(){
        return name;
    }

    public Permission getType(){
        return type;
    }

    public boolean isCancelled(){
        return cancel;
    }

    public void setCancelled(boolean cancel){
        this.cancel = cancel;
    }

    private void setPermission(){
        if(!cancel) {
            Player p = Bukkit.getPlayerExact(name);
            PlayerHelper.setPlayerPermission(name, type);
            if(p != null) {
                MessageManager.getInstance().sendMessage(p, MessageType.GOOD, CommonMessages.classChangeInform(type));
                boolean isAtLeastKnight = Permission.KNIGHT.isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(p));
                PlayerHelper.refreshPlayerName(p);
                for(ServerPlayer sp : PlayerManager.getInstance().getOnlinePlayers()) {
                    if(isAtLeastKnight) {
                        sp.getBukkitPlayer().showPlayer(p);
                    }
                    TagAPI.refreshPlayer(sp.getBukkitPlayer());
                }
            }
        }
    }
}
