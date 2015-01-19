package server.plugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.common.CommonMessages;
import server.plugin.common.EnumHelper;
import server.plugin.common.HelperFunctions;
import server.plugin.common.PlayerHelper;
import server.plugin.manager.ConfigManager;
import server.plugin.manager.MessageManager;
import server.plugin.types.Enums.MessageType;
import server.plugin.types.Enums.Permission;
import server.plugin.types.Enums.PunishmentType;
import server.plugin.types.Punishment;

public class EnforcementCommands extends NormalCommandExecutor {

    private Permission PERMANENT_DURATION_PERMISSION = EnumHelper.getPlayerType(ConfigManager.getInstance().getConfig("Config Files.permission").getString("permanent_ban_or_jail"));

    public EnforcementCommands(){
        // commands.add( new jail() );
        // commands.add( new unjail() );
        commands.add(new kick());
        // commands.add( new ban() );
        // commands.add( new unban() );
    }

    private class jail extends BaseCommand {
        private Permission playerType;

        public jail(){
            this.name = "jail";
            this.description = "Allows you to jail a player";
            this.arguments = "<player> <time> <reason>";
            this.permission = Permission.KING;
        }

        @Override
        public void onCommand(Player player, String[] args){
            this.playerType = PlayerHelper.getPlayerPermission(player);
            if(args.length >= 2) {
                if(!isNumber(args[1])) { // if they try doing it without a time
                    if(!hasPermissionForPermanent(player)) { // if they don't
                                                             // have
                                                             // permission to
                                                             // do it without
                                                             // a time
                        MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, "You must specify a time!");
                        return;
                    }
                }
            }
        }
    }

    private class kick extends BaseCommand {

        public kick(){
            this.name = "kick";
            this.description = "Allows you to kick a player";
            this.arguments = "<player> <reason>";
            this.permission = Permission.KNIGHT;
        }

        @Override
        public void onCommand(Player player, String[] args){
            Player kickedPlayer = Bukkit.getServer().getPlayerExact(args[0]);
            if(kickedPlayer == null) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, CommonMessages.playerDoesntExist(args[0]));
                return;
            }

            Permission perm = PlayerHelper.getPlayerPermission(kickedPlayer);
            if(perm.isLessThanOrEqual(Permission.KNIGHT) && PlayerHelper.getPlayerPermission(player) != Permission.KING) {
                MessageManager.getInstance().sendMessage(player, MessageType.SEVERE, perm.name + "s cannot be kicked from the server");
                return;
            }

            String reason = HelperFunctions.combineString(args, 1);
            if(!reason.equals(null))
                reason = HelperFunctions.formatColoredString(reason);

            Punishment kick = new Punishment(kickedPlayer, player, PunishmentType.KICK, reason, System.currentTimeMillis());
            kick.invokePunishment(0);
            MessageManager.getInstance().sendMessage(player, MessageType.GOOD, "Kicked " + kickedPlayer.getName() + " for: " + ChatColor.RESET + reason);
        }
    }

    private boolean isNumber(String arg){
        try {
            Double.parseDouble(arg);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private double getNumber(String arg){
        try {
            return Double.parseDouble(arg);
        } catch(Exception e) {
            return -1;
        }
    }

    private boolean hasPermissionForPermanent(Player player){
        if(!PERMANENT_DURATION_PERMISSION.isGreaterThanOrEqual(PlayerHelper.getPlayerPermission(player)))
            return false;
        return true;
    }
}
