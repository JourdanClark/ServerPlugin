package server.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.SubCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.HelperFunctions;
import server.plugin.common.StaticMethods;
import server.plugin.manager.ChatChannelManager;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.ChatChannel;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;
import server.plugin.types.Task;

public class ChatCommands extends SubCommandExecutor {

    public ChatCommands(){
        super();

        this.commandName = "chat";

        commands.add(new create());
        commands.add(new delete());
        commands.add(new list());
        commands.add(new join());
        commands.add(new censor());
    }

    private class create extends BaseCommand {

        private String channelAlreadyExists = "A channel by that name already exists";

        public create(){
            this.name = "create";
            this.description = "Allows you to create a new channel";
            this.arguments = "<name>";
            this.permission = Permission.TRAVELER;
            this.aliases.add("c");
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length != 1) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidNumberOfArguments());
                return;
            }
            if(ChatChannelManager.getInstance().channelExists(args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, channelAlreadyExists);
                return;
            }
            if(HelperFunctions.containsSpecialCharacter(args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "The channel name cannot contain special characters");
                return;
            }
            if(args[0].equalsIgnoreCase("local") || args[0].equalsIgnoreCase("whisper")) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, channelAlreadyExists);
                return;
            }
            if(ChatChannelManager.getInstance().addChannel(args[0]))
                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "The channel '" + args[0] + "' has been created");
            else
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "An error occurred while creating the channel '" + args[0] + "'");
        }
    }

    private class delete extends BaseCommand {

        public delete(){
            this.name = "delete";
            this.description = "Allows you to delete a channel you created";
            this.arguments = "<name>";
            this.permission = Permission.TRAVELER;
            this.aliases.add("d");
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length != 1) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidNumberOfArguments());
                return;
            }
            if(!ChatChannelManager.getInstance().channelExists(args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "No channel by that name exists");
                return;
            }
            ChatChannel channel = ChatChannelManager.getInstance().getChannel(args[0]);
            if(!channel.isOwner(player)) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "You do not own this channel ");
                return;
            }
            ChatChannelManager.getInstance().removeChannel(channel);
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "The channel '" + args[0] + "' has been deleted");
        }
    }

    private class list extends BaseCommand {

        public list(){
            this.name = "list";
            this.description = "Lists all public channels";
            this.arguments = "<name>";
            this.permission = Permission.TRAVELER;
            this.aliases.add("l");
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length != 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidNumberOfArguments());
                return;
            }
            ChatChannel[] channels = ChatChannelManager.getInstance().getChannels();
            if(channels != null) {
                MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, ChatColor.YELLOW + "------ " + ChatColor.BLUE + "Public Channels" + ChatColor.YELLOW + " ------");
                for(ChatChannel c : channels) {
                    if(c != null)
                        MessageManager.getInstance().sendMessage(player, MessageType.INFO, c.getName());
                }
            } else {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "There are no public channels");
            }
        }
    }

    private class join extends BaseCommand {

        public join(){
            this.name = "join";
            this.description = "Allows you to join a channel";
            this.arguments = "<name>";
            this.permission = Permission.TRAVELER;
            this.aliases.add("j");
        }

        @Override
        public void onCommand(Player player, String[] args){
            String channelName = args[0];
            String message = "Joining the channel: %s";
            ServerPlayer sp = PlayerManager.getInstance().getPlayer(player);

            if(args.length != 1) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.invalidNumberOfArguments());
                return;
            }
            if(!ChatChannelManager.getInstance().channelExists(channelName) && !channelName.equalsIgnoreCase("local") && !channelName.equalsIgnoreCase("whisper")) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "The channel '" + channelName + "' does not exist");
                return;
            }

            ChatChannel channel = null;

            if(channelName.equalsIgnoreCase("local")) {
                channelName = player.getName().toLowerCase() + ".local";
                channel = ChatChannelManager.getInstance().getChannel(channelName);
                message = String.format(message, "Local");
            } else if(channelName.equalsIgnoreCase("whisper")) {
                channelName = player.getName().toLowerCase() + ".whisper";
                channel = ChatChannelManager.getInstance().getChannel(channelName);
                message = String.format(message, "Whisper");
            } else {
                channel = ChatChannelManager.getInstance().getChannel(channelName);
                message = String.format(message, channel.getName());
            }

            if(channel == PlayerManager.getInstance().getPlayer(player).getCurrentChannel()) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "You are already in this channel");
                return;
            }

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, message);
            sp.getCurrentChannel().removePlayer(sp.getBukkitPlayer());
            sp.setChannel(channel);
            channel.addPlayer(player);
            Object[] arguments = {channel, player};
            Task.getInstance().delayed(StaticMethods.class, "loadAChannelForAPlayer", arguments, 30);
        }
    }

    private class censor extends BaseCommand {

        public censor(){
            this.name = "censor";
            this.description = "Toggle whether or not to censor messages";
            this.permission = Permission.LEPPER;
        }

        @Override
        public void onCommand(Player player, String[] args){
            ServerPlayer sp = PlayerManager.getInstance().getPlayer(player);
            if(sp.isCensoringMessages()) {
                MessageManager.getInstance().sendMessage(sp, MessageType.SEVERE, "Messages will no longer be censored");
                sp.setCensoringMessages(false);
            } else {
                MessageManager.getInstance().sendMessage(sp, MessageType.GOOD, "Messages will now be censored");
                sp.setCensoringMessages(true);
            }
        }
    }
}
