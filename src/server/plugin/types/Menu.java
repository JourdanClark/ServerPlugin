package server.plugin.types;

import java.util.ArrayList;

public class Menu {

    private String                       name;
    private ArrayList<ToggleableSetting> components;

    public Menu(String name){
        this.name = name;
        components = new ArrayList<ToggleableSetting>();
    }

    public String getName(){
        return name;
    }

}
