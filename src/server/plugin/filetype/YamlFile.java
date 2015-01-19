package server.plugin.filetype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlFile extends YamlConfiguration {

    private File              file;
    private YamlConfiguration configuration;
    private int               saveQue, count;

    public YamlFile(File file){
        this.file = file;
        configuration = new YamlConfiguration();
        saveQue = 0;
        count = saveQue;
        reload();
    }

    public YamlFile(File file, int saveQue){
        this.file = file;
        this.saveQue = saveQue;
        count = 0;
        configuration = new YamlConfiguration();
        reload();
    }

    /**
     * Gets an object from the file at the specified path.
     */
    @Override
    public Object get(String path){
        return configuration.get(path);
    }

    /**
     * Gets a boolean from the file at the specified path.
     */
    @Override
    public boolean getBoolean(String path){
        return configuration.getBoolean(path);
    }

    public float getFloat(String path){
        try {
            return Float.parseFloat(configuration.getString(path));
        } catch(Exception e) {
            System.err.println("Error getting float from config:");
            System.err.println(" File: " + file.getPath().toString());
            System.err.println(" Path: " + path);
            e.printStackTrace();
        }
        return 0f;
    }

    /**
     * Gets a double from the file at the specified path.
     */
    @Override
    public double getDouble(String path){
        return configuration.getDouble(path);
    }

    /**
     * Gets an int from the file at the specified path.
     */
    @Override
    public int getInt(String path){
        return configuration.getInt(path);
    }

    /**
     * Gets a String from the file at the specified path.
     */
    @Override
    public String getString(String path){
        return configuration.getString(path);
    }

    /**
     * Reloads the file tied to the YamlFile.
     */
    public void reload(){
        try {
            configuration.load(file);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves any updated information to the file.
     */
    public void save(){
        try {
            configuration.save(file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds/Updates information to the file at the specified path
     */
    @Override
    public void set(String path, Object item){
        configuration.set(path, item);
        if(count == saveQue) {
            save();
            reload();
            count = 0;
        } else {
            count++;
        }
    }
}