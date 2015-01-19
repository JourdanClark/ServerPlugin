package server.plugin.manager;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import server.plugin.types.Cooldown;

public class CooldownManager {

    private static CooldownManager instance = new CooldownManager();

    public static CooldownManager getInstance(){
        return instance;
    }

    private CooldownManager(){
    }

    public void init(){
        cooldowns = new HashMap<String, HashMap<String, Cooldown>>();
    }

    // player cooldown name
    private static HashMap<String, HashMap<String, Cooldown>> cooldowns;

    public void addCooldown(Player player, String cooldownName, long durationInServerTicks){
        if(player != null && cooldownName != null) {
            HashMap<String, Cooldown> map = new HashMap<String, Cooldown>();
            Cooldown cooldown = new Cooldown(cooldownName, player, durationInServerTicks);
            map.put(cooldownName.toLowerCase(), cooldown);
            cooldowns.put(player.getName().toLowerCase(), map);
        }
    }

    public void addCooldown(String player, String cooldownName, long durationInServerTicks){
        if(Bukkit.getPlayerExact(player) != null && cooldownName != null) {
            HashMap<String, Cooldown> map = new HashMap<String, Cooldown>();
            Cooldown cooldown = new Cooldown(cooldownName, Bukkit.getPlayerExact(player), durationInServerTicks);
            map.put(cooldownName.toLowerCase(), cooldown);
            cooldowns.put(Bukkit.getPlayerExact(player).getName().toLowerCase(), map);
        }
    }

    public Cooldown getCooldown(String player, String cooldownName){ // "String player"
                                                                     // in
                                                                     // case
                                                                     // the
                                                                     // player
                                                                     // logged
                                                                     // off
                                                                     // and
                                                                     // you
                                                                     // can
                                                                     // no
                                                                     // longer
                                                                     // cast
                                                                     // them
                                                                     // to
                                                                     // player
        HashMap<String, Cooldown> map = cooldowns.get(player.toLowerCase());
        Cooldown cooldown = null;
        if(map != null)
            cooldown = map.get(cooldownName.toLowerCase());
        return cooldown;
    }

    public static void removeCooldownForPlayer(String player, String cooldownName){ // "String player"
                                                                                    // in
                                                                                    // case
                                                                                    // the
                                                                                    // player
                                                                                    // logged
                                                                                    // off
                                                                                    // and
                                                                                    // you
                                                                                    // can
                                                                                    // no
                                                                                    // longer
                                                                                    // cast
                                                                                    // them
                                                                                    // to
                                                                                    // player
        HashMap<String, Cooldown> map = cooldowns.get(player.toLowerCase());
        Cooldown cooldown = null;
        if(map != null)
            cooldown = map.get(cooldownName.toLowerCase());
        if(cooldown != null) {
            map.remove(cooldownName.toLowerCase());
            cooldowns.put(player.toLowerCase(), map);
        }
    }

    public static void removeCooldown(Cooldown cooldown){
        HashMap<String, Cooldown> map = cooldowns.get(cooldown.playerName.toLowerCase());
        Cooldown cd = null;
        if(map != null)
            cd = map.get(cooldown.cooldownName.toLowerCase());
        if(cd != null) {
            map.remove(cooldown.cooldownName.toLowerCase());
            cooldowns.put(cooldown.playerName.toLowerCase(), map);
        }

    }
}
