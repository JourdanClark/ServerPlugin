package server.plugin.common;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import server.plugin.database.tables.MinecraftUsersTable;
import server.plugin.database.tables.StatisticTypesTable;
import server.plugin.database.tables.StatisticsTable;
import server.plugin.event.StatisticEvent;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.StatisticEventType;
import server.plugin.types.Enums.StatisticType;
import server.plugin.types.ServerPlayer;

public class StatsHelper {

    /**
     * @param player The player in question.
     * @param type The statistic type to get data from.
     * @return Returns the count for the statistic.
     */
    private static Object getStat(ServerPlayer player, StatisticType type){
        int statTypeID = StatisticTypesTable.getStatisticTypeID(type);
        int playerID = MinecraftUsersTable.getPlayerID(player.getName());
        HashMap<String, Object> data = StatisticsTable.getStatisticData(StatisticsTable.getStatisticID(statTypeID, playerID));
        return data.get(StatisticsTable.values.COUNT.toString());
    }

    /**
     * Decreases the statistic by the given amount.
     * @param player
     * @param type
     * @param amount
     */
    private static void decStat(ServerPlayer player, StatisticType type, double amount){
        int statTypeID = StatisticTypesTable.getStatisticTypeID(type);
        int playerID = MinecraftUsersTable.getPlayerID(player.getName());
        HashMap<String, Object> data = StatisticsTable.getStatisticData(StatisticsTable.getStatisticID(statTypeID, playerID));
        String count = StatisticsTable.values.COUNT.toString();
        data.put(count, (int)data.get(count) - Math.abs(amount));
        StatisticsTable.setStatisticData(data);
    }

    /**
     * Increases the statistic by the given amount.
     * @param player
     * @param type
     * @param amount
     */
    private static void incStat(ServerPlayer player, StatisticType type, double amount){
        int statTypeID = StatisticTypesTable.getStatisticTypeID(type);
        int playerID = MinecraftUsersTable.getPlayerID(player.getName());
        HashMap<String, Object> data = StatisticsTable.getStatisticData(StatisticsTable.getStatisticID(statTypeID, playerID));
        String count = StatisticsTable.values.COUNT.toString();
        data.put(count, (long)data.get(count) + Math.abs(amount));
        StatisticsTable.setStatisticData(data);
        
    }

    /**
     * @param player The ServerPlayer you would like to decrease the statistics
     *            of.
     * @param type The StatisticType to decrease.
     * @param amount The amount to decrease the statistic by.
     */
    public static void decreasePlayerStatistic(ServerPlayer player, StatisticType type, double amount){
        StatisticEvent event = new StatisticEvent(amount, type, StatisticEventType.LOSS);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled())
            decStat(player, type, amount);
    }

    /**
     * @param player The Player you would like to decrease the statistics of.
     * @param type The StatisticType to decrease.
     * @param amount The amount to decrease the statistic by.
     */
    public static void decreasePlayerStatistic(Player player, StatisticType type, double amount){
        StatisticEvent event = new StatisticEvent(amount, type, StatisticEventType.LOSS);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled())
            decStat(PlayerManager.getInstance().getPlayer(player), type, amount);
    }

    /**
     * @param player The name of the Player you would like to decrease the
     *            statistics of.
     * @param type The StatisticType to decrease.
     * @param amount The amount to decrease the statistic by.
     */
    public static void decreasePlayerStatistic(String player, StatisticType type, double amount){
        StatisticEvent event = new StatisticEvent(amount, type, StatisticEventType.LOSS);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled())
            decStat(PlayerManager.getInstance().getPlayer(player), type, amount);
    }

    /**
     * @param player The ServerPlayer to get the statistic from.
     * @param type The type of statistic to retrieve.
     * @return Returns the statistic in the form of an object.
     */
    public static Object getStatistic(ServerPlayer player, StatisticType type){
        return getStat(player, type);
    }

    /**
     * @param player The Player to get the statistic from.
     * @param type The type of statistic to retrieve.
     * @return Returns the statistic in the form of an object.
     */
    public static Object getStatistic(Player player, StatisticType type){
        return getStat(PlayerManager.getInstance().getPlayer(player), type);
    }

    /**
     * @param player The name of the Player to get the statistic from.
     * @param type The type of statistic to retrieve.
     * @return Returns the statistic in the form of an object.
     */
    public static Object getStatistic(String player, StatisticType type){
        return getStat(PlayerManager.getInstance().getPlayer(player), type);
    }

    /**
     * @param player The ServerPlayer you would like to increase the statistics
     *            of.
     * @param type The type of statistic to increase.
     * @param amount The amount to increase the statistic by.
     */
    public static void increasePlayerStatistic(ServerPlayer player, StatisticType type, double amount){
        StatisticEvent event = new StatisticEvent(amount, type, StatisticEventType.GAIN);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled())
            incStat(player, type, amount);
    }

    /**
     * @param player The Player you would like to increase the statistics of.
     * @param type The type of statistic to increase.
     * @param amount The amount to increase the statistic by.
     */
    public static void increasePlayerStatistic(Player player, StatisticType type, double amount){
        StatisticEvent event = new StatisticEvent(amount, type, StatisticEventType.GAIN);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled())
            incStat(PlayerManager.getInstance().getPlayer(player), type, amount);
    }

    /**
     * @param player The name of the Player you would like to increase the
     *            statistics of.
     * @param type The type of statistic to increase.
     * @param amount The amount to increase the statistic by.
     */
    public static void increasePlayerStatistic(String player, StatisticType type, double amount){
        StatisticEvent event = new StatisticEvent(amount, type, StatisticEventType.GAIN);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled())
            incStat(PlayerManager.getInstance().getPlayer(player), type, amount);
    }
}