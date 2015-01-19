package server.plugin.combat.abilities;

import org.bukkit.entity.Player;
import server.plugin.manager.CooldownManager;
import server.plugin.manager.MessageManager;
import server.plugin.types.Cooldown;
import server.plugin.types.Enums.MessageType;

public abstract class Ability implements AbilityInterface {

    /**
     * @return Returns true if the item in the hand is the item used for the ability.
     */
    public boolean checkItemHolding(Player player){
        if(player.getItemInHand().getType() == getItem()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a cooldown for the player for the ability.
     * @param player
     */
    public void addCooldown(Player player){
        CooldownManager.getInstance().addCooldown(player, getName(), getCooldown());
    }

    /**
     * @param player
     * @return Returns true if the player has a cooldown for the ability.
     */
    public boolean hasCooldown(Player player){
        if(CooldownManager.getInstance().getCooldown(player.getName(), getName()) != null)
            return true;
        return false;
    }

    /**
     * Sends a message telling the player they have a cooldown for the ability.
     * @param player
     */
    public void sendCooldownMessage(Player player){
        Cooldown cooldown = CooldownManager.getInstance().getCooldown(player.getName(), getName());
        if(cooldown != null)
            MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "[Cooldown] " + "You must wait " + cooldown + " to use this ability");
    }
}
