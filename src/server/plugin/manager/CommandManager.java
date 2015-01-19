package server.plugin.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import server.plugin.commands.ChatCommands;
import server.plugin.commands.DisguiseCommands;
import server.plugin.commands.EnforcementCommands;
import server.plugin.commands.LocationCommands;
import server.plugin.commands.PermissionCommands;
import server.plugin.commands.PlayerSettingCommands;
import server.plugin.commands.RegionCommands;
import server.plugin.commands.ServerCommands;
import server.plugin.commands.TeleportCommands;
import server.plugin.commands.VanillaCommands;
import server.plugin.commands.VanishCommands;
import server.plugin.commands.WarpCommands;
import server.plugin.commands.WhisperCommands;
import server.plugin.commands.WorldCommands;
import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.GroupCommandExecutor;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.commands.baseclasses.ServerCommandExecutor;
import server.plugin.commands.baseclasses.SubCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.PlayerHelper;
import server.plugin.event.PermissionEvent;
import server.plugin.filetype.YamlFile;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class CommandManager implements Listener {

    private HashMap<String, Permission>      whiteList = new HashMap<String, Permission>();

    private ArrayList<ServerCommandExecutor> executors = new ArrayList<ServerCommandExecutor>();

    private static CommandManager            instance  = new CommandManager();

    public static CommandManager getInstance(){
        return instance;
    }

    private CommandManager(){

        // Initialize the executors
        executors.add(new DisguiseCommands());
        executors.add(new LocationCommands());
        executors.add(new RegionCommands());
        executors.add(new PermissionCommands());
        executors.add(new VanillaCommands());
        executors.add(new TeleportCommands());
        executors.add(new ServerCommands());
        executors.add(new WhisperCommands());
        executors.add(new VanishCommands());
        executors.add(new WarpCommands());
        executors.add(new EnforcementCommands());
        executors.add(new ChatCommands());
        executors.add(new PlayerSettingCommands());
        executors.add(new WorldCommands());

        initWhiteList();
    }

    public void onPermissionChange(PermissionEvent event){
        if(event.getType().isGreaterThanOrEqual(Permission.MAGE)) {
            YamlFile wepif = new YamlFile(new File("wepif.yml"));
            wepif.set("permissions.users." + event.getPlayerName() + ".groups", "mage");
        } else {
            YamlFile wepif = new YamlFile(new File("wepif.yml"));
            wepif.set("permissions.users." + event.getPlayerName() + ".groups", "None");
        }
    }

    public void CommandPreprocesser(PlayerCommandPreprocessEvent event){

        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage();
        String commandName = getCommandFromMessage(message).toLowerCase();

        // If the command is white listed, and the player meets the permissions
        // for it, send it through
        if(whiteList.containsKey(commandName)) {
            // if the player meets the permissions for the white listed command,
            // send it through
            if(whiteList.get(commandName).isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player))) {
                event.setCancelled(false);
                return;
            } else if(!whiteList.get(commandName).isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player))) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidPermissions());
                return;
            }
            return;
        }

        commandName = commandName.substring(1, commandName.length());

        // Get the executor for the command, ignoring the prefix
        ServerCommandExecutor executor = getCommandExecutor(player, commandName);

        if(executor == null) {
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.unknownCommand());
            return;
        }

        String[] args = Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length);

        if(executor instanceof SubCommandExecutor) {
            // Take off the first two items /region create <args we want>
            ((SubCommandExecutor) executor).handleSubCommand(player, args);
            return;
        }

        if(executor instanceof NormalCommandExecutor) {
            // Take off the first item /tpa <args we want>
            ((NormalCommandExecutor) executor).handleCommand(player, commandName, args);
            return;
        }

    }

    private ServerCommandExecutor getCommandExecutor(Player player, String commandName){
        commandName = commandName.toLowerCase();
        for(ServerCommandExecutor executor : executors) {
            if(executor instanceof SubCommandExecutor) {
                if(((SubCommandExecutor) executor).getCommandName().equalsIgnoreCase(commandName))
                    return executor;
            }

            if(executor instanceof NormalCommandExecutor) {
                for(BaseCommand command : executor.commands) {
                    if(command.getName().equalsIgnoreCase(commandName) || command.getAliases().contains(commandName))
                        return executor;
                }
            }

            if(executor instanceof GroupCommandExecutor) {
                GroupCommandExecutor group = (GroupCommandExecutor) executor;
                for(ServerCommandExecutor e : group.executors) {
                    if(e instanceof NormalCommandExecutor) {
                        for(BaseCommand command : e.commands) {
                            if(command.getName().equalsIgnoreCase(commandName) || command.getAliases().contains(commandName))
                                return e;
                        }
                    }
                    if(e instanceof SubCommandExecutor) {
                        if(((SubCommandExecutor) e).getCommandName().equalsIgnoreCase(commandName))
                            return e;
                    }
                }
            }
        }
        return null;
    }

    // private BaseCommand getCommand( String commandName ) {
    //
    // for ( ServerCommandExecutor executor : executors ) {
    // for ( BaseCommand command : executor.commands ) {
    // if ( command.name.equalsIgnoreCase( commandName ) ) return command;
    // }
    // }
    //
    // return null;
    // }
    
    public String getHelpMessage(){
        String message = "Available commands:\n";
        for(ServerCommandExecutor e : executors)
            for(BaseCommand b : e.commands)
                if(!b.getName().equals("help"))
                    message += "/" + b.getName() + "\n";
        return message += "Use /help <command> for more info";
    }

    /**
     * Takes a string, and returns the text before the first whitespace
     * 
     * @param message
     * @return command
     */
    private String getCommandFromMessage(String message){
        String[] split = message.split(" ");

        if(split.length > 0)
            return split[0];

        return message;
    }

    private void initWhiteList(){
        whiteListGeneral();
        whiteListWorldEdit();
    }

    private void whiteListGeneral(){
        // whiteList.put( "/gamemode", PlayerType.MAGE );
        whiteList.put("/stop", Permission.KING);
    }

    private void whiteListWorldEdit(){
        // http://wiki.sk89q.com/wiki/WorldEdit/Permissions
        whiteList.put("//copy", Permission.MAGE);
        whiteList.put("//paste", Permission.MAGE);
        whiteList.put("//cut", Permission.MAGE);
        whiteList.put("//hcyl", Permission.MAGE);
        whiteList.put("//cyl", Permission.MAGE);
        whiteList.put("//hsphere", Permission.MAGE);
        whiteList.put("//sphere", Permission.MAGE);
        whiteList.put("//pyramid", Permission.MAGE);
        whiteList.put("//hpyramid", Permission.MAGE);
        whiteList.put("//walls", Permission.MAGE);
        whiteList.put("/brush", Permission.MAGE);
        whiteList.put("/br", Permission.MAGE);
        whiteList.put("/unstuck", Permission.PALADIN);
        whiteList.put("/!", Permission.MAGE);
        whiteList.put("/ascend", Permission.PALADIN);
        whiteList.put("/asc", Permission.PALADIN);
        whiteList.put("/descend", Permission.PALADIN);
        whiteList.put("/desc", Permission.PALADIN);
        whiteList.put("/jumpto", Permission.PALADIN);
        whiteList.put("/j", Permission.PALADIN);
        whiteList.put("/ceil", Permission.MAGE);
        whiteList.put("/up", Permission.MAGE);
        whiteList.put("//redo", Permission.MAGE);
        whiteList.put("/redo", Permission.MAGE);
        whiteList.put("//undo", Permission.MAGE);
        whiteList.put("/undo", Permission.MAGE);
        whiteList.put("//sel", Permission.MAGE);
        whiteList.put("/tree", Permission.MAGE);
        whiteList.put("/none", Permission.MAGE);
        whiteList.put("//snow", Permission.MAGE);
        whiteList.put("/snow", Permission.MAGE);
        whiteList.put("//thaw", Permission.MAGE);
        whiteList.put("/thaw", Permission.MAGE);
        whiteList.put("//pos1", Permission.MAGE);
        whiteList.put("//pos2", Permission.MAGE);
        whiteList.put("//wand", Permission.MAGE);
        whiteList.put("//hpos1", Permission.MAGE);
        whiteList.put("//hpos2", Permission.MAGE);
        whiteList.put("//ex", Permission.MAGE);
        whiteList.put("//ext", Permission.MAGE);
        whiteList.put("//extinguish", Permission.MAGE);
        whiteList.put("/ex", Permission.MAGE);
        whiteList.put("/ext", Permission.MAGE);
        whiteList.put("/extinguish", Permission.MAGE);
        whiteList.put("/butcher", Permission.MAGE);
        whiteList.put("//fill", Permission.MAGE);
        whiteList.put("//help", Permission.MAGE);
        whiteList.put("//green", Permission.MAGE);
        whiteList.put("/green", Permission.MAGE);
        whiteList.put("//drain", Permission.MAGE);
        whiteList.put("/clearclipboard", Permission.MAGE);
        whiteList.put("//rotate", Permission.MAGE);
        whiteList.put("//flip", Permission.MAGE);
        whiteList.put("//hollow", Permission.MAGE);
        whiteList.put("/superpickaxe", Permission.MAGE);
        whiteList.put("/pickaxe", Permission.MAGE);
        whiteList.put("//", Permission.MAGE);
        whiteList.put("/,", Permission.MAGE);
        whiteList.put("/sp", Permission.MAGE);
        whiteList.put("//outset", Permission.MAGE);
        whiteList.put("//inset", Permission.MAGE);
        whiteList.put("/remove", Permission.MAGE);
        whiteList.put("/rem", Permission.MAGE);
        whiteList.put("/rement", Permission.MAGE);
        whiteList.put("//fixlava", Permission.MAGE);
        whiteList.put("/fixlava", Permission.MAGE);
        whiteList.put("//searchitem", Permission.MAGE);
        whiteList.put("//i", Permission.MAGE);
        whiteList.put("//search", Permission.MAGE);
        whiteList.put("/searchitem", Permission.MAGE);
        whiteList.put("//faces", Permission.MAGE);
        whiteList.put("//outline", Permission.MAGE);
        whiteList.put("//smooth", Permission.MAGE);
        whiteList.put("/tool", Permission.MAGE);
        whiteList.put("/mat", Permission.MAGE);
        whiteList.put("/material", Permission.MAGE);
        whiteList.put("/fill", Permission.MAGE);
        whiteList.put("/size", Permission.MAGE);
        whiteList.put("/mask", Permission.MAGE);
        whiteList.put("/forestgen", Permission.MAGE);
        whiteList.put("/pumpkins", Permission.MAGE);
        whiteList.put("/biomelist", Permission.MAGE);
        whiteList.put("/biomels", Permission.MAGE);
        whiteList.put("/biomeinfo", Permission.MAGE);
        whiteList.put("//setbiome", Permission.MAGE);
        whiteList.put("/chunkinfo", Permission.MAGE);
        whiteList.put("/listchunks", Permission.MAGE);
        whiteList.put("/delchunks", Permission.MAGE);
        whiteList.put("//schematic", Permission.MAGE);
        whiteList.put("//schem", Permission.MAGE);
        whiteList.put("//load", Permission.MAGE);
        whiteList.put("/worldedit", Permission.MAGE);
        whiteList.put("/we", Permission.MAGE);
        whiteList.put("//fast", Permission.MAGE);
        whiteList.put("//toggleplace", Permission.MAGE);
        whiteList.put("/toggleplace", Permission.MAGE);
        whiteList.put("//limit", Permission.MAGE);
        whiteList.put("//gmask", Permission.MAGE);
        whiteList.put("/gmask", Permission.MAGE);
        whiteList.put("//generate", Permission.MAGE);
        whiteList.put("//gen", Permission.MAGE);
        whiteList.put("//g", Permission.MAGE);
        whiteList.put("//clearhistory", Permission.MAGE);
        whiteList.put("/clearhistory", Permission.MAGE);
        whiteList.put("/thru", Permission.MAGE);
        whiteList.put("//overlay", Permission.MAGE);
        whiteList.put("//naturalize", Permission.MAGE);
        whiteList.put("/regen", Permission.MAGE);
        whiteList.put("//deform", Permission.MAGE);
        whiteList.put("//replace", Permission.MAGE);
        whiteList.put("//re", Permission.MAGE);
        whiteList.put("//rep", Permission.MAGE);
        whiteList.put("//stack", Permission.MAGE);
        whiteList.put("//set", Permission.MAGE);
        whiteList.put("//move", Permission.MAGE);
        whiteList.put("//center", Permission.MAGE);
        whiteList.put("//middle", Permission.MAGE);
        whiteList.put("/.s", Permission.MAGE);
        whiteList.put("/cs", Permission.MAGE);
        whiteList.put("//chunk", Permission.MAGE);
        whiteList.put("/toggleeditwand", Permission.MAGE);
        whiteList.put("//contract", Permission.MAGE);
        whiteList.put("//distr", Permission.MAGE);
        whiteList.put("//desel", Permission.MAGE);
        whiteList.put("//deselect", Permission.MAGE);
        whiteList.put("//count", Permission.MAGE);
        whiteList.put("//size", Permission.MAGE);
        whiteList.put("//expand", Permission.MAGE);
        whiteList.put("//shift", Permission.MAGE);
        whiteList.put("//;", Permission.MAGE);
        whiteList.put("/snapshot", Permission.MAGE);
        whiteList.put("/snap", Permission.MAGE);
        whiteList.put("/restore", Permission.MAGE);
        whiteList.put("//restore", Permission.MAGE);
        whiteList.put("/range/repl", Permission.MAGE);
        whiteList.put("/cycler", Permission.MAGE);
        whiteList.put("/floodfill", Permission.MAGE);
        whiteList.put("/flood", Permission.MAGE);
        whiteList.put("/deltree", Permission.MAGE);
        whiteList.put("/farwand", Permission.MAGE);
        whiteList.put("/lrbuild", Permission.MAGE);
        whiteList.put("//lrbuild", Permission.MAGE);
        whiteList.put("/info", Permission.MAGE);
        whiteList.put("/fillr", Permission.MAGE);
        whiteList.put("//removeabove", Permission.MAGE);
        whiteList.put("/removeabove", Permission.MAGE);
        whiteList.put("//removebelow", Permission.MAGE);
        whiteList.put("/removebelow", Permission.MAGE);
        whiteList.put("//removenear", Permission.MAGE);
        whiteList.put("/removenear", Permission.MAGE);
        whiteList.put("//replacenear", Permission.MAGE);
        whiteList.put("/replacenear", Permission.MAGE);
    }
}
