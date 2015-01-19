package server.plugin.commands;

import java.util.HashMap;

import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.manager.MessageManager;
import server.plugin.manager.WorldManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class WorldCommands extends NormalCommandExecutor {

    private static String tag = "[Worlds] ";

    public WorldCommands(){
        commands.add(new list());
    }

    private class list extends BaseCommand {

        public list(){
            this.name = "list";
            this.description = "List all the worlds on the server";
            this.permission = Permission.JESTER;
            this.enableHelp = false;
        }

        @Override
        public void onCommand(Player player, String[] args){
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "--- World List ---");

            HashMap<String, String> worlds = WorldManager.getInstance().getWorlds();

            for(String worldName : worlds.keySet()) {
                if(WorldManager.getInstance().getWorld(worlds.get(worldName)).getGenerator() != null) {
                    MessageManager.getInstance().sendMessage(player, MessageType.INFO, worldName + tag + "- " + WorldManager.getInstance().getWorld(worlds.get(worldName)).getGenerator().toString());
                } else {
                    MessageManager.getInstance().sendMessage(player, MessageType.INFO, worldName + tag + "- Default Generator");
                }
            }
        }
    }
}
