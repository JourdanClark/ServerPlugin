package server.plugin.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import server.plugin.commands.baseclasses.BaseCommand;
import server.plugin.commands.baseclasses.NormalCommandExecutor;
import server.plugin.manager.MenuManager;
import server.plugin.types.Enums.Permission;

public class PlayerSettingCommands extends NormalCommandExecutor {

    public PlayerSettingCommands(){
        commands.add(new setting());
    }

    private class setting extends BaseCommand {
        public setting(){
            this.name = "settings";
            this.description = "Alter you settings";
            this.permission = Permission.LEPPER;
        }

        @Override
        public void onCommand(Player player, String[] args){
            Inventory inv = MenuManager.getInstance().getMenu("settings").getInventory();
            player.openInventory(inv);
        }
    }
}
