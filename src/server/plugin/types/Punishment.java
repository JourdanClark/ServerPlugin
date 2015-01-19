package server.plugin.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import server.plugin.common.HelperFunctions;
import server.plugin.common.PlayerHelper;
import server.plugin.types.Enums.PunishmentType;

public class Punishment {

    private Player         player, admin;
    private PunishmentType type;
    private String         reason = "No reason given";
    private long           time;
    private String         emailSubject = "", emailBody = "";

    /**
     * Creates a new Punishment
     * 
     * @param player The player being kicked/banned
     * @param admin The player who issued the punishment
     * @param type The PunishmentType (kick/ban/tempban)
     * @param reason The reason given
     * @param time The time the punishment was given
     */
    public Punishment(Player player, Player admin, PunishmentType type, String reason, long time){
        this.player = player;
        this.admin = admin;
        this.type = type;
        this.time = time;
        if(!reason.equals(null))
            this.reason = reason;
    }

    /**
     * This will kick, tempban, or ban the player based on the PunishmentType.
     * Pass in a duration for tempbans.
     * 
     * @param duration How long tempbans should be. Everything else ignores this
     *            variable.
     */
    public void invokePunishment(long duration){
        switch(type){
            case KICK:
                player.kickPlayer("You have been kicked by " + admin.getName() + " for: " + reason);
                sendAdminEmail();
                // add offense to database
                break;
            case TEMPBAN:
                // add offense to database
                break;
            case BAN:
                // add offense to database
                break;
            default:
                break;
        }
    }

    private void sendAdminEmail(){
        if(emailBody.equals(""))
            createEmail();
        // send email
        Bukkit.broadcastMessage("Subject: " + emailSubject);
        Bukkit.broadcastMessage("Email: " + emailBody);
    }

    private void createEmail(){
        emailSubject = player.getName() + " was " + type.pasttense + " by " + admin.getName();

        String[] dateTime = HelperFunctions.timeToDate(time);
        emailBody = "On " + dateTime[0] + " at " + dateTime[1] + // On Thursday,
                                                                 // October
                                                                 // 10th,
                                                                 // 2013 at
                                                                 // 1:39am
        ",  the " + PlayerHelper.getPlayerPermission(admin).name + " '" + admin.getName() + "' " + // ,
                                                                                                   // the
                                                                                                   // King
                                                                                                   // 'SuckyComedian'
        type.pasttense.toLowerCase() + " " + player.getName() + // kicked
                                                                // minidude22
        " for: \"" + ChatColor.stripColor(reason) + "\""; // for:
                                                          // "Being a bitch."
    }
}
