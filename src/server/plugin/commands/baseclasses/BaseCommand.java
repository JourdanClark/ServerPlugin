package server.plugin.commands.baseclasses;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import server.plugin.types.Enums.Permission;

public abstract class BaseCommand {

    public abstract void onCommand(Player player, String[] args);

    protected Permission   permission  = Permission.TRAVELER;
    protected String       name        = "Default";
    protected String       description = "";
    protected String       arguments   = "";
    protected String       parent      = "";
    protected List<String> aliases     = new ArrayList<String>();
    protected boolean      enableHelp  = true;

    /**
     * @return Returns the permission of the command.
     */
    public Permission getPermission(){
        return this.permission;
    }

    /**
     * @return Returns the name of the command.
     */
    public String getName(){
        return this.name;
    }

    /**
     * @return Returns the description shown to the player in a help message.
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * @return Returns the arguments the command takes.
     */
    public String getArguments(){
        return this.arguments;
    }

    /**
     * @return Returns the aliases for the command.
     */
    public List<String> getAliases(){
        return this.aliases;
    }

    /**
     * @return Returns true if the command has a help message enabled.
     */
    public boolean isHelpEnabled(){
        return this.enableHelp;
    }

}
