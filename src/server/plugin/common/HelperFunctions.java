package server.plugin.common;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HelperFunctions {

    private static HashMap<String, ChatColor> colors = new HashMap<String, ChatColor>();

    public static void init(){
        colors.put("%aqu", ChatColor.AQUA);
        colors.put("%bla", ChatColor.BLACK);
        colors.put("%blu", ChatColor.BLUE);
        colors.put("%pur", ChatColor.DARK_PURPLE);
        colors.put("%pin", ChatColor.LIGHT_PURPLE);
        colors.put("%red", ChatColor.RED);
        colors.put("%whi", ChatColor.WHITE);
        colors.put("%yel", ChatColor.YELLOW);
        colors.put("%gre", ChatColor.GREEN);
        colors.put("%gra", ChatColor.GRAY);
        colors.put("%ita", ChatColor.ITALIC);
        colors.put("%bol", ChatColor.BOLD);
        colors.put("%res", ChatColor.RESET);
    }

    /**
     * @param start The starting Location.
     * @return Returns the block on the ground if the given location is in the
     *         air.
     */
    public static Location getGroundBlock(Location start){
        Location ground = start;
        while(ground.getBlock().getType().equals(Material.AIR)) {
            ground.setY(ground.getY() - 1);
        }
        ground.setY(ground.getY() + 1);
        return ground;
    }

    /**
     * @param a An integer array.
     * @return Returns the max value from the given integer array.
     */
    public static int min(int[] a){
        int res = Integer.MAX_VALUE;

        for(int i = 0;i < a.length;i++) {
            res = Math.min(a[i], res);
        }

        return res;
    }

    /**
     * Safely converts a Long to an Integer.
     * 
     * @param l The Long in question.
     * @return Returns an Integer if it could safely convert the Long. Otherwise
     *         it throws an exception.
     */
    public static int safeLongToInt(long l){
        if(l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * Creates a location from an object array where the first part of the array
     * is the world name followed by the x, y, and z. Optionally, you may add
     * the yaw and pitch to the array.
     * 
     * @param worldXYZ The object array
     * @return Returns a location if the array is large enough (at least 4 but
     *         no greater than 6). Otherwise, it returns null.
     */
    public static Location createLocationFromPoints(Object[] worldXYZ){
        if(worldXYZ.length < 4)
            return null;
        if(worldXYZ.length == 4)
            return new Location(Bukkit.getServer().getWorld((String) worldXYZ[0]), (double) worldXYZ[1], (double) worldXYZ[2], (double) worldXYZ[3]);
        if(worldXYZ.length == 6)
            return new Location(Bukkit.getServer().getWorld((String) worldXYZ[0]), (double) worldXYZ[1], (double) worldXYZ[2], (double) worldXYZ[3], (float) worldXYZ[4], (float) worldXYZ[5]);
        return null;
    }

    /**
     * Creates a location from an object array where the first part of the array
     * is the world name followed by the x, y, and z
     * 
     * @param worldXYZ The object array
     * @param player The player to use for the pitch and yaw
     * @return Returns a location if the array is the correct size (at least 4).
     *         Otherwise, it returns null.
     */
    public static Location createLocationFromPoints(Object[] worldXYZ, Player player){
        if(worldXYZ.length < 4)
            return null;
        return new Location(Bukkit.getServer().getWorld((String) worldXYZ[0]), (double) worldXYZ[1], (double) worldXYZ[2], (double) worldXYZ[3], player.getLocation().getYaw(), player.getLocation().getPitch());
    }

    /**
     * @param loc The location involved.
     * @param size The size of the array. Should be either 4 or 6.
     * @return Returns an Object array containing the world name, x, y, and z if
     *         the size is four. Also returns the yaw and pitch if the size is
     *         6.
     */
    public static Object[] getArrayFromLocation(Location loc, int size){
        if(size == 4)
            return new Object[]{loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()};
        if(size == 6)
            return new Object[]{loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch()};
        return null;
    }

    /**
     * @param whatToGet What you are looking for in the array.
     * @return Returns the location of the object in the array. If nothing is
     *         found, it returns -1;
     */
    public static int getLocationInArray(Object[] array, Object whatToGet){
        int i = 0;
        for(Object ob : array) {
            if(ob.equals(whatToGet))
                return i;
            i++;
        }
        return -1;
    }

    /**
     * Combines the String Array into a single string.
     * 
     * @param args The String Array.
     * @return Returns the string from the array or null if it had a problem.
     */
    public static String combineString(String[] args){
        String string = "";
        for(int i = 0;i < args.length;i++) {
            if(i != args.length - 1)
                string += args[i] + " ";
            else
                string += args[i];
        }
        if(string.equals(""))
            return null;
        else
            return string;
    }

    /**
     * Combines the String Array into a single string.
     * 
     * @param args The String Array.
     * @param start Where to start from.
     * @return Returns the string from the array or null if it had a problem.
     */
    public static String combineString(String[] args, int start){
        String string = "";
        for(int i = start;i < args.length;i++) {
            if(i != args.length - 1)
                string += args[i] + " ";
            else
                string += args[i];
        }
        if(string.equals(""))
            return null;
        else
            return string;
    }

    /**
     * Looks through the String to find color codes which come from a HashMap in
     * this class.
     * 
     * @param string The String to check.
     * @return Returns the String formatted with ChatColors if there were any.
     */
    public static String formatColoredString(String string){
        String[] st = string.split(" ");
        String message = string;

        if(string.equals(null))
            return null;

        if(st.length == 0) {
            for(String color : colors.keySet()) {
                message = message.replaceAll(color, colors.get(color).toString());
            }
            return message;
        }

        for(int i = 0;i < st.length;i++) {
            for(String color : colors.keySet()) {
                st[i] = st[i].replaceAll(color, colors.get(color).toString());
            }
        }
        return combineString(st);
    }

    /**
     * Looks through the String Array to find color codes which come from a
     * HashMap in this class.
     * 
     * @param string The String to check.
     * @return Returns the String formatted with ChatColors if there were any.
     */
    public static String formatColoredString(String[] string){
        String[] message = string;
        if(string.equals(null))
            return null;
        if(string.length == 1) {
            for(String s : colors.keySet()) {
                message[0] = message[0].replaceAll(s, colors.get(s).toString());
            }
            return combineString(message);
        }
        for(int i = 0;i < message.length;i++) {
            for(String color : colors.keySet()) {
                message[i] = message[i].replaceAll(color, colors.get(color).toString());
            }
        }
        return combineString(message);
    }

    /**
     * Converts time (such as System.currentTimeMills()) to something like [0] =
     * Thursday, October 10, 2013 [1] = 12:56am.
     * 
     * @param time Time should be in milliseconds.
     * @return Returns a string array where the first string is the date and the
     *         second is the time of day.
     */
    public static String[] timeToDate(long Time){
        String time, date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Time);

        // split up what cal.getTime() is to get the date and time strings
        String[] split = cal.getTime().toString().split(" ");

        // make the day into the full name
        switch(split[0].toLowerCase()){
            case "sun":
                split[0] = "Sunday";
                break;
            case "mon":
                split[0] = "Monday";
                break;
            case "tue":
                split[0] = "Tuesday";
                break;
            case "wed":
                split[0] = "Wednesday";
                break;
            case "thu":
                split[0] = "Thursday";
                break;
            case "fri":
                split[0] = "Friday";
                break;
            case "sat":
                split[0] = "Saturday";
                break;
            default:
                break;
        }

        // make the month into the full name
        switch(split[1].toLowerCase()){
            case "jan":
                split[1] = "January";
                break;
            case "feb":
                split[1] = "Febuary";
                break;
            case "mar":
                split[1] = "March";
                break;
            case "apr":
                split[1] = "April";
                break;
            case "may":
                split[1] = "May";
                break;
            case "jun":
                split[1] = "June";
                break;
            case "jul":
                split[1] = "July";
                break;
            case "aug":
                split[1] = "August";
                break;
            case "sep":
                split[1] = "September";
                break;
            case "oct":
                split[1] = "October";
                break;
            case "nov":
                split[1] = "November";
                break;
            case "dec":
                split[1] = "December";
                break;
            default:
                break;
        }

        // create date string
        date = split[0] + ", " + split[1] + " " + split[2] + ", " + split[5];

        // create the time string
        split = split[3].split(":"); // hour:minute:second

        int hour = 0, minute = 0;
        String ampm = "am";

        try {
            hour = Integer.parseInt(split[0]);
            minute = Integer.parseInt(split[1]);
        } catch(Exception e) {
        }

        if(hour > 12) {
            hour -= 12;
            ampm = "pm";
        } else {
            if(hour == 0) {
                hour = 12;
            }
        }

        String hourString = (hour < 10) ? "0" + hour : hour + "";
        String minuteString = (minute < 10) ? "0" + minute : minute + "";

        time = hourString + ":" + minuteString + ampm;

        return new String[]{date, time};
    }

    /**
     * @param string The message to check.
     * @return Returns true if the message contains special characters.
     */
    public static boolean containsSpecialCharacter(String string){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }
}
