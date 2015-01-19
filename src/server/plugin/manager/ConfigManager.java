package server.plugin.manager;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import server.plugin.filetype.YamlFile;

public class ConfigManager {

    private static ConfigManager             instance    = new ConfigManager();
    // Array to tell it where there are different config files
    private String[]                         Directories = new String[]{"Config Files",
                                                         // "Minigames/Arenas"
                                                         };

    private static HashMap<String, YamlFile> configs     = new HashMap<String, YamlFile>();

    private static YamlFile                  yaml;

    public static ConfigManager getInstance(){
        return instance;
    }

    public ConfigManager(){
    }

    public void init(){
        for(String directory : Directories) {
            for(File file : FileManager.getFolderFiles(directory)) {
                if(FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("yml")) {
                    String configName = FilenameUtils.removeExtension(file.getName());
                    yaml = new YamlFile(FileManager.getFile(directory + "/" + file.getName(), false));
                    configs.put(directory.replace("/", ".") + "." + configName, yaml);
                }
            }
        }
    }

    public YamlFile getConfig(String configName){
        return configs.get(configName);
    }

    public void test(){
        for(String accessor : configs.keySet()) {
            LogManager.getInstance().info(accessor);
        }
    }
}
