package server.plugin.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import server.plugin.types.Enums.Setting;

public abstract class ToggleableSetting {

    private ItemStack enabled  = new ItemStack(Material.WOOL, 13);
    private ItemStack disabled = new ItemStack(Material.WOOL, 14);
    protected Setting setting;
    protected boolean active   = false;

    public ToggleableSetting(Setting setting){
        this.setting = setting;
    }

    public ItemStack getItem(){
        if(active)
            return enabled;
        return disabled;
    }

    public abstract void onClick();

}
