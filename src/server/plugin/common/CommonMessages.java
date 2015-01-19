package server.plugin.common;

import server.plugin.types.Enums.Permission;

public class CommonMessages {

    /**
     * Your class has been set to [type.color + type.name]
     */
    public static String classChangeInform(Permission type){ // used in
                                                             // PermissionEvent
        return "Your class has been set to " + type.toString();
    }

    /**
     * name has joined the game.
     */
    public static String disguiseJoinGame(String name){ // used in
                                                        // DisguiseExecutor
        return name + " has joined the game.";
    }

    /**
     * name has left the game.
     */
    public static String disguiseLeaveGame(String name){ // used in
                                                         // DisguiseExecutor
        return name + " has left the game.";
    }

    /**
     * Invalid number of arguments!
     */
    public static String invalidNumberOfArguments(){ // used in
                                                     // LocationExecutor,
                                                     // RedVsBlueCommandManager
        return "Invalid number of arguments!";
    }

    /**
     * You do not have permission for this command!
     */
    public static String invalidPermissions(){ // used in ServerCommandExecutor
        return "You do not have permission for this command!";
    }

    /**
     * The player 'playerName' does not exist!
     */
    public static String playerDoesntExist(String playerName){ // used in
                                                               // PermissionExecutor,
                                                               // TeleportExecutor
        return "The player '" + playerName + "' does not exist!";
    }

    /**
     * The player 'playerName' is not online!
     */
    public static String playerNotOnline(String playerName){
        return "The player '" + playerName + "' is not online!";
    }

    /**
     * Unknown Command!
     */
    public static String unknownCommand(){
        return "Unknown Command!";
    }
}
