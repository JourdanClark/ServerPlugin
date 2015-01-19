package server.plugin.minigames.arena;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import server.plugin.types.ServerRegion;

public class Arena {

    private ServerRegion                             region;

    private int                                      id;
    private String                                   name;

    // Name Spawn
    private HashMap<String, String>                  teams   = new HashMap<String, String>();

    // Name Location
    private HashMap<String, Location>                spawns  = new HashMap<String, Location>();

    // Name prop value
    private HashMap<String, HashMap<String, Object>> players = new HashMap<String, HashMap<String, Object>>();

    /**
     * Create an arena with specific teams.
     * 
     * @param ID - Unique ID for the arena
     * @param Name - Display name for the arena.
     * @param Teams - String array with team names.
     * @param Min - World edit location min.
     * @param Max - World edit location max.
     */
    public Arena(int ID, String Name, ServerRegion Region, String Teams[]){
        // Setup
        id = ID;
        name = Name;
        region = Region;

        initializeSpawns(Teams);
        initializeTeams(Teams);
    }

    /**
     * Add a player to a specific team
     * 
     * @param p
     * @param team
     * @return False if the team doesn't exist, or the player is already on a
     *         team
     */
    public boolean addPlayer(Player p, String team){
        if(!Arrays.asList(teams).contains(team) || containsPlayer(p))
            return false;

        HashMap<String, Object> attributes = players.get(p.getName());

        attributes.put("team", team);

        players.put(p.getName(), attributes);

        return true;
    }

    /**
     * @param p
     * @return Whether or not the player is on a team.
     */
    public boolean containsPlayer(Player p){
        return players.containsKey(p.getName());
    }

    /**
     * @return id - The arena's ID
     */
    public int getArenaID(){
        return id;
    }

    /**
     * @return name - The arena's Name.
     */
    public String getArenaName(){
        return name;
    }

    /**
     * @param team
     * @return number of players
     */
    public Integer getPlayersOnTeam(String team){
        // the requested team is not one of the available teams
        if(!teams.keySet().contains(team))
            return null;

        HashMap<String, Object> attributes;

        int counter = 0;
        for(String player : players.keySet()) {
            attributes = players.get(player);

            if((String) attributes.get("team") == team) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * @param p
     * @return Null if the player isn't on a team or the team name
     */
    public String getPlayerTeam(Player p){
        if(!players.containsKey(p.getName()))
            return null;

        HashMap<String, Object> attributes = players.get(p.getName());

        return (String) attributes.get("team");
    }

    /**
     * Get the spawn location for a given team
     * 
     * @param team
     * @return Location
     */
    public Location getSpawnLocation(String team){
        if(spawns.containsKey(team))
            return spawns.get(team);
        return null;
    }

    /**
     * @return The arena's teams.
     */
    public Set<String> getTeams(){
        return teams.keySet();
    }

    /**
     * Add a player to the team with the least players
     * 
     * @param p
     * @return The team the player was added to or null if the player is on a
     *         team already
     */
    // public String addPlayer( Player p ) {
    // if ( containsPlayer( p ) ) return null;
    //
    // // int[] teamcount = new int[ teams.size() ];
    //
    // HashMap<String, Integer> teamCount = new HashMap<String, Integer>();
    //
    // for ( String team : teams.keySet() ) {
    // teamCount.put( team, getPlayersOnTeam( team ) );
    // }
    //
    // int low = server.plugin.common.HelperFunctions.min( teamCount );
    //
    // int teamIndex = Arrays.asList( teamCount ).indexOf( low );
    //
    // players.put( p.getName(), teams[teamIndex] );
    //
    // return teams[ teamIndex ];
    // }

    /**
     * Initialize the spawns for every team to the center of the region.
     */
    private void initializeSpawns(String[] Teams){
        for(String team : Teams) {
            Location spawn = new Location(region.min.getWorld(), region.min.getX() + ((region.max.getX() - region.min.getX()) / 2), region.min.getY() + ((region.max.getY() - region.min.getY()) / 2), region.min.getZ() + ((region.max.getZ() - region.min.getZ()) / 2));
            spawns.put(team, spawn);
        }
    }

    private void initializeTeams(String[] Teams){
        for(String team : Teams) {
            teams.put(team, team);
        }
    }

    /**
     * @param p - Player
     * @return True if the player is in the region.
     */
    public boolean playerInRegion(Player p){
        Location l = p.getLocation();

        if(l.getX() >= region.min.getX() && l.getY() >= region.min.getY() && l.getZ() >= region.min.getZ() && l.getX() <= region.max.getX() && l.getY() <= region.max.getY() && l.getZ() <= region.max.getZ())
            return true;
        return false;
    }

    /**
     * Remove the player from the player list
     * 
     * @param p
     * @return False if the player isn't on a team
     */
    public boolean removePlayer(Player p){
        if(!containsPlayer(p))
            return false;

        players.remove(p.getName());

        return true;
    }

    /**
     * Change a teams spawn location
     * 
     * @param team
     * @param loc
     * @return false if the team doesn't exist, else true
     */
    public boolean setSpawnLocation(String team, Location loc){
        if(!teams.containsKey(team) || !spawns.containsKey(team))
            return false;
        spawns.put(team, loc);
        return true;
    }
}
