package server.plugin.commands;

import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.SubCommandExecutor;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.DebugType;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class ServerCommands extends SubCommandExecutor {

    public ServerCommands(){
        super();

        this.commandName = "Server";

        commands.add(new PlayerDebugOptions());
    }

    private class PlayerDebugOptions extends BaseCommand {

        public PlayerDebugOptions(){
            this.name = "debug";
            this.description = "Toggle debug for a given type\n  Types: <none|all|file|minigame|debug|general|unknown>";
            this.arguments = "<type>";
            this.permission = Permission.MAGE;

            this.aliases.add("d");
        }

        private DebugType getDebugType(String type){
            switch(type.toLowerCase()){
                case "file":
                    return DebugType.FILE;
                case "general":
                    return DebugType.GENERAL;
                case "unknown":
                    return DebugType.UNKNOWN;
                case "minigame":
                    return DebugType.MINIGAME;
                default:
                    return null;
            }
        }

        public void onCommand(Player p, String[] args){

            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "[Debug] You must provide a debug type");
                return;
            }

            DebugType type = getDebugType(args[0]);

            if(type == null && !(args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("none"))) {
                MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "[Debug] Invalid debug type!");
                MessageManager.getInstance().sendMessage(p, MessageType.INFO, "[Debug]   Types: <none|all|file|minigame|debug|general|unknown>");
                return;
            }

            ServerPlayer player = PlayerManager.getInstance().getPlayer(p);

            if(type != null) {
                if(player.getDebugListenTypeList().contains(type)) {
                    player.removeDebugListenType(type);
                    MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "[Debug] You're no longer listening to '" + args[0] + "' debug messages");
                } else {
                    player.addDebugListenType(type);
                    MessageManager.getInstance().sendMessage(p, MessageType.GOOD, "[Debug] You're now listening to '" + args[0] + "' debug messages");
                }

                return;
            }

            if(args[0].equalsIgnoreCase("all")) {
                player.getDebugListenTypeList().clear();
                for(DebugType t : DebugType.values()) {
                    player.addDebugListenType(t);
                }
                MessageManager.getInstance().sendMessage(p, MessageType.GOOD, "[Debug] You are now listening all debug messages");
                return;
            }

            if(args[0].equalsIgnoreCase("none")) {
                player.getDebugListenTypeList().clear();
                MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "[Debug] You are no longer listening to debug messages");
                return;
            }

            MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "[Niga] daffuq?");

        }

    }

}
