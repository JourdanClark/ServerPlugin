package server.plugin.commands.baseclasses;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import server.plugin.common.CommonMessages;
import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class SubCommandExecutor extends ServerCommandExecutor {

    protected String commandName = "default";

    /**
     * Call super() when extending this class if you want to include the help
     * command
     */
    public SubCommandExecutor(){
        commands.add(new CommandHelp());
    }

    /**
     * Public accessor function for the command name
     * 
     * @return commandName
     */
    public String getCommandName(){
        return this.commandName;
    }

    /**
     * Pass the sub command down to its proper handler, after checking
     * permission
     * 
     * @param player
     * @param args
     */
    public void handleSubCommand(Player player, String[] args){
        if(args.length == 0) {
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "--- " + commandName + " Commands ---");
            for(BaseCommand c : commands) {
                if(c.getPermission().isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player)))
                    MessageManager.getInstance().sendMessage(player, MessageType.INFO, "/" + commandName + " " + c.getName() + " " + c.getArguments());
            }
            return;
        }

        BaseCommand sub = getSubCommand(args[0], true);

        if(sub == null) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "'" + args[0] + "' is not a valid subcommand");
            return;
        }

        if(!sub.getPermission().isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player))) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidPermissions());
            return;
        }

        // Remove the sub-command from the args
        args = Arrays.copyOfRange(args, 1, args.length);

        try {
            sub.onCommand(player, args);
        } catch(Exception e) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "An error has occured: " + e.getCause());
            e.printStackTrace();
        }

    }

    /**
     * Get the command or SUB-command from the 'commands' list
     * 
     * @param name
     * @param aliases
     * @return
     */
    protected BaseCommand getSubCommand(String name, boolean checkAliases){
        for(BaseCommand cmd : commands) {
            if(cmd.getName() != null && cmd.getName().equalsIgnoreCase(name))
                return cmd;
            if(checkAliases && cmd.getAliases() != null && cmd.getAliases().contains(name))
                return cmd;
        }
        return null;
    }

    /**
     * Return a SUB-command's aliases in a string
     * 
     * @param cmd
     * @return
     */
    protected String getCommandAliases(BaseCommand cmd){
        String aliases = "";

        for(String alias : cmd.getAliases()) {
            aliases += alias + " | ";
        }

        return aliases.substring(0, aliases.lastIndexOf("| "));
    }

    /**
     * Basic help command thats dynamic and will offer help to your different
     * sub commands
     */
    private class CommandHelp extends BaseCommand {

        public CommandHelp(){
            this.name = "help";
            this.arguments = "<commandName>";
            this.description = "Provides help for the sub commands";
            this.permission = Permission.LEPPER;

            this.aliases.add("h");
        }

        public void onCommand(Player p, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(p, MessageType.INFO, "/" + commandName + " " + name + " " + arguments);
                MessageManager.getInstance().sendMessage(p, MessageType.INFO, "  " + ChatColor.ITALIC + description);
                return;
            }

            BaseCommand command = getSubCommand(args[0], false);

            if(command == null) {
                MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "No sub command named '" + args[0] + "'");
                return;
            }

            if(!command.getPermission().isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(p))) {
                MessageManager.getInstance().sendMessage(p, MessageType.SEVERE, "You do not have the required permissions for that command");
                return;
            }

            MessageManager.getInstance().sendMessage(p, MessageType.INFO, "/" + commandName + " " + command.getName() + " " + command.getArguments());
            MessageManager.getInstance().sendMessage(p, MessageType.INFO, "  Aliases: " + ChatColor.ITALIC + getCommandAliases(command));
            MessageManager.getInstance().sendMessage(p, MessageType.INFO, "  " + ChatColor.ITALIC + command.getDescription());
        }

    }
}
