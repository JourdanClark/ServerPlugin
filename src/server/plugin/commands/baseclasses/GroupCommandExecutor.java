package server.plugin.commands.baseclasses;

import java.util.ArrayList;

public class GroupCommandExecutor extends ServerCommandExecutor {
    /**
     * All the executors used in this group command class.
     */
    public ArrayList<ServerCommandExecutor> executors = new ArrayList<ServerCommandExecutor>();

}
