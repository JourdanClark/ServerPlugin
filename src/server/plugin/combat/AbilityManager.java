package server.plugin.combat;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import server.plugin.combat.abilities.Ability;
import server.plugin.combat.spells.Fireball;
import server.plugin.combat.spells.Launch;
import server.plugin.combat.spells.LightningStrike;
import server.plugin.combat.spells.Push;

public class AbilityManager {
    /**
     * Keeps track of the available abilities on the server.
     */
    private static ArrayList<Ability> abilities = new ArrayList<Ability>();

    private static AbilityManager     instance  = new AbilityManager();

    public static AbilityManager getInstance(){
        return instance;
    }

    private AbilityManager(){

        // Initialize the executors
        abilities.add(new Push());
        abilities.add(new Fireball());
        abilities.add(new LightningStrike());
        abilities.add(new Launch());

    }

    /**
     * Called when the player uses an ability.
     * @param event
     */
    public void onAbilityUse(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(abilitiyUsed(player)) {
            Ability ability = getAbility(player.getItemInHand().getType(), getNextSlotItem(player));
            ability.use(player, event.getClickedBlock());
        }

    }
    
    /**
     * Called when the player uses an ability.
     * @param event
     */
    public void onAbilityUse(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(abilitiyUsed(player)) {
            Ability ability = getAbility(player.getItemInHand().getType(), getNextSlotItem(player));
            ability.use(player, event.getRightClicked());
        }

    }

    /**
     * Checks to see if an ability was used.
     * @param player The player in question.
     * @return Returns true if the player used an ability.
     */
    private boolean abilitiyUsed(Player player){
        Material inHand = player.getItemInHand().getType();
        Material nextSlot = getNextSlotItem(player);

        if(nextSlot == Material.AIR || nextSlot == null) {
            return false;
        }

        for(Ability a : abilities) {
            if(a.getItem() == inHand) {
                if(a.getTrigger() == nextSlot) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the ability that can be used with the item in their hand and
     * the item in their hotbar.
     * @param item The item in the player's hand.
     * @param trigger The item to the right of their hand.
     * @return Returns the ability that can be used.
     */
    private Ability getAbility(Material item, Material trigger){
        for(Ability a : abilities) {
            if(a.getItem() == item) {
                if(a.getTrigger() == trigger) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * A method to find the trigger.
     * @param player
     * @return Returns the item to the right of their hand.
     */
    private Material getNextSlotItem(Player player){
        Inventory inv = player.getInventory();
        Material inHand = player.getItemInHand().getType();
        Material nextSlot = Material.AIR;

        for(int i = 0;i < 9;i++) {
            if(inv.getItem(i) != null && inv.getItem(i).getType() == inHand) {
                if(inv.getItem(i + 1) != null)
                    nextSlot = inv.getItem(i + 1).getType();
                break;
            }
        }
        return nextSlot;
    }

    /**
     * @param name The name of the ability.
     * @return Returns the ability with the given name.
     */
    public Ability getAbilityFromName(String name){
        for(Ability a : abilities) {
            if(a.getName() == name)
                return a;
        }
        return null;
    }

}
