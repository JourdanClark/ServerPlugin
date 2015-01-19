package server.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.types.Enums.StatisticEventType;
import server.plugin.types.Enums.StatisticType;

public class StatisticEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private double             amount;
    private StatisticType      type;
    private StatisticEventType eventType;

    private boolean            cancel;

    public StatisticEvent(double amount, StatisticType type, StatisticEventType eventType){
        this.amount = Math.abs(amount);
        this.eventType = eventType;
        this.type = type;
    }

    /**
     * @return Returns the absolute value of the amount of stat gain/loss in the
     *         event.
     */
    public double getAmount(){
        return amount;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * @return Returns the StatisticType involved in the event.
     */
    public StatisticType getType(){
        return type;
    }

    /**
     * @return Returns true if the event was cancelled.
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * @return Returns true if the event is a gain towards their total
     *         statistics, false if it subtracts from it.
     */
    public boolean isStatisticGain(){
        if(eventType == StatisticEventType.GAIN)
            return true;
        else
            return false;
    }

    /**
     * Cancels the event if the event was fired correctly.
     * 
     * @param cancel Whether or not to cancel the event.
     */
    public void setCancelled(boolean cancel){
        this.cancel = cancel;
    }
}
