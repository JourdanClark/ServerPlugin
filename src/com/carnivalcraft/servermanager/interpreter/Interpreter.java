package com.carnivalcraft.servermanager.interpreter;

import java.util.Scanner;

import javax.swing.SwingWorker;

import org.bukkit.Bukkit;

import server.plugin.ServerPlugin;

public class Interpreter {

    private static Interpreter instance = new Interpreter();

    public static Interpreter getInstance(){
        return instance;
    }

    public void init(){
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception{
                Scanner s = new Scanner(System.in);
                while(s.hasNextLine()) {
                    String message = s.nextLine();
                    if(!message.endsWith("] ")) {
                        publish(message + "\n");
                        // System.out.println("Got Message: " + message);
                        if(message.equals("shutdown")) {
                            System.out.println("Received shutdown request");
                            ServerPlugin.getInstance().getServer().shutdown();
                        }
                        if(message.startsWith("timeset ")) {
                            try {
                                Bukkit.getServer().getWorld("youtube").setTime(Integer.parseInt(message.substring(8)));
                            } catch(Exception e) {
                                System.err.println("Could not set the time!");
                            }
                        }
                        if(message.startsWith("gettime")) {
                            System.out.println("answer: " + Bukkit.getServer().getWorld("youtube").getTime());
                        }
                        if(message.startsWith("!") && message.length() > 1) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), message.substring(1));
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

}
