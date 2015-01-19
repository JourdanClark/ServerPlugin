package server.plugin.types;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import server.plugin.common.StatsHelper;
import server.plugin.manager.DataManager;

public class Enums {

    public enum ChatType{
        WHISPER, 
        CHAT, 
        BROADCAST
    }

    public enum DebugType{
        UNKNOWN, 
        FILE, 
        ERROR, 
        GENERAL, 
        MINIGAME;
    }

    public enum Permission{
        LEPPER(100, "Lepper", "" + ChatColor.BLACK), // Previously banned person - Third xp/points
        TRAVELER(200, "Traveler", "" + ChatColor.GRAY), // Default class, basic /tpa commands - Half xp/points
        PEASANT(300, "Peasant", "" + ChatColor.WHITE), // Linked accounts on forum - Full xp/points - /sethome /home
        SQUIRE(400, "Squire", "" + ChatColor.BLUE), // Donator
        BARON(500, "Baron", "" + ChatColor.RED), // Someone who owns their own property
        KNIGHT(600, "Knight", "" + ChatColor.DARK_RED), // Level 1 ADMIN - Jail/Mute - temp
        PALADIN(700, "Paladin", "" + ChatColor.DARK_GREEN), // Level 2 ADMIN - Jail/Mute/Kick/Ban - temp gamemode
        MAGE(800, "Mage", "" + ChatColor.DARK_PURPLE), // Level 3 ADMIN - Jail/Mute/Kick/Ban/WorldEdit - Permannt
        JESTER(900, "Jester", "" + ChatColor.DARK_AQUA), // Ben, Neil, Jaxson
        KING(1000, "King", "" + ChatColor.GOLD); // Derrik, Jourdan

        public int level;
        public String name, color;

        public static Permission getPermission(String name){
            for(Permission p : Permission.values())
                if(p.name.equalsIgnoreCase(name))
                    return p;
            return null;
        }
        
        public static Permission getPermission(int level){
            for(Permission p : Permission.values())
                if(p.level == level)
                    return p;
            return null;
        }
        
        Permission(int level, String name, String color){
            this.level = level;
            this.name = name;
            this.color = color;
        }

        /**
         * Compares to see if type1 is 'more powerful' than type 2
         */
        public boolean isGreaterThanOrEqual(Permission type){

            if(type.level >= level)
                return true;

            return false;
        }

        public boolean isLessThanOrEqual(Permission type){

            if(type.level <= level)
                return true;

            return false;
        }

        /**
         * @return Returns the type name formatted with the type color.
         */
        @Override
        public String toString(){
            return color + name;
        }
    }

    public enum ServerTeleportCause{
        UNKNOWN, 
        DEMAND_COMMAND, 
        ACCEPT_COMMAND, 
        REQUEST_COMMAND, 
        WAITING_ROOM, 
        GAME_END, 
        GAME_RESPAWN, 
        GAME_LOSE, 
        GAME_WIN, 
        GAME_OTHER;
    }

    public enum StatisticEventType{
        GAIN, 
        LOSS
    }

    public enum StatisticType{

        TOTAL_GAME_COINS_RECEIVED("Total Game Coins Received"),

        BLOCKS_PLACED("Blocks Placed"),

        PLAYERS_KILLED("Players Killed", EntityType.PLAYER), 
        VILLAGERS_KILLED("Villagers Killed", EntityType.VILLAGER), 
        SNOW_GOLEMS_KILLED("Snow Golems Killed", EntityType.SNOWMAN), 
        IRON_GOLEMS_KILLED("Iron Golems Killed", EntityType.IRON_GOLEM),

        IRON_MINED("Iron Mined", Material.IRON_ORE), 
        GOLD_MINED("Gold Mined", Material.GOLD_ORE), 
        DIAMONDS_MINED("Diamonds Mined", Material.DIAMOND_ORE), 
        EMERALDS_MINED("Emeralds Mined", Material.EMERALD_ORE),

