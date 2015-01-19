package server.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.SubCommandExecutor;
import server.plugin.manager.LocationManager;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class LocationCommands extends SubCommandExecutor {

    private String tag = "[Location] ";

    public LocationCommands(){
        super();

        this.commandName = "location";

        commands.add(new CreateLocation());
        commands.add(new RemoveLocation());
        commands.add(new ListLocations());
        commands.add(new TeleportToLocation());
        commands.add(new ChangeLocation());

    }

    private class CreateLocation extends BaseCommand {

        public CreateLocation(){
            this.name = "create";
            this.description = "Used to define a Location at your location";
            this.arguments = "<name>";
            this.permission = Permission.KING;

            this.aliases.add("c");
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must provide a name for the location you're creating");
                return;
            }

            if(!(LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]) == null)) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is already a location named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                return;
            }

            LocationManager.getInstance().createLocation(player, args[0], player.getLocation());

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The location '" + args[0] + "' has been succesfully created");
        }

    }

    private class RemoveLocation extends BaseCommand {

        public RemoveLocation(){
            this.name = "remove";
            this.description = "Used to remove a Location";
            this.arguments = "<name>";
            this.permission = Permission.KING;

            this.aliases.add("r");
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must provide a name for the location you're removing");
                return;
            }

            if(LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]) == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is no location named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                return;
            }

            if(LocationManager.getInstance().removeLocation(player.getWorld().getName(), args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The region '" + args[0] + "' has been succesfully removed");
            } else {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There was a problem removing the region '" + args[0] + "' from the world'" + player.getWorld().getName() + "'");
            }

        }

    }

    private class ListLocations extends BaseCommand {

        public ListLocations(){
            this.name = "list";
            this.description = "List the locations for the world you're in";
            this.permission = Permission.KING;

            this.aliases.add("l");
        }

        public void onCommand(Player player, String[] args){
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "--- Locations in " + player.getWorld().getName() + " ---");

            String[] locations = LocationManager.getInstance().getWorldLocations(player.getWorld().getName());

            if(locations == null || locations.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, "  None");
                return;
            }

            for(String name : locations) {
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, "  - " + name);
            }
        }

    }

    private class TeleportToLocation extends BaseCommand {

        public TeleportToLocation(){
            this.name = "teleport";
            this.description = "Teleport to a location";
            this.arguments = "<name>";
            this.permission = Permission.KING;

            this.aliases.add("tp");
        }

        public void onCommand(Player player, String[] args){
            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You must provide a name for the location you're removing");
                return;
            }

            Location location = LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]);

            if(location == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is no location named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                return;
            }

            if(player.teleport(location)) {
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "Teleported to '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
            } else {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "Failed to teleport to '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
            }

        }

    }

    private class ChangeLocation extends BaseCommand {

        public ChangeLocation(){
            this.name = "change";
            this.description = "Make changes to a location";
            this.arguments = "<name> [distance] [axis]";
            this.permission = Permission.KING;

            this.aliases.add("adjust");
            this.aliases.add("a");
        }

        public void onCommand(Player player, String[] args){

            if(args.length == 1) {
                if(LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]) == null) {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is no a location named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                    return;
                }

                if(LocationManager.getInstance().setLocation(player.getWorld().getName(), args[0], player.getLocation())) {
                    MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The location '" + args[0] + "' has been succesfully updated to your position");
                } else {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There was an error updating the location '" + args[0] + "'");
                }
            } else if(args.length == 3) {
                if(LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]) == null) {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There is no a location named '" + args[0] + "' in the world '" + player.getWorld().getName() + "'");
                    return;
                }

                try {

                    double ammount = Double.valueOf(args[1]);

                    if((!args[2].equalsIgnoreCase("x")) && (!args[2].equalsIgnoreCase("y")) && (!args[2].equalsIgnoreCase("z"))) {
                        MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "Invalid axis '" + args[2] + "' must be 'x', 'y', or 'z'");
                    }

                    Location loc = LocationManager.getInstance().getLocation(player.getWorld().getName(), args[0]);

                    if(args[2].equalsIgnoreCase("x"))
                        loc.setX(loc.getX() + ammount);
                    if(args[2].equalsIgnoreCase("y"))
                        loc.setY(loc.getY() + ammount);
                    if(args[2].equalsIgnoreCase("z"))
                        loc.setZ(loc.getZ() + ammount);

                    if(LocationManager.getInstance().setLocation(player.getWorld().getName(), args[0], loc)) {
                        MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "The location '" + args[0] + "' adjusted by '" + args[1] + "' on the '" + args[2] + "' axis");
                    } else {
                        MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "There was a problem adjusting the location '" + args[0] + "' by '" + args[1] + "' on the '" + args[2] + "' axis");
                    }

                } catch(NumberFormatException e) {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "Invalid distance! '" + args[1] + "' must be a number");
                }

            } else {
                MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.RED + tag + "Invalid number of arguments. Propper use:");
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, "   /location adjust dirtSpawn - Sets the location to your location");
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, "   /location adjust dirtSpawn 3 y - Adjust the location '3' along the y axis.");
            }

        }

    }
}
