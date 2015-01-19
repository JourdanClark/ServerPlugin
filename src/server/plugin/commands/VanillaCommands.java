package server.plugin.commands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import server.plugin.ServerPlugin;
import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.GroupCommandExecutor;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.StaticMethods;
import server.plugin.manager.CommandManager;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;
import server.plugin.types.Task;

public class VanillaCommands extends GroupCommandExecutor {

    public VanillaCommands(){
        super();

        executors.add(new HelpExecutor());
        executors.add(new TimeExecutor());
        executors.add(new SpawnExecutor());
        executors.add(new GamemodeExecutor());
        executors.add(new WeatherExecutor());
    }

    private class HelpExecutor extends NormalCommandExecutor {
        public HelpExecutor(){
            commands.add(new Help());
        }

        private class Help extends BaseCommand {

            public Help(){
                this.name = "help";
                this.description = "";
                this.permission = Permission.LEPPER;
            }

            @Override
            public void onCommand(Player player, String[] args){
                MessageManager.getInstance().sendMessage(player, MessageType.INFO, CommandManager.getInstance().getHelpMessage());
            }
        }
    }
    
    private class TimeExecutor extends NormalCommandExecutor {
        public TimeExecutor(){
            commands.add(new Time());
        }

        private class Time extends BaseCommand {

            public Time(){
                this.name = "time";
                this.description = "Set the time for the world you're in";
                this.arguments = "<time>";
                this.permission = Permission.MAGE;
            }

