package server.plugin.combat.spells;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import server.plugin.ServerPlugin;

public class Fireball extends Spell {

    @Override
    public String getName(){
        return "Fireball";
    }

    @Override
    public Material getWand(){
        return Material.STICK;
    }

    @Override
    public Material getTrigger(){
        return Material.FIREBALL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean use(Player player, Object useOn){
        Block b = player.getTargetBlock(null, 3);
        Location loc = new Location(player.getWorld(), b.getLocation().getX(), b.getLocation().getY() + 0.5, b.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
        Entity fireball = player.getLocation().getWorld().spawnEntity(loc, EntityType.FIREBALL);
        fireball.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
        fireball.setVelocity(player.getLocation().getDirection().multiply(6));
        player.getWorld().playEffect(b.getLocation(), Effect.BLAZE_SHOOT, 4);
        player.getWorld().playSound(player.getLocation(), Sound.FIRE, 1, 1);
        return true;
    }

    @Override
    public boolean onHit(Object entity){
        if(entity instanceof Entity) {
            Entity e = (Entity) entity;
            e.removeMetadata("ability", ServerPlugin.getInstance());
            // TODO
            return true;
        }
        return false;
    }

    @Override
    public int getCooldown(){
        // TODO Auto-generated method stub
        return 0;
    }

}
