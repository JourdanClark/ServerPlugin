package server.plugin.combat.spells;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import server.plugin.ServerPlugin;

public class Push extends Spell {

    @SuppressWarnings("deprecation")
    @Override
    public boolean use(Player player, Object useOn){
        if(useOn == null) {
            return false;
        }
        if(hasCooldown(player)) {
            sendCooldownMessage(player);
            return false;
        }
        if(checkItemHolding(player)) {
            if(useOn instanceof Block) {
                Block b = (Block) useOn;
                if(b.getType() == Material.AIR) {
                    return false;
                }
                FallingBlock block = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
                block.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
                b.setType(Material.AIR);
                Vector vector = player.getLocation().getDirection().multiply(1.5);
                vector.setY(0.8);
                block.setVelocity(vector);
                player.getWorld().playEffect(block.getLocation(), Effect.ENDER_SIGNAL, 4);
                player.getWorld().playSound(block.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                addCooldown(player);
                return true;
            }
            if(useOn instanceof Entity) {
                Entity e = (Entity) useOn;
                e.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
                Vector vector = player.getLocation().getDirection().multiply(1.5);
                vector.setY(0.8);
                if(e instanceof Monster) {
                    e.setMetadata("immune", new FixedMetadataValue(ServerPlugin.getInstance(), "damage"));
                    player.setMetadata("immune", new FixedMetadataValue(ServerPlugin.getInstance(), "damage"));
                    player.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
                }
                if(e instanceof Animals) {
                    vector.multiply(3);
                    e.setMetadata("immune", new FixedMetadataValue(ServerPlugin.getInstance(), "damage"));
                    player.setMetadata("immune", new FixedMetadataValue(ServerPlugin.getInstance(), "damage"));
                    player.setMetadata("ability", new FixedMetadataValue(ServerPlugin.getInstance(), getName()));
                }
                e.setVelocity(vector);
                player.getWorld().playEffect(e.getLocation(), Effect.ENDER_SIGNAL, 4);
                player.getWorld().playSound(e.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                addCooldown(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName(){
        return "Push";
    }

    @Override
    public int getCooldown(){
        return 10;
    }

    @Override
    public Material getWand(){
        return Material.STICK;
    }

    @Override
    public Material getTrigger(){
        return Material.ENDER_PEARL;
    }

    @Override
    public boolean onHit(Object entity){
        if(entity instanceof Entity) {
            Entity e = (Entity) entity;
            e.removeMetadata("ability", ServerPlugin.getInstance());
            if(e.hasMetadata("immune"))
                e.removeMetadata("immune", ServerPlugin.getInstance());
            return true;
            // TODO
        }
        return false;
    }
}
