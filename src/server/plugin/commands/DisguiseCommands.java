package server.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.PlayerHelper;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class DisguiseCommands extends NormalCommandExecutor {

    private String tag = "[Disguise] ";

    public DisguiseCommands(){
        commands.add(new disguise());
    }

    private class disguise extends BaseCommand {

        public disguise(){
            this.name = "disguise";
            this.description = "Disguise yourself as someone else!";
            this.arguments = "<name>";
            this.permission = Permission.JESTER;
        }

        @Override
        public void onCommand(Player player, String[] args){
            ServerPlayer sPlayer = PlayerManager.getInstance().getPlayer(player);

            if(args.length == 0) {
                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, tag + "Usage: /" + name + " " + arguments);
                return;
            }

            if(args[0].equalsIgnoreCase("-off")) {
                if(!sPlayer.getTemp("disguise").equals("")) {
                    MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You are not presently disguised");
                    MessageManager.getInstance().sendMessage(player, MessageType.INFO, tag + "To disguise yourself, use /disguse <name>");
                    return;
                }

                MessageManager.broadcastWithoutServerTag(ChatColor.YELLOW + CommonMessages.disguiseLeaveGame(sPlayer.getStringTemp("disguise")));
                MessageManager.broadcastWithoutServerTag(ChatColor.YELLOW + CommonMessages.disguiseJoinGame(sPlayer.getName()));

                // makes their disguise be null so that the event from tag api
                // doesn't send a fake tag
                sPlayer.setTemp("disguise", "");

                // changes their display name and list name to the disguise name
                // and disguise permission color from the config
                PlayerHelper.refreshPlayerName(player);

                MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "You are no longer disguised");
                return;
            }

            if(args[0].length() > 16) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "You can only use up to 16 characters for your disguise name");
                return;
            }

            if(PlayerHelper.playerHasPlayedBefore(args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "A player by the name of '" + args[0] + "' has played on the server before");
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "If they login and you're disguised as them, it will cause an error");
                return;
            }

            if(PlayerHelper.playerHasDisguise(args[0])) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, tag + "A player is already using that disguise");
                return;
            }

            // has their fake character join the game
            MessageManager.broadcastWithoutServerTag(ChatColor.YELLOW + CommonMessages.disguiseJoinGame(args[0]));

            if(!sPlayer.getTemp("disguise").equals(""))
                // has their normal character leave the game
                MessageManager.broadcastWithoutServerTag(ChatColor.YELLOW + CommonMessages.disguiseLeaveGame(sPlayer.getName()));
            else
                // has their normal character leave the game
                MessageManager.broadcastWithoutServerTag(ChatColor.YELLOW + CommonMessages.disguiseLeaveGame(sPlayer.getStringTemp("disguise")));

            sPlayer.setTemp("disguise", args[0]);

            // changes their display name and list name to the disguise name and
            // disguise permission color from the config
            PlayerHelper.refreshPlayerName(player);

            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, tag + "You are now disguised as '" + args[0] + "'");
        }

    }

}
