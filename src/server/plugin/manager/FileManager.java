package server.plugin.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import server.plugin.types.Enums.DebugType;

public class FileManager {
    public static boolean debugInfo = false;

    public static String  dataFolder;

    /**
     * @param path - Create folders down the path provided
     * @return The result of making the directories
     */
    public static boolean createFolders(String path){
        File f = new File(dataFolder + "/" + path);

        Boolean b = f.mkdirs();

        if(debugInfo) {
            if(b) {
                LogManager.getInstance().info("The directories '" + path + "' were created.");
            } else {
                LogManager.getInstance().info("The directories '" + path + "' were not created.");
            }
        }

        return b;
    }

    public static boolean deleteFile(String path){
        File f = new File(dataFolder + "/" + path);

        if(f.isFile()) {
            return f.delete();
        }

        return false;
    }

    /**
     * @param path - Path of the file to check.
     * @return Whether the file exists or not.
     */
    public static boolean fileExists(String path){
        File f = new File(dataFolder + "/" + path);

        Boolean b = f.exists();

        if(debugInfo) {
            if(b) {
                LogManager.getInstance().info("The file '" + path + "' exists.");
            } else {
                LogManager.getInstance().info("The file '" + path + "' does not exist.");
            }
        }

        return b;
    }

    /**
     * @param path - Path to the file as a string
     * @return The file unless there was an error
     */
    public static File getFile(String path, Boolean create){
        File f = new File(dataFolder + "/" + path);

        try {
            if(create) {
                Boolean b = f.createNewFile();
                if(debugInfo) {
                    if(b) {
                        LogManager.getInstance().debug("The file '" + path + "' was created.", DebugType.FILE);
                    } else {
                        LogManager.getInstance().debug("The file '" + path + "' was not created.", DebugType.FILE);
                    }
                }
            }
            return f;
        } catch(IOException e) {
            LogManager.getInstance().warning("There was a problem creating the file at '" + path + "'");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param path
     * @return A list of files in the directory
     */
    public static File[] getFolderFiles(String path){
        File directory = new File(dataFolder + "/" + path);

        if(directory.isDirectory()) {

            ArrayList<File> files = new ArrayList<File>();

            for(File f : directory.listFiles()) {
                if(!f.isDirectory())
                    files.add(f);
            }

            return files.toArray(new File[files.size()]);

        }

        return null;
    }

    /**
     * @param path
     * @return A list of the folders in the directory
     */
    public static File[] getFolderFolders(String path){
        File directory = new File(dataFolder + "/" + path);

        if(directory.isDirectory()) {

            ArrayList<File> folders = new ArrayList<File>();

            for(File f : directory.listFiles()) {
                if(f.isDirectory())
                    folders.add(f);
            }

            return folders.toArray(new File[folders.size()]);

        }

        return null;
    }

}
