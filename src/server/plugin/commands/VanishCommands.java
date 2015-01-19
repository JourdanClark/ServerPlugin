package server.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class VanishCommands extends NormalCommandExecutor {
    private String tag = "[Vanish] ";

    public VanishCommands(){
        commands.add(new vanish());
    }

    private class vanish extends BaseCommand {

        public vanish(){
            this.name = "vanish";
            this.description = "Hides you from everyone who isn't an admin";
            this.permission = Permission.KNIGHT;
            this.aliases.add("hide");
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length >= 1) {
                if(args[0].equals("-admin"))
                    ;
                if(onAdminCommand(player, args))
                    return;
            }
            if(!PlayerManager.getInstance().getPlayer(player).isVanished()) {
                for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
                    if(permission.isLessThanOrEqual(PlayerHelper.getPlayerPermission(p.getBukkitPlayer()))) {
                        p.getBukkitPlayer().hidePlayer(player);
                    }
                }
                PlayerManager.getInstance().getPlayer(player).setHidden(true);
                MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.BLUE + tag + "You are now hidden");
            } else {
                for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
                    p.getBukkitPlayer().showPlayer(player);
                }
                PlayerManager.getInstance().getPlayer(player).setHidden(false);
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You are no longer hidden");
            }
        }

        public boolean onAdminCommand(Player player, String[] args){
            if(!Permission.JESTER.isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player)))
                return false;

            if(!PlayerManager.getInstance().getPlayer(player).isVanished()) {
                for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
                    if(Permission.JESTER.isLessThanOrEqual(PlayerHelper.getPlayerPermission(p.getBukkitPlayer()))) {
                        p.getBukkitPlayer().hidePlayer(player);
                    }
                }
                PlayerManager.getInstance().getPlayer(player).setHidden(true);
                MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.BLUE + tag + "You are now from everyone except da cool mofos");
            } else {
                for(ServerPlayer p : PlayerManager.getInstance().getOnlinePlayers()) {
                    p.getBukkitPlayer().showPlayer(player);
                }
                PlayerManager.getInstance().getPlayer(player).setHidden(false);
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You are no longer hidden");

            }
            return true;
        }
    }
}
