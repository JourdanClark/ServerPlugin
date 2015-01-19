package server.plugin.combat.spells;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import server.plugin.ServerPlugin;

public class Launch extends Spell {

    @Override
    public boolean use(Player player, Object useOn){
        if(checkItemHolding(player)) {
            player.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
            player.setMetadata("immune", new FixedMetadataValue(ServerPlugin.getInstance(), "damage"));
            player.setMetadata("allowed", new FixedMetadataValue(ServerPlugin.getInstance(), "flight"));
            player.setVelocity(player.getLocation().getDirection().multiply(5));
            player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, 4);
            player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
            return true;
        }
        return false;
    }

    @Override
    public String getName(){
        return "Launch";
    }

    @Override
    public int getCooldown(){
        return 2000;
    }

    @Override
    public Material getWand(){
        return Material.STICK;
    }

    @Override
    public Material getTrigger(){
        return Material.ARROW;
    }

    @Override
    public boolean onHit(Object entity){
        if(entity instanceof Entity) {
            Entity e = (Entity) entity;
            e.removeMetadata("ability", ServerPlugin.getInstance());
            e.removeMetadata("immune", ServerPlugin.getInstance());
            e.removeMetadata("allowed", ServerPlugin.getInstance());
            return true;
            // TODO
        }
        return false;
    }
}
