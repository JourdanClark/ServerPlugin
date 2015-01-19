package server.plugin.minigames.redvsblue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.common.CommonMessages;
import server.plugin.manager.LocationManager;
import server.plugin.manager.MessageManager;
import server.plugin.manager.MessageManager.MessageType;
import server.plugin.manager.RegionManager;
import server.plugin.minigames.arena.Arena;
import server.plugin.minigames.arena.ArenaManager;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerCommandExecutor;
import server.plugin.types.ServerRegion;
import server.plugin.types.SubCommand;

public class RedVsBlueCommandManager extends ServerCommandExecutor {

    public RedVsBlueCommandManager(){
        super();

        commandName = "RedVsBlue";

        commands.add(new Join());
        commands.add(new Create());
        commands.add(new ArenaList());
        commands.add(new Teleport());
        commands.add(new SetSpawn());
    }

    private class ArenaList extends SubCommand {

        public ArenaList(){
            permission = Permission.KING;
        }

        @Override
        public List<String> aliases(){
            List<String> a = new ArrayList<String>();
            a.add("l");
            return a;
        }

        @Override
        public String description(){
            return "List the available arenas.";
        }

        @Override
        public String name(){
            return "list";
        }

        @Override
        public void onCommand(Player p, String[] args){
            HashMap<String, Arena> arenas = ArenaManager.getInstance().getArenas();

            MessageManager.getInstance().sendMessage(p, "--- List of Arenas ---", MessageType.GOOD, false);
            for(String arena : arenas.keySet()) {
                MessageManager.getInstance().sendMessage(p, arena, MessageType.GOOD, false);
            }
        }

    }

    private class Create extends SubCommand {

        public Create(){
            permission = Permission.KING;
        }

        public List<String> aliases(){
            List<String> a = new ArrayList<String>();
            a.add("c");
            return a;
        }

        public String arguments(){
            return "<name> <region>";
        }

        public String description(){
            return "Create a RedVsBlue arena.";
        }

        public String name(){
            return "Create";
        }

        public void onCommand(Player p, String[] args){
            if(args.length < 2) {
                MessageManager.getInstance().sendMessage(p, "Error creating RedVsBlue arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, CommonMessages.invalidNumberOfArguments(), MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, "Usage: /redvsblue create <name> <regionName>", MessageType.SUBINFO, false);
                return;
            }

            if(ArenaManager.getInstance().getArena(args[0]) != null) {
                MessageManager.getInstance().sendMessage(p, "Error creating RedVsBlue arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " There is already an arena named '" + args[0] + "'!", MessageType.INFO, false);
                return;
            }

            ServerRegion region = RegionManager.getInstance().getRegion(p.getWorld().getName(), args[1]);

            if(region == null) {
                MessageManager.getInstance().sendMessage(p, "Error creating RedVsBlue arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " There is no region named '" + args[1] + "'!", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " Type '/region' for help creating regions.", MessageType.INFO, false);
                return;
            }

            ArenaManager.getInstance().addArena(p, region, args[1], new String[]{"red", "blue"});

            MessageManager.getInstance().sendMessage(p, "The RedVsBlue arena '" + args[1] + "' has been successfully created", MessageType.GOOD, false);

        }

    }

    private class Join extends SubCommand {

        public Join(){
            permission = Permission.KING;
        }

        public List<String> aliases(){
            List<String> a = new ArrayList<String>();
            a.add("j");
            return a;
        }

        public String description(){
            return "Join a game.";
        }

        public String name(){
            return "Join";
        }

        public void onCommand(Player p, String[] args){
            if(args == null) {
                p.sendMessage("Put a team number nikka");
            } else {
                p.sendMessage("" + args);
            }
        }

    }

    private class SetSpawn extends SubCommand {

        public SetSpawn(){
            permission = Permission.KING;
        }

        public List<String> aliases(){
            List<String> a = new ArrayList<String>();
            a.add("t");
            return a;
        }

        public String arguments(){
            return "<arena> <team> <location>";
        }

        public String description(){
            return "Set a spawn location.";
        }

        public String name(){
            return "SpawnSet";
        }

        public void onCommand(Player p, String[] args){

            if(args.length < 3) {
                MessageManager.getInstance().sendMessage(p, "Error setting spawn for a RedVsBlue Arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, CommonMessages.invalidNumberOfArguments(), MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " Usage: /redvsblue spawnset <arena> <team> <location>", MessageType.SUBINFO, false);
                return;
            }

            Arena arena = ArenaManager.getInstance().getArena(args[0]);

            if(arena == null) {
                MessageManager.getInstance().sendMessage(p, "Error setting spawn for a RedVsBlue Arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " The arena named '" + args[0] + "' does not exist!", MessageType.SEVERE, false);
                return;
            }

            if(!arena.getTeams().contains(args[1])) {
                MessageManager.getInstance().sendMessage(p, "Error setting spawn for a RedVsBlue Arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " There is no team named '" + args[1] + "' for the arena named '" + args[0] + "'!", MessageType.SEVERE, false);
                return;
            }

            Location location = LocationManager.getInstance().getLocation(p.getWorld().getName(), args[2]);

            if(location == null) {
                MessageManager.getInstance().sendMessage(p, "Error setting spawn for a RedVsBlue Arena:", MessageType.SEVERE, false);
                MessageManager.getInstance().sendMessage(p, " No location named '" + args[2] + "'!", MessageType.SEVERE, false);
                return;
            }

            arena.setSpawnLocation(args[1], location);
            MessageManager.getInstance().sendMessage(p, "The location '" + args[2] + "' was successfully set for the team '" + args[1] + "' in the arena '" + args[0] + "'", MessageType.GOOD, false);
        }

    }

    private class Teleport extends SubCommand {

        public Teleport(){
            permission = Permission.KING;
        }

        public List<String> aliases(){
            List<String> a = new ArrayList<String>();
            a.add("tp");
            return a;
        }

        public String arguments(){
            return "<arena> <team>";
        }

        public String description(){
            return "Teleport to a team's spawn location.";
        }

        public String name(){
            return "teleport";
        }

        public void onCommand(Player p, String[] args){
            if(args.length > 0) {
                if(ArenaManager.getInstance().getArena(args[0]) == null) {
                    MessageManager.getInstance().sendMessage(p, "Invalid arena name!", MessageType.SEVERE, false);
                    return;
                }
                if(args.length > 1) {
                    if(ArenaManager.getInstance().getArena(args[0]) != null) {
                        p.teleport(ArenaManager.getInstance().getArena(args[0]).getSpawnLocation(args[1]));
                    } else {
                        p.sendMessage("Not a valid arena Name!");
                    }
                } else {
                    MessageManager.getInstance().sendMessage(p, "--- " + args[0] + " Team Spawns ---", MessageType.GOOD, false);
                    for(String team : ArenaManager.getInstance().getArena(args[0]).getTeams()) {
                        Location l = ArenaManager.getInstance().getArena(args[0]).getSpawnLocation(team);
                        MessageManager.getInstance().sendMessage(p, team + ":" + l, MessageType.INFO, false);
                    }
                }
            } else {
                MessageManager.getInstance().sendMessage(p, "Arena name required.", MessageType.SEVERE, false);
            }
        }

    }

    private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();

}
