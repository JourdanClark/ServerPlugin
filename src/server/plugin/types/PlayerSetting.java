package server.plugin.types;

import org.bukkit.entity.Player;

import server.plugin.manager.PlayerManager;
import server.plugin.types.Enums.Setting;

public class PlayerSetting extends ToggleableSetting {

    public PlayerSetting(Setting setting){
        super(setting);
    }

    @Override
    public void onClick(){

    }

    public void setStatusToDatabaseValue(Player player){
        // DataManager.getInstance().querySQL(get the setting from the
        // database);
        active = PlayerManager.getInstance().getPlayer(player).isCensoringMessages();
    }

}