        CHICKENS_KILLED("Chickens Killed", EntityType.CHICKEN), 
        COWS_KILLED("Cows Killed", EntityType.COW), 
        OCELOTS_KILLED("Ocelots Killed", EntityType.OCELOT), 
        PIGS_KILLED("Pigs Killed", EntityType.PIG), 
        SHEEP_KILLED("Sheep Killed", EntityType.SHEEP), 
        HORSES_KILLED("Horses Killed", EntityType.HORSE), 
        SQUID_KILLED("Squid Killed", EntityType.SQUID), 
        BATS_KILLED("Bats Killed", EntityType.BAT), 
        MOOSHROOMS_KILLED("Mooshrooms Killed", EntityType.MUSHROOM_COW), 
        WOLVES_KILLED("Wolves Killed", EntityType.WOLF),

        ENDERMEN_KILLED("Endermen Killed", EntityType.ENDERMAN), 
        ZOMBIE_PIGMEN_KILLED("Zombie Pigmen Killed", EntityType.PIG_ZOMBIE), 
        BLAZES_KILLED("Blazes Killed", EntityType.BLAZE), 
        CAVE_SPIDERS_KILLED("Cave Spiders Killed", EntityType.CAVE_SPIDER), 
        CREEPERS_KILLED("Creepers Killed", EntityType.CREEPER), 
        GHASTS_KILLED("Ghasts Killed", EntityType.GHAST), 
        MAGMA_CUBES_KILLED("Magma Cubes Killed", EntityType.MAGMA_CUBE), 
        SILVERFISH_KILLED("Silverfish Killed", EntityType.SILVERFISH), 
        SKELETONS_KILLED("Skeletons Killed", EntityType.SKELETON), 
        SLIMES_KILLED("Slimes Killed", EntityType.SLIME), 
        SPIDERS_KILLED("Spiders Killed", EntityType.SPIDER), 
        WITCHES_KILLED("Witches Killed", EntityType.WITCH), 
        WITHER_SKELETONS_KILLED("Wither Skeletons Killed", EntityType.SKELETON), 
        ZOMBIES_KILLED("Zombies Killed", EntityType.ZOMBIE), 
        ZOMBIE_VILLAGERS_KILLED("Zombie Villagers Killed", EntityType.ZOMBIE),

        ENDER_DRAGONS_KILLED("Ender Dragons Killed", EntityType.ENDER_DRAGON), 
        WITHERS_KILLED("Withers Killed", EntityType.WITHER),

        DAMAGE_DEALT("Damage Dealt"), 
        DAMAGE_TAKEN("Damage Taken"),

        WOLVES_TAMED("Wolves Tamed", EntityType.WOLF), 
        HORSES_TAMED("Horses Tamed", EntityType.HORSE), 
        OCELOTS_TAMED("Ocelots Tamed", EntityType.OCELOT);

        public static StatisticType findStatisticTypeByListenType(Object listenType){
            for(StatisticType type : StatisticType.values()) {
                if(listenType == type.listenFor) {
                    return type;
                }
            }
            return null;
        }

        public static StatisticType findStatisticTypeByName(String name){
            for(StatisticType type : StatisticType.values()) {
                if(name == type.name) {
                    return type;
                }
            }
            return null;
        }