            @Override
            public void onCommand(Player player, String[] args){
                if(args.length >= 1) {
                    try {
                        Long.parseLong(args[0]);
                        player.getWorld().setFullTime(Long.parseLong(args[0]));
                        MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Time set to " + args[0]);
                        return;
                    } catch(Exception e) {
                        if(ServerPlugin.freezeTime && !args[0].equals("freeze")) {
                            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "Time is frozen at " + player.getWorld().getTime() + " and cannot be changed");
                            MessageManager.getInstance().sendMessage(player, MessageType.INFO, "Use '/time freeze' to unfreeze time.");
                            return;
                        }
                        switch((String) args[0].toLowerCase()){
                            case "day":
                                player.getWorld().setFullTime(0);
                                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Time set to 0");
                                return;
                            case "noon":
                                player.getWorld().setFullTime(6000);
                                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Time set to 6000");
                                return;
                            case "night":
                                player.getWorld().setFullTime(12000);
                                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Time set to 12000");
                                return;
                            case "midnight":
                                player.getWorld().setFullTime(18000);
                                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Time set to 18000");
                                return;
                            case "freeze":
                                boolean frozen = ServerPlugin.freezeTime;
                                if(!frozen) {
                                    Object[] parameters = {player.getWorld(), player.getWorld().getTime()};
                                    int taskID = Task.getInstance().repeatable(StaticMethods.class, "setTime", parameters, 10, 10);
                                    ServerPlugin.freezeID = taskID;
                                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "Time Frozen at " + player.getWorld().getTime());

                                } else {
                                    Task.removeRepeatableEvent(ServerPlugin.freezeID);
                                    ServerPlugin.freezeID = -1;
                                    MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Time has resumed");
                                }
                                ServerPlugin.freezeTime = !ServerPlugin.freezeTime;
                                break;
                            default:
                                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "Invalid time");
                                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, "Usage: /time [<time>|day|noon|night|midnight]");
                                return;
                        }
                    }
                } else {
                    MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "The current time is " + player.getWorld().getTime());
                }
            }
        }
    }

    private class SpawnExecutor extends NormalCommandExecutor {

        public SpawnExecutor(){
            commands.add(new SetSpawn());
            commands.add(new Spawn());
        }

        private class SetSpawn extends BaseCommand {

            public SetSpawn(){
                this.name = "setspawn";
                this.description = "Used to set the spawn for the world you are in";
                this.permission = Permission.KING;
            }

            @Override
            public void onCommand(Player player, String[] args){
                Location loc = player.getLocation();
                player.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Spawn set");
            }
        }

        private class Spawn extends BaseCommand {

            public Spawn(){
                this.name = "spawn";
                this.description = "Used to teleport to the spawn location in your world";
                this.permission = Permission.TRAVELER;
            }

            // TODO have this trigger a ServerPlayerTeleport event with the
            // reason SPAWN_TELEPORT or something like that
            @Override
            public void onCommand(Player player, String[] args){
                player.teleport(player.getWorld().getSpawnLocation());
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Teleported to spawn");
            }
        }
    }

    private class WeatherExecutor extends NormalCommandExecutor {

        public WeatherExecutor(){
            commands.add(new ToggleDownfall());
        }

        private class ToggleDownfall extends BaseCommand {

            public ToggleDownfall(){
                this.name = "toggledownfall";
                this.description = "Used to toggle the weather in the weather you're in";
                this.permission = Permission.MAGE;
                this.aliases.add("togglerain");

            }

            @Override
            public void onCommand(Player player, String[] args){
                World world = player.getWorld();
                if(world.hasStorm()) {
                    if(world.isThundering()) {
                        world.setThundering(false);
                    }
                    world.setStorm(false);
                    MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Rain turned off");
                    return;
                } else {
                    world.setStorm(true);
                    world.setThundering(true);
                    MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Rain turned on");
                    return;
                }
            }
        }
    }

    private class GamemodeExecutor extends NormalCommandExecutor {

        public GamemodeExecutor(){
            commands.add(new ChangeGamemode());
        }

        private class ChangeGamemode extends BaseCommand {

            public ChangeGamemode(){
                this.name = "gamemode";
                this.description = "Used to change your (or another player's) gamemode";
                this.arguments = "[adventure|survival|creative]";
                this.permission = Permission.PALADIN;

                this.aliases.add("gm");
            }

            private void setGameMode(Player player, GameMode type){

                switch(type){

                    case ADVENTURE:
                        player.setGameMode(GameMode.ADVENTURE);
                        MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Your gamemode has been set to Adventure");
                        return;
                    case SURVIVAL:
                        player.setGameMode(GameMode.SURVIVAL);
                        MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Your gamemode has been set set to Survival");
                        return;
                    case CREATIVE:
                        player.setGameMode(GameMode.CREATIVE);
                        MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Your gamemode has been set set to Creative");
                        return;
                }

            }

            @Override
            public void onCommand(Player player, String[] args){
                if(args.length == 0) {
                    switch(player.getGameMode()){
                        case ADVENTURE:
                            setGameMode(player, GameMode.SURVIVAL);
                            return;
                        case SURVIVAL:
                            setGameMode(player, GameMode.CREATIVE);
                            return;
                        case CREATIVE:
                            setGameMode(player, GameMode.SURVIVAL);
                            return;
                    }
                } else if(args.length == 1) {

                    switch(args[0].toLowerCase()){
                        case "a":
                            setGameMode(player, GameMode.ADVENTURE);
                            return;
                        case "s":
                            setGameMode(player, GameMode.SURVIVAL);
                            return;
                        case "c":
                            setGameMode(player, GameMode.CREATIVE);
                            return;
                        case "adventure":
                            setGameMode(player, GameMode.ADVENTURE);
                            return;
                        case "survival":
                            setGameMode(player, GameMode.SURVIVAL);
                            return;
                        case "creative":
                            setGameMode(player, GameMode.CREATIVE);
                            return;
                    }
                } else {
                    ServerPlayer sp = PlayerManager.getInstance().getPlayer(args[0]);
                    if(sp == null) {
                        MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.playerDoesntExist(args[0]));
                        return;
                    }
                    Player otherPlayer = sp.getBukkitPlayer();
                    switch(args[1].toLowerCase()){
                        case "a":
                            setGameMode(otherPlayer, GameMode.ADVENTURE);
                            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, otherPlayer.getName() + "'s gamemode has been set to Adventure");
                            return;
                        case "s":
                            setGameMode(otherPlayer, GameMode.SURVIVAL);
                            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, otherPlayer.getName() + "'s gamemode has been set to Survival");
                            return;
                        case "c":
                            setGameMode(otherPlayer, GameMode.CREATIVE);
                            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, otherPlayer.getName() + "'s gamemode has been set to Creative");
                            return;
                        case "adventure":
                            setGameMode(otherPlayer, GameMode.ADVENTURE);
                            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, otherPlayer.getName() + "'s gamemode has been set to Adventure");
                            return;
                        case "survival":
                            setGameMode(otherPlayer, GameMode.SURVIVAL);
                            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, otherPlayer.getName() + "'s gamemode has been set to Survival");
                            return;
                        case "creative":
                            setGameMode(otherPlayer, GameMode.CREATIVE);
                            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, otherPlayer.getName() + "'s gamemode has been set to Creative");
                            return;
                    }
                }
            }
        }
    }

}
