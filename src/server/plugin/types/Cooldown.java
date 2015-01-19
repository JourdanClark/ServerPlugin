package server.plugin.types;

import org.bukkit.entity.Player;

import server.plugin.manager.CooldownManager;

public class Cooldown {
    public String cooldownName, playerName;
    public long   duration, startTime;

    public Cooldown(String cooldownName, Player player, long durationInServerTicks){
        this.cooldownName = cooldownName;
        this.playerName = player.getName();
        this.duration = durationInServerTicks;
        this.startTime = System.currentTimeMillis();
        Object[] cooldown = {this};
        Task.getInstance().delayed(CooldownManager.getInstance().getClass(), "removeCooldown", cooldown, Integer.parseInt(durationInServerTicks + "") - 1);
    }

    /**
     * @return Returns the amount of time remaining in milliseconds.
     */
    public long getTimeRemaining(){
        return Math.abs((((System.currentTimeMillis() - ((duration / 20) * 1000)) - startTime) / 1000));
    }

    /**
     * @return Returns the amount of time left in a readable format. [ex.
     *         "1 hour 34 minutes 5.07 seconds" or "16.87 seconds"]
     */
    @Override
    public String toString(){
        // gets the duration in milliseconds and subtracts that from the start
        // time
        long milliTimeLeft = (long) Math.abs(((System.currentTimeMillis() - ((duration / 20d) * 1000)) - startTime)); // need
                                                                                                                      // to
                                                                                                                      // make
                                                                                                                      // 20
                                                                                                                      // a
                                                                                                                      // double
                                                                                                                      // for
                                                                                                                      // small
                                                                                                                      // durations.
                                                                                                                      // otherwise
                                                                                                                      // the
                                                                                                                      // numbers
                                                                                                                      // are
                                                                                                                      // all
                                                                                                                      // over
                                                                                                                      // because
                                                                                                                      // 20
                                                                                                                      // as
                                                                                                                      // an
                                                                                                                      // int
                                                                                                                      // returns
                                                                                                                      // 0
        int hoursLeft = (int) ((milliTimeLeft / (1000 * 60 * 60)) % 24);
        int minutesLeft = (int) ((milliTimeLeft / (1000 * 60)) % 60);
        int secondsLeft = (int) (milliTimeLeft / 1000) % 60;
        int remainingMilli = Math.abs((int) (milliTimeLeft % 1000));

        String formattedRemaining = "";
        if((remainingMilli + "").length() < 3) // if it would go in the 10ths
                                               // place
            formattedRemaining = ("0" + remainingMilli + "00").substring(0, 2);// the
                                                                               // 00
                                                                               // prevent
                                                                               // it
                                                                               // from
                                                                               // throwing
                                                                               // an
                                                                               // exception
                                                                               // if
                                                                               // there
                                                                               // aren't
                                                                               // enough
                                                                               // 0's
                                                                               // (i
                                                                               // clicked
                                                                               // 200
                                                                               // times
                                                                               // a
                                                                               // second
                                                                               // and
                                                                               // it
                                                                               // threw
                                                                               // an
                                                                               // error
                                                                               // for
                                                                               // the
                                                                               // string
                                                                               // being
                                                                               // too
                                                                               // short)
        else
            formattedRemaining = (remainingMilli + "00").substring(0, 2);// the
                                                                         // 00
                                                                         // prevent
                                                                         // it
                                                                         // from
                                                                         // throwing
                                                                         // an
                                                                         // exception
                                                                         // if
                                                                         // there
                                                                         // aren't
                                                                         // enough
                                                                         // 0's
                                                                         // (i
                                                                         // clicked
                                                                         // 200
                                                                         // times
                                                                         // a
                                                                         // second
                                                                         // and
                                                                         // it
                                                                         // threw
                                                                         // an
                                                                         // error
                                                                         // for
                                                                         // the
                                                                         // string
                                                                         // being
                                                                         // too
                                                                         // short)
        String message = "";

        switch(hoursLeft){
            case 0:
                break;
            case 1:
                message += hoursLeft + " hour ";
                break;
            default:
                message += hoursLeft + " hours ";
        }

        switch(minutesLeft){
            case 0:
                break;
            case 1:
                message += minutesLeft + " minute ";
                break;
            default:
                message += minutesLeft + " minutes ";
        }

        switch(secondsLeft){
            default:
                if(milliTimeLeft >= 0)
                    message += secondsLeft + "." + formattedRemaining + " seconds";
                else
                    message += "0.00 seconds";
        }

        if(message.equals(""))
            return null;
        else
            return message;
    }
}