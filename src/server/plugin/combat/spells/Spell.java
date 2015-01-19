package server.plugin.combat.spells;

import org.bukkit.Material;

import server.plugin.combat.abilities.Ability;

public abstract class Spell extends Ability implements SpellInterface {

    /**
     * Returns the item in the player's hand.
     */
    @Override
    public Material getItem(){
        return getWand();
    }
}
