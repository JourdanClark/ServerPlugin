package server.plugin.types;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;

public class ChatChannel {

    private String            name, owner;
    private String[]          last20;
    private ArrayList<String> players;
    private Permission        permission = Permission.TRAVELER;

    public ChatChannel(String name){
        this.name = name;
        last20 = new String[20];
        init();
        players = new ArrayList<String>();
    }

    public ChatChannel(String name, Permission permission){
        this.name = name;
        last20 = new String[20];
        init();
        players = new ArrayList<String>();
        if(permission != null)
            this.permission = permission;

    }

    private void init(){
        for(int i = 0;i < 20;i++) {
            last20[i] = "";
        }
    }

    /**
     * Adds the message to the channel
     * 
     * @param message The message being added to the channel
     * @param sendMessage Whether or not to send the message to the channel
     */
    public void putMessage(String message, boolean sendMessage){
        int i = 0;
        for(;i < 20;i++) {
            if(last20[i].equals("")) {
                last20[i] = message;
                if(sendMessage) {
                    sendMessageToChannel(message);
                    return;
                }
                return;
            }
        }
        // nothing was empty

        // shift everything back 1 in the array and put the new message at the
        // bottom
        while(i < 19) {
            last20[i] = last20[i + 1];
        }
        last20[19] = message;

        if(sendMessage) {
            sendMessageToChannel(message);
        }
    }

    /**
     * @return Returns the name of the channel
     */
    public String getName(){
        return name;
    }

    /**
     * Sends the player the last 20 messages from that channel
     * 
     * @param player The player loading the channel
     */
    public void loadChannel(Player player){
        String message = "";
        for(int i = 0;i < 20;i++) {
            message = last20[i];
            if(!message.equals(null) && !message.equals(""))
                MessageManager.getInstance().sendMessage(player, MessageType.CUSTOM, message);
        }
        MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Joined channel");
    }

    /**
     * Adds a player to the Chat Channel
     * 
     * @param player The Player to add
     * @return Returns false if the player is already in the channel or if the
     *         player is null
     */
    public boolean addPlayer(Player player){
        if(player == null || !hasPermission(player)) {
            return false;
        }

        for(String s : players) {
            if(s.equalsIgnoreCase(player.getName().toLowerCase()))
                return false;
        }
        players.add(player.getName().toLowerCase());
        return true;
    }

    /**
     * Adds a player to the Chat Channel
     * 
     * @param player The name of the player to add
     * @return Returns false if the player is already in the channel or if the
     *         player is null
     */
    public boolean addPlayer(String player){
        if(player.equals(null) || !hasPermission(Bukkit.getPlayerExact(player.toLowerCase()))) {
            return false;
        }

        for(String s : players) {
            if(s.equalsIgnoreCase(player.toLowerCase()))
                return false;
        }

        players.add(player.toLowerCase());
        return true;
    }

    /**
     * Sends a message to all the players listening to the channel
     * @param message
     * @deprecated Should use putMessage(message, true) to send a message.
     */
    @Deprecated
    public void sendMessageToChannel(String message){
        if(message == null || message.equals("")) {
            return;
        }

        for(String s : players) {
            Player player = Bukkit.getPlayerExact(s);
            if(player != null) {
                player.sendMessage(message);
            } else {
                players.remove(s); // they should be removed when the log out and re-added when the log in TODO
            }
        }
    }

    /**
     * Checks to see if a player has permission for the channel.
     * 
     * @param player The player in question.
     * @return Returns true if the player has permission.
     */
    public boolean hasPermission(Player player){
        if(permission.isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player)))
            return true;
        return false;
    }

    /**
     * Allows you to set the permission of a channel.
     * 
     * @param permission The permission to set the channel to.
     */
    public void setPermission(Permission permission){
        this.permission = permission;
    }

    /**
     * Allows you to set the owner of the channel. This person is allowed to
     * delete the channel.
     * 
     * @param player The owner of the channel.
     */
    public void setOwner(Player player){
        owner = player.getName();
    }

    /**
     * Allows you to set the owner of the channel. This person is allowed to
     * delete the channel.
     * 
     * @param player The owner of the channel.
     */
    public void setOwner(String player){
        owner = player;
    }

    /**
     * @return Returns the owner of the channel
     */
    public String getOwner(){
        return owner;
    }

    /**
     * @param player The player in question.
     * @return Returns true if the owner is the given player.
     */
    public boolean isOwner(Player player){
        return player.getName().equalsIgnoreCase(owner);
    }

    /**
     * @param player The player in question.
     * @return Returns true if the owner is the given player.
     */
    public boolean isOwner(String player){
        return player.equalsIgnoreCase(owner);
    }

    /**
     * Removes the player from the channel.
     * 
     * @param player The player to remove.
     */
    public void removePlayer(Player player){
        if(player == null || !players.contains(player.getName().toLowerCase())) {
            return;
        }
        players.remove(player.getName().toLowerCase());
    }

    /**
     * Removes the player from the channel.
     * 
     * @param player The player to remove.
     */
    public void removePlayer(String player){
        if(player == null || !players.contains(player.toLowerCase())) {
            return;
        }
        players.remove(player.toLowerCase());
    }
}
