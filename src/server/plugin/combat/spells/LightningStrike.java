package server.plugin.combat.spells;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import server.plugin.ServerPlugin;

public class LightningStrike extends Spell {

    @Override
    public String getName(){
        return "Lightning Strike";
    }

    @Override
    public Material getWand(){
        return Material.STICK;
    }

    @Override
    public Material getTrigger(){
        return Material.REDSTONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean use(Player player, Object useOn){
        Bukkit.getServer().broadcastMessage("use");
        Block b = player.getTargetBlock(null, 1);
        Item block = b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.REDSTONE));
        Bukkit.getServer().broadcastMessage("launch");
        block.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
        Vector vector = player.getLocation().getDirection().multiply(1.5);
        vector.setY(0.2);
        block.setVelocity(vector);
        player.getWorld().playEffect(block.getLocation(), Effect.BOW_FIRE, 4);
        player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
        return true;
    }

    @Override
    public boolean onHit(Object entity){
        if(entity instanceof Entity) {
            Entity e = (Entity) entity;
            e.removeMetadata("ability", ServerPlugin.getInstance());
            e.getLocation().getWorld().strikeLightning(e.getLocation());
            return true;
        }
        return false;
    }

    @Override
    public int getCooldown(){
        return 0;
    }
}