        public static StatisticType findStatisticTypeByID(int id){
            ResultSet set = DataManager.getInstance().querySQL(
                    "SELECT id, name FROM statistic_types"
                    );
            try {
                while(set.next()){
                    if(set.getInt("id") == id){
                        String name = set.getString("name").replace("_", " ");
                        for(StatisticType type : StatisticType.values()){
                            if(type.name == name)
                                return type;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String name;
        public Object listenFor;

        StatisticType(String name){
            this.name = name;
        }

        StatisticType(String name, Object listenFor){
            this.name = name;
            this.listenFor = listenFor;
        }

        public void decreaseStat(String player, double amount){
            StatsHelper.decreasePlayerStatistic(player, this, amount);
        }

        public void increaseStat(String player, double amount){
            StatsHelper.increasePlayerStatistic(player, this, amount);
        }

        public void decreaseStat(Player player, double amount){
            StatsHelper.decreasePlayerStatistic(player, this, amount);
        }

        public void increaseStat(Player player, double amount){
            StatsHelper.increasePlayerStatistic(player, this, amount);
        }

        public void decreaseStat(ServerPlayer player, double amount){
            StatsHelper.decreasePlayerStatistic(player, this, amount);
        }

        public void increaseStat(ServerPlayer player, double amount){
            StatsHelper.increasePlayerStatistic(player, this, amount);
        }
    }

    public enum ScoreboardType{
        TITLE(DisplaySlot.BELOW_NAME), 
        SIDEBAR(DisplaySlot.SIDEBAR);

        DisplaySlot display;

        ScoreboardType(DisplaySlot display){
            this.display = display;
        }

        public DisplaySlot getDisplay(){
            return display;
        }
    }

    public enum ObjectiveType{
        PERMISSION
    }

    /**
     * Usage: MessageType.SEVERE.format( "Holy cow" ) Output: ChatColor.RED +
     * "Holy cow!"
     */
    public enum MessageType{
        /**
         * Color: RED <br/>
         * Format: "%s!"
         * @param Message
         */
        SEVERE(ChatColor.RED + "%s!"),

        /**
         * Color: YELLOW <br/>
         * Format: "%s"
         * @param Message
         */
        INFO(ChatColor.YELLOW + "%s"),

        /**
         * Color: ITALIC <br/>
         * Format: "%s"
         * @param Message
         */
        SUBINFO(ChatColor.ITALIC + "%s"),

        /**
         * Color: GREEN <br/>
         * Format: "%s"
         * @param Message
         */
        GOOD(ChatColor.GREEN + "%s"),

        /**
         * Color: MultiColor <br/>
         * Format: "[%s: %s] %s"
         * @param To /From
         * @param playerName
         * @param Message
         */
        WHISPER(ChatColor.AQUA + "" + ChatColor.ITALIC + "[%s: %s] " + ChatColor.WHITE + "%s"),

        /**
         * Color: AQUA <br/>
         * Format: "%s"
         * @param Message
         */
        TPA(ChatColor.AQUA + "%s"),

        /**
         * Color: None <br/>
         * Format: "%s"
         * @param Message
         */
        CUSTOM("%s");

        private String template;

        MessageType(String template){
            this.template = template;
        }

        /**
         * Format the chat type based upon the given arguments.
         * 
         * @param args - Make sure the arguments you pass meet the format for
         *            that type
         * @return The properly formatted string
         */
        public String format(Object... args){
            return String.format(this.template, args);
        }
    }

    public enum Databases{
        MINECRAFT("minecraft", "minecraft", "thisisapassword", "3306", "71.195.202.184"), ;
        public String name, user, password, port, hostName;

        Databases(String DatabaseName, String user, String password, String port, String hostName){
            this.name = DatabaseName;
            this.user = user;
            this.password = password;
            this.port = port;
            this.hostName = hostName;
        }
    }

    public enum PunishmentType{
        KICK("Kick", "Kicked"), 
        TEMPBAN("Tempban", "Tempbanned"), 
        BAN("Ban", "Banned");

        String name, pasttense;

        PunishmentType(String name, String pasttense){
            this.name = name;
            this.pasttense = pasttense;
        }
    }

    public enum Setting{
        CENSOR("censor");

        String name;

        Setting(String name){
            this.name = name;
        }
    }

    public enum DatabaseFieldType{
        BIT("BIT"),
        TINYINT("TINYINT"),
        SMALLINT("SMALLINT"),
        MEDIUMINT("MEDIUMINT"),
        INT("INT"),
        BIGINT("BIGINT"),
        FLOAT("FLOAT"),
        DOUBLE("DOUBLE"),
        DATE("DATE"),
        DATETIME("DATETIME"),
        TIMESTAMP("TIMESTAMP"),
        TIME("TIME"),
        YEAR("YEAR"),
        CHAR("CHAR"),
        VARCHAR("VARCHAR"),
        TINYBLOB("TINYBLOB"),
        TINYTEXT_BLOB("TINYTEXT BLOB"),
        LONGTEXT_ENUM("LONGTEXT ENUM"),
        SET("SET");
        
        public String type;
        
        DatabaseFieldType(String type){
            this.type = type;
        }
        public String setLength(int length){
            return String.format(type + "(%s)", length);
        }
        @Override
        public String toString(){
            return type;
        }
    }
}


