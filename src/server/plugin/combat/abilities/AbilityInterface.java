package server.plugin.combat.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface AbilityInterface {

    /**
     * Uses the ability.
     * @param player The player using the ability.
     * @param useOn What the player is using the ability on.
     * @return Returns true if the ability was used successfully.
     */
    public boolean use(Player player, Object useOn);

    /**
     * Called when the ability hits the target.
     * @param entity The entity effected by the ability.
     * @return Returns true if the method was successfully run.
     */
    public boolean onHit(Object entity);

    /**
     * @return Returns the name of the ability.
     */
    public String getName();

    /**
     * @return Returns the action taken to use the ability.
     */
    public Material getTrigger();

    /**
     * @return Returns the item used to select the ability in the hotbar.
     */
    public Material getItem();

    /**
     * Be sure to put the cooldown in server ticks because that is what it will
     * be used as! [ex. 1 second equals 20l]
     * @return returns the cooldown for the ability
     */
    public int getCooldown();

}
