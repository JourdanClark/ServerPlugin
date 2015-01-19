package server.plugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.MessageType;

public class WhisperEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String                   message;
    private Player                   from, to;
    private boolean                  cancel   = false;

    public WhisperEvent(Player from, Player to, String message){
        this.message = message;
        this.from = from;
        this.to = to;
        sendWhisper();
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    /**
     * @return Returns the message involved in the event.
     */
    public String getMessage(){
        return message;
    }

    /**
     * @return Returns the Player sending the message.
     */
    public Player getPlayerFrom(){
        return from;
    }

    /**
     * @return Returns the Player receiving the message.
     */
    public Player getPlayerTo(){
        return to;
    }

    /**
     * @return True if the event was cancelled.
     */
    public boolean isCancelled(){
        return cancel;
    }

    /**
     * Sends the message if the event was not cancelled.
     */
    private void sendWhisper(){
        if(!cancel) {
            MessageManager manager = MessageManager.getInstance();

            manager.sendMessage(to, MessageType.WHISPER, "From", PlayerHelper.checkDisguise(from.getName()), message);
            manager.sendMessage(from, MessageType.WHISPER, "To", PlayerHelper.checkDisguise(to.getName()), message);

            if(PlayerManager.getInstance().getPlayer(to).getTemp("whisper.reply").equals("")) {
                manager.sendMessage(to, MessageType.SUBINFO, "Reply with /r <message>");
            }

            PlayerManager.getInstance().getPlayer(to).setTemp("whisper.reply", from.getName());

        }
    }

    /**
     * Stops the whisper from taking place if true.
     * 
     * @param cancel Whether or not to stop the whisper.
     */
    public void setCancelled(boolean cancel){
        this.cancel = cancel;
    }

}
