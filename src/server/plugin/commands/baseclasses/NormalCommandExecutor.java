package server.plugin.commands.baseclasses;

import org.bukkit.entity.Player;

import server.plugin.common.CommonMessages;
import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;

public class NormalCommandExecutor extends ServerCommandExecutor {

    protected NormalCommandExecutor(){
    }
    
    /**
     * Runs the command.
     * @param player The player who called the command.
     * @param commandName The name of the command used.
     * @param args The arguments provided when the command was called.
     */
    public void handleCommand(Player player, String commandName, String[] args){

        BaseCommand command = getCommand(commandName, true);

        if(command == null) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "'" + commandName + "' is not a valid command. How the hell did we get here?");
            return;
        }

        if(!command.getPermission().isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player))) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidPermissions());
            return;
        }

        if(args.length >= 1 && command.enableHelp) {
            if(args[0].equalsIgnoreCase("help")) {
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, command.description);
                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, "Usage: /" + command.name + " " + command.arguments);
                if(command.aliases.size() > 0)
                    MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, "Aliases: " + command.aliases.toString());
                return;
            }
        }

        try {
            command.onCommand(player, args);
        } catch(Exception e) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "An error has occured: " + e.getCause());
            e.printStackTrace();
        }

    }

    /**
     * @param name The name of the command run.
     * @param checkAliases Checks to see if the command has aliases.
     * @return Returns the command used.
     */
    protected BaseCommand getCommand(String name, boolean checkAliases){
        for(BaseCommand cmd : commands) {
            if(cmd.getName() != null && cmd.getName().equalsIgnoreCase(name))
                return cmd;
            if(checkAliases && cmd.getAliases() != null && cmd.getAliases().contains(name))
                return cmd;
        }
        return null;
    }
}
