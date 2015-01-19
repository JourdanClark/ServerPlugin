package server.plugin.manager;

import java.util.ArrayList;

import server.plugin.types.Menu;

public class MenuManager {

    private static MenuManager instance = new MenuManager();

    public static MenuManager getInstance(){
        return instance;
    }

    private ArrayList<Menu> menu = new ArrayList<Menu>();

    public void init(){
        instance = this;
    }

    // public Menu getMenu(String name){
    //
    // }
    //
}
