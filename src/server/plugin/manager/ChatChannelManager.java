package server.plugin.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import server.plugin.types.ChatChannel;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class ChatChannelManager {

    private String                    nameFormat      = "<%s> ";
    private int                       localChatRadius = 100;

    private static ChatChannelManager instance        = new ChatChannelManager();

    public static ChatChannelManager getInstance(){
        return instance;
    }

    private ArrayList<ChatChannel> channels;

    public ChatChannelManager(){
    }

    public void init(){
        channels = new ArrayList<ChatChannel>();
        // load known chat channels into array
        addChannel("Global");
        addChannel("Admin", Permission.KNIGHT);

        // for errything in the database createAndAddChannels();

    }

    public void handleChat(AsyncPlayerChatEvent event){
        event.setCancelled(true);
        String message = String.format(nameFormat, event.getPlayer().getDisplayName()) + event.getMessage();

        // add chat to players local chatss
        ChatChannel local = null;
        ChatChannel current = null;
        ServerPlayer sp = PlayerManager.getInstance().getPlayer(event.getPlayer());
        if(sp.getCurrentChannel() == sp.getLocalChannel()) {
            List<Entity> entityList = event.getPlayer().getNearbyEntities(localChatRadius, localChatRadius, localChatRadius);
            entityList.add(event.getPlayer());
            for(Entity e : entityList) {
                if(e instanceof Player) {
                    sp = PlayerManager.getInstance().getPlayer((Player) e);
                    local = sp.getLocalChannel();
                    current = sp.getCurrentChannel();
                    boolean equals = local == current;
                    local.putMessage(message, equals); // it will send the message if the local channel is the same as their current channel
                }
            }
        }
        current = null;
        sp = PlayerManager.getInstance().getPlayer(event.getPlayer());
        // send to the channel they're in if it isn't whisper or local;
        if(sp.getCurrentChannel() != sp.getLocalChannel() && sp.getCurrentChannel() != sp.getWhisperChannel())
            current = sp.getCurrentChannel();
        if(current != null) {
            current.putMessage(message, true);
        }
    }

    public void sendMessage(String message, String channelName){
        ChatChannel channel = getChannel(channelName);
        if(channel == null) {
            return;
        }
        channel.putMessage(message, true);
    }

    public void sendMessage(String message, String[] channelNames){
        for(String channel : channelNames) {
            sendMessage(message, channel);
        }
    }

    /**
     * @param name The name of the Chat Channel
     * @return Returns the Chat Channel from the provided name. Null if there is
     *         not a channel named that.
     */
    public ChatChannel getChannel(String name){
        for(ChatChannel c : channels) {
            if(c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adds a Chat Channel to the ArrayList if one by that name does not already
     * exist.
     * 
     * @param name The name of the Chat Channel
     * @return Returns false if there is already a Chat Channel named that.
     *         Returns true if it is able to successfully add the channel.
     */
    public boolean addChannel(String name){
        for(ChatChannel c : channels) {
            if(c.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        channels.add(new ChatChannel(name));
        return true;
    }

    /**
     * Adds a Chat Channel to the ArrayList if one by that name does not already
     * exist.
     * 
     * @param name The name of the Chat Channel
     * @return Returns false if there is already a Chat Channel named that.
     *         Returns true if it is able to successfully add the channel.
     */
    public boolean addChannel(String name, Permission permission){
        for(ChatChannel c : channels) {
            if(c.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        channels.add(new ChatChannel(name, permission));
        return true;
    }

    /**
     * Adds a Chat Channel to the ArrayList if one with the same name does not
     * already exist.
     * 
     * @param channel The Chat Channel to add
     * @return Returns false if there is already a Chat Channel named that.
     *         Returns true if it is able to successfully add the channel.
     */
    public boolean addChannel(ChatChannel channel){
        for(ChatChannel c : channels) {
            if(c.getName().equalsIgnoreCase(channel.getName())) {
                return false;
            }
        }
        channels.add(channel);
        return true;
    }

    /**
     * Checks to see if a Chat Channel already exists by the given name
     * 
     * @param name The name of the Chat Channel
     * @return Returns false if there isn't a Chat Channel by that name
     */
    public boolean channelExists(String name){
        for(ChatChannel c : channels) {
            if(c.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns an array of all the saved Chat Channels
     */
    public ChatChannel[] getChannels(){
        ChatChannel[] Channels = new ChatChannel[channels.size()];

        for(int i = 0;i < channels.size();i++) {
            if(!channels.get(i).getName().endsWith(".local") && !channels.get(i).getName().endsWith(".whisper"))
                Channels[i] = channels.get(i);
        }

        return Channels;

    }

    /**
     * Removes a channel from the array list.
     * 
     * @param channel The channel to remove.
     */
    public void removeChannel(ChatChannel channel){
        if(channel == null)
            return;
        if(channels.contains(channel))
            channels.remove(channel);
    }

    /**
     * Removes a channel from the array list.
     * 
     * @param name The name of the channel to remove.
     */
    public void removeChannel(String name){
        if(name.equals(null))
            return;
        ChatChannel channel = getChannel(name);
        removeChannel(channel);
    }
}
