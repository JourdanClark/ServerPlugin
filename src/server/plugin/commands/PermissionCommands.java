package server.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.SubCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.EnumHelper;
import server.plugin.common.PlayerHelper;
import server.plugin.event.PermissionEvent;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class PermissionCommands extends SubCommandExecutor {

    private String tag = "[Perms] ";

    public PermissionCommands(){
        super();

        this.commandName = "permission";

        commands.add(new SetClass());
        commands.add(new GetClass());
        commands.add(new ListClasses());

    }

    private class SetClass extends BaseCommand {

        public SetClass(){
            this.name = "set";
            this.description = "Set a players permission";
            this.arguments = "<player> <permission>";
            this.permission = Permission.JESTER;

            this.aliases.add("s");
        }

        public void onCommand(Player player, String[] args){
            if(args.length < 2) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.invalidNumberOfArguments());
                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, tag + "Usage: /permission set <player> <permission>");
            }

            Permission type = EnumHelper.getPlayerType(args[1]);

            if(type == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "'" + args[1] + "' is not a valid type!");
                return;
            }

            if(type == Permission.KING) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You can not set players to King status!");
                return;
            }

            if(PlayerHelper.getPlayerPermission(args[0]) == Permission.KING) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "Kings cannot have their permissions changed!");
                return;
            }

            PermissionEvent e = new PermissionEvent(args[0], type);
            Bukkit.getServer().getPluginManager().callEvent(e);

            if(!e.isCancelled()) {
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The permission has been set to '" + args[1] + "' for player '" + args[0] + "'");
            } else {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
            }

        }

    }

    private class GetClass extends BaseCommand {

        public GetClass(){
            this.name = "get";
            this.description = "Get what permissions a player has";
            this.arguments = "<name>";
            this.permission = Permission.JESTER;

            this.aliases.add("g");
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.invalidNumberOfArguments());
                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, tag + "Usage: /permission get <player>");
            }

            Permission type = PlayerHelper.getPlayerPermission(args[0]);

            if(type == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + args[0] + ": has the class '" + type.name + "'");

        }

    }

    private class ListClasses extends BaseCommand {

        public ListClasses(){
            this.name = "list";
            this.description = "List the different permissions";
            this.permission = Permission.JESTER;

            this.aliases.add("l");
        }

        public void onCommand(Player player, String[] args){
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "--- Player Permissions ---");
            int i = 1;
            for(Permission type : Permission.values()) {
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, i + ": " + type.color + type.name);
                i++;
            }
        }

    }
}
