package server.plugin.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.manager.LocationManager;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class WarpCommands extends NormalCommandExecutor {
    private String tag = "[Warp] ";

    public WarpCommands(){
        commands.add(new warp());
    }

    private class warp extends BaseCommand {

        public warp(){
            this.name = "warp";
            this.description = "Warps you to a location";
            this.permission = Permission.KNIGHT;
        }

        @Override
        public void onCommand(Player player, String[] args){
            Location loc = LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]);

            if(args.length > 0) {
                if(loc == null) {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "Warp does not exist");
                    return;
                }
                player.teleport(loc);
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "You have warped to " + args[0]);
                return;
            }
        }

    }
}
