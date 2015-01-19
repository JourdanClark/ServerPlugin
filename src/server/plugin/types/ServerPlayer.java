package server.plugin.types;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import server.plugin.database.tables.DisguisesTable;
import server.plugin.database.tables.MinecraftUsersTable;
import server.plugin.manager.ChatChannelManager;
import server.plugin.types.Enums.DebugType;

public class ServerPlayer {
    private String                  name;
    private HashMap<String, Object> attributes     = new HashMap<String, Object>();
    private ArrayList<DebugType>    debugListen;
    private int                     id;
    private boolean                 vanished       = false;
    private ChatChannel             channel        = ChatChannelManager.getInstance().getChannel("Global");
    private boolean                 isRunning      = false;
    private boolean                 censorMessages = true;

    public ServerPlayer(String name){
        this.name = name;
        init();
    }

    public void addDebugListenType(DebugType type){
        debugListen.add(type);
    }

    public Location getBlockLocation(){
        return new Location(Bukkit.getServer().getWorld(getStringTemp("block.world")), getIntTemp("block.x"), getIntTemp("block.y"), getIntTemp("block.z"), getBukkitPlayer().getLocation().getYaw(), getBukkitPlayer().getLocation().getPitch());
    }

    public boolean getBooleanTemp(String attribute){
        return Boolean.parseBoolean(attributes.get(attribute) + "");
    }

    public Player getBukkitPlayer(){
        return Bukkit.getPlayerExact(name);
    }

    public ArrayList<DebugType> getDebugListenTypeList(){
        return debugListen;
    }

    public double getDoubleTemp(String attribute){
        return Double.parseDouble(attributes.get(attribute) + "");
    }

    public int getIntTemp(String attribute){
        return Integer.parseInt(attributes.get(attribute) + "");
    }

    public String getName(){
        return name;
    }

    public Location getPreviousBlockLocation(){
        return new Location(Bukkit.getServer().getWorld(getStringTemp("oldblock.world")), getIntTemp("oldblock.x"), getIntTemp("oldblock.y"), getIntTemp("oldblock.z"), getBukkitPlayer().getLocation().getYaw(), getBukkitPlayer().getLocation().getPitch());
    }

    public String getStringTemp(String attribute){
        return(attributes.get(attribute) + "");
    }

    public Object getTemp(String attribute){
        return attributes.get(attribute);
    }

    public void setChannel(ChatChannel channel){
        this.channel = channel;
    }

    public ChatChannel getCurrentChannel(){
        return channel;
    }

    public ChatChannel getLocalChannel(){
        return ChatChannelManager.getInstance().getChannel(name.toLowerCase() + ".local");
    }

    public ChatChannel getWhisperChannel(){
        return ChatChannelManager.getInstance().getChannel(name.toLowerCase() + ".whisper");
    }

    public void init(){
        this.debugListen = new ArrayList<DebugType>();
        
        setTemp("whisper.reply", "");
        setTemp("tpa.cooldown", 0);
        setTemp("block.world", getBukkitPlayer().getLocation().getWorld().getName());
        setTemp("block.x", 0);
        setTemp("block.y", 0);
        setTemp("block.z", 0);
        setTemp("oldblock.world", getBukkitPlayer().getLocation().getWorld().getName());
        setTemp("oldblock.x", 0);
        setTemp("oldblock.y", 0);
        setTemp("oldblock.z", 0);
        setTemp("disguise", "");
        
        HashMap<String,Object> data = DisguisesTable.getDisguiseData(getBukkitPlayer());
        if(data != null)
            setTemp("disguise", data.get(DisguisesTable.values.NAME.toString()));
        
        id = MinecraftUsersTable.getPlayerID(getBukkitPlayer());
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void setRunning(boolean setRunningTo){
        isRunning = setRunningTo;
    }

    public void setRunning(boolean setRunningTo, boolean reflectOnBukkitPlayer){
        isRunning = setRunningTo;
        if(reflectOnBukkitPlayer)
            getBukkitPlayer().setSprinting(isRunning);
    }

    public void throwPlayer(String name){
        if(Bukkit.getPlayerExact(name) == null) {
            return;
        }
        // TODO
        Player player = Bukkit.getPlayerExact(name);
        Vector vector = player.getLocation().getDirection().multiply(1.5);
        vector.setY(0.8);
        player.setVelocity(vector);
    }

    public boolean isVanished(){
        return vanished;
    }

    public void removeDebugListenType(DebugType type){
        debugListen.remove(type);
    }

    public void sendMessage(String message){
        Bukkit.getPlayerExact(name).sendMessage(message);
    }

    public void setTemp(String attribute, Object value){
        attributes.put(attribute, value);
    }

    public boolean isCensoringMessages(){
        return censorMessages;
    }

    public void setCensoringMessages(boolean censor){
        censorMessages = censor;
    }

    public void setHidden(boolean hidden){
        vanished = hidden;

    }

    public int getID(){
        return id;
    }
}
