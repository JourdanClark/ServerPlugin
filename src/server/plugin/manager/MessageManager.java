package server.plugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.ServerPlayer;

public class MessageManager {

    private static MessageManager instance = new MessageManager();

    public static MessageManager getInstance(){
        return instance;
    }

    private MessageManager(){
    }

    public static void broadcast(String msg){
        Bukkit.getServer().broadcastMessage(msg);
    }

    public static void broadcastWithoutServerTag(String msg){
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(msg);
        }
    }

    /**
     * Used to handle messaging players, does NOT replace disguised names.
     * 
     * @param player - The player you're sending the message to
     * @param type - The message type you're sending
     * @param args - The arguments for the message template
     */
    public void sendMessage(Player player, MessageType type, Object... args){
        player.sendMessage(type.format(args));
    }

    /**
     * Overloaded for CommandSender
     * 
     * @param player - The player you're sending the message to
     * @param type - The message type you're sending
     * @param args - The arguments for the message template
     */
    public void sendMessage(CommandSender sender, MessageType type, Object... args){
        sender.sendMessage(type.format(args));
    }

    /**
     * Overloaded for ServerPlayer
     * 
     * @param player - The player you're sending the message to
     * @param type - The message type you're sending
     * @param args - The arguments for the message template
     */
    public void sendMessage(ServerPlayer sPlayer, MessageType type, Object... args){
        sPlayer.sendMessage(type.format(args));
    }
}
