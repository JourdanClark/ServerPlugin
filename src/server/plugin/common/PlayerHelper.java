package server.plugin.common;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import server.plugin.ServerPlugin;
import server.plugin.database.tables.DisguisesTable;
import server.plugin.database.tables.PermissionsTable;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.ChatChannel;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class PlayerHelper {

    /**
     * @param players The players in question.
     * @return Returns a String array of what name to use in messages.
     */
    public static String[] checkDisguise(Player... players){
        String[] newNames = new String[players.length];
        for(int i = 0;i >= players.length;i++) {
            if(!PlayerManager.getInstance().getPlayer(players[i]).getTemp("disguise").equals("")) {
                newNames[i] = (String) PlayerManager.getInstance().getPlayer(players[i]).getTemp("disguise");
            } else {
                newNames[i] = players[i].getName();
            }
        }
        return newNames;
    }

    /**
     * @param players The players in question.
     * @return Returns a String array of what name to use in messages.
     */
    public static String[] checkDisguise(String... players){
        String[] newNames = new String[players.length];
        for(int i = 0;i >= players.length;i++) {
            if(!PlayerManager.getInstance().getPlayer(players[i]).getTemp("disguise").equals("")) {
                newNames[i] = (String) PlayerManager.getInstance().getPlayer(players[i]).getTemp("disguise");
            } else {
                newNames[i] = PlayerManager.getInstance().getPlayer(players[i]).getName();
            }
        }
        return newNames;
    }

    
    /**
     * @param player The player in question.
     * @return Returns the permission of the player. If no permission is found it returns the default permission.
     */
    public static Permission getPlayerPermission(Player player){
        return getPlayerPermission(player.getName());
    }

    /**
     * @param player The player in question.
     * @return Returns the permission of the player. If no permission is found it returns the default permission.
     */
    public static Permission getPlayerPermission(String player){
        System.out.println(PermissionsTable.getPlayerPermission(player));
        return PermissionsTable.getPlayerPermission(player);
    }

    /**
     * Checks a ServerPlayer's saved Location to see if they are standing at a
     * new one.
     * 
     * @param player The ServerPlayer involved.
     * @return Returns true if they are standing in a new location, false if
     *         they are not.
     */
    public static boolean isNewBlockLocation(ServerPlayer player){
        if(
                player.getStringTemp("block.world").equals(player.getBukkitPlayer().getLocation().getWorld().getName()) 
                && player.getIntTemp("block.x") == player.getBukkitPlayer().getLocation().getBlockX() 
                && player.getIntTemp("block.y") == player.getBukkitPlayer().getLocation().getBlockY() 
                && player.getIntTemp("block.z") == player.getBukkitPlayer().getLocation().getBlockZ()
          )
            return false;
        return true;
    }

    /**
     * Compares a ServerPlayer's saved Location to a given one.
     * 
     * @param player The ServerPlayer involved.
     * @param location The Location to compare with.
     * @return Returns true if the given Location is not the same as the one
     *         saved in the ServerPlayer attributes, false if it is the same.
     */
    public static boolean isNewBlockLocation(ServerPlayer player, Location location){
        if(
                player.getStringTemp("block.world").equals(location.getWorld().getName()) 
                && player.getIntTemp("block.x") == location.getBlockX() 
                && player.getIntTemp("block.y") == location.getBlockY() 
                && player.getIntTemp("block.z") == location.getBlockZ()
          )
            return false;
        return true;
    }

    /**
     * Checks to see if it'd be safe to use the given disguise name.
     * 
     * @param disguiseName The name in question.
     * @return Returns true if it would be safe to use.
     */
    public static boolean isSafeDisguise(String disguiseName){
        if(playerHasDisguise(disguiseName) || playerHasPlayedBefore(disguiseName))
            return false;
        return true;
    }

    /**
     * Checks to see if any player is disguised as the given name already.
     * 
     * @param disguiseName The name in question.
     * @return Returns true if a player already has the disguise.
     */
    public static boolean playerHasDisguise(String disguiseName){
        return DisguisesTable.disguiseExists(disguiseName);
    }

    /**
     * @param player The player in question.
     * @return Returns true if the player has played before.
     */
    public static boolean playerHasPlayedBefore(Player player){
        if(Arrays.asList(Bukkit.getOfflinePlayers()).contains(Bukkit.getOfflinePlayer(player.getName())))
            return true;
        return false;
    }

    /**
     * @param playerName The player in question.
     * @return Returns true if the player has played before.
     */
    public static boolean playerHasPlayedBefore(String playerName){
        if(Arrays.asList(Bukkit.getOfflinePlayers()).contains(Bukkit.getOfflinePlayer(playerName)))
            return true;
        return false;
    }

    /**
     * Sets the permission of the player.
     * @param player The player to set the permission for.
     * @param permission The permission to set it to.
     */
    public static void setPlayerPermission(Player player, Permission permission){
        setPlayerPermission(player.getName(), permission);
    }

    /**
     * Sets the permission of the player.
     * @param player The player to set the permission for.
     * @param permission The permission to set it to.
     */
    public static void setPlayerPermission(String player, Permission permission){
        PermissionsTable.setPlayerPermission(player, permission);
    }

    /**
     * Updates a ServerPlayer's block location to where they currently are
     * standing.
     * 
     * @param player The ServerPlayer involved.
     */
    public static void updateBlockLocation(ServerPlayer player){
        player.setTemp("oldblock.world", player.getStringTemp("block.world"));
        player.setTemp("oldblock.x", player.getIntTemp("block.x"));
        player.setTemp("oldblock.y", player.getIntTemp("block.y"));
        player.setTemp("oldblock.z", player.getIntTemp("block.z"));
        player.setTemp("block.world", player.getBukkitPlayer().getLocation().getWorld().getName());
        player.setTemp("block.x", player.getBukkitPlayer().getLocation().getBlockX());
        player.setTemp("block.y", player.getBukkitPlayer().getLocation().getBlockY());
        player.setTemp("block.z", player.getBukkitPlayer().getLocation().getBlockZ());
    }

    /**
     * Updates a player's name tag. Should be used whenever it is to be changed
     * to be sure it updates for everyone around the player.
     * 
     * @param player The player whose name tag needs to be updated.
     */
    public static void refreshPlayerName(Player player){
        ServerPlayer sPlayer = PlayerManager.getInstance().getPlayer(player);

        // player is disguised, make them a traveler
        if(!sPlayer.getStringTemp("disguise").equals("")) {
            Permission permission = ServerPlugin.defaultPermission;
            player.setDisplayName(permission.color + sPlayer.getStringTemp("disguise") + ChatColor.RESET);
            player.setPlayerListName(permission.color + sPlayer.getStringTemp("disguise"));
            TagAPI.refreshPlayer(player);
        } else {
            Permission permission = PlayerHelper.getPlayerPermission(player);
            if(permission == null) {
                MessageManager.broadcast(player.getName() + ": My permissions are set wrong massa!");
                return;
            }
            player.setDisplayName(permission.color + player.getName() + ChatColor.RESET);
            player.setPlayerListName(permission.color + player.getName());
            TagAPI.refreshPlayer(player);
        }
    }

    /**
     * Loads a chat channel for a player.
     * @param channel The channel to set it to.
     * @param player The player to set it for.
     */
    public static void loadAChannelForAPlayer(ChatChannel channel, Player player){
        channel.loadChannel(player);
        PlayerManager.getInstance().getPlayer(player).setChannel(channel);
    }

}
