package server.plugin.commands;

import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.SubCommandExecutor;
import server.plugin.manager.MessageManager;
import server.plugin.manager.RegionManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class RegionCommands extends SubCommandExecutor {

    private String tag = "[Region] ";

    public RegionCommands(){
        super();

        this.commandName = "region";

        commands.add(new CreateRegion());
        commands.add(new RemoveRegion());
    }

    private class CreateRegion extends BaseCommand {

        public CreateRegion(){
            this.name = "create";
            this.description = "Used to define a region, need to have world edit selection";
            this.arguments = "<name>";
            this.permission = Permission.KING;

            this.aliases.add("c");
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must provide a name for the region you're creating");
                return;
            }

            if(!(RegionManager.getInstance().getRegion(player.getWorld().getName(), args[0]) == null)) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is already a region named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                return;
            }

            Selection selection = RegionManager.getInstance().getPlayerSelection(player);

            if(selection == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must have a region selected with the world edit wand");
                return;
            }

            RegionManager.getInstance().createRegion(player, args[0], selection);
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The region '" + args[0] + "' has been succesfully created");
        }

    }

    private class RemoveRegion extends BaseCommand {

        public RemoveRegion(){
            this.name = "remove";
            this.description = "Used to remove a region";
            this.arguments = "<name>";
            this.permission = Permission.KING;

            this.aliases.add("r");
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must provide a name for the region you're removing");
                return;
            }

            if(RegionManager.getInstance().getRegion(player.getWorld().getName(), args[0]) == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is no region named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                return;
            }

            if(RegionManager.getInstance().removeRegion(player.getWorld().getName(), args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The region '" + args[0] + "' has been succesfully removed");
            } else {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There was a problem removing the region '" + args[0] + "' from the world'" + player.getWorld().getName() + "'");
            }
        }

    }
}
