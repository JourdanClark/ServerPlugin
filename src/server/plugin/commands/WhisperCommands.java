package server.plugin.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.HelperFunctions;
import server.plugin.common.PlayerHelper;
import server.plugin.event.WhisperEvent;
import server.plugin.manager.MessageManager;
import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.ServerPlayer;

public class WhisperCommands extends NormalCommandExecutor {

    public WhisperCommands(){
        commands.add(new Reply());
        commands.add(new Whisper());
    }

    private String combineMessage(String[] args){
        String combined = "";

        for(int i = 0;i < args.length;i++) {
            if(i == args.length) {
                combined += args[i];
            } else {
                combined += args[i] + " ";
            }
        }

        return combined;
    }

    private class Whisper extends BaseCommand {

        public Whisper(){
            this.name = "whisper";
            this.description = "Send a private message to a player";
            this.arguments = "<player> <message>";
            this.permission = Permission.TRAVELER;
            this.enableHelp = false;

            this.aliases.add("msg");
            this.aliases.add("message");
            this.aliases.add("w");
        }

        @Override
        public void onCommand(Player player, String[] args){
            if(args.length < 2) {
                MessageManager.getInstance().sendMessage(player, MessageType.SUBINFO, "Usage: /msg <name> <message>");
                return;
            }

            // Player p = Bukkit.getPlayer( args[0] );
            ServerPlayer sPlayer = PlayerManager.getInstance().getPlayer(args[0]);

            if(sPlayer == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            Player playerFrom = player;
            Player playerTo = sPlayer.getBukkitPlayer();
            String message = combineMessage(Arrays.copyOfRange(args, 1, args.length));

            fireWhisperEvent(playerFrom, playerTo, message);
        }

    }

    private class Reply extends BaseCommand {

        public Reply(){
            this.name = "reply";
            this.description = "Send a message to the last person who sent you a whisper";
            this.arguments = "<message>";
            this.permission = Permission.TRAVELER;
            this.enableHelp = false;

            this.aliases.add("r");
        }

        public void onCommand(Player player, String[] args){
            ServerPlayer sPlayer = PlayerManager.getInstance().getPlayer(player);

            if(sPlayer.getStringTemp("whisper.reply").equals("")) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "There is no one to reply to");
            } else {
                Player replyPlayer = Bukkit.getPlayerExact(sPlayer.getStringTemp("whisper.reply"));
                if(args.length == 0) {
                    MessageManager.getInstance().sendMessage(player, MessageType.INFO, "You will reply to '" + PlayerHelper.checkDisguise(replyPlayer) + "'");
                } else {

                    Player playerFrom = player;
                    Player playerTo = replyPlayer;
                    String message = combineMessage(args);

                    fireWhisperEvent(playerFrom, playerTo, message);
                }
            }

        }

    }

    private void fireWhisperEvent(Player from, Player to, String message){
        WhisperEvent e = new WhisperEvent(from, to, HelperFunctions.formatColoredString(message));
        Bukkit.getServer().getPluginManager().callEvent(e);

    }

}
