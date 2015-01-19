package server.plugin.types;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import server.plugin.ServerPlugin;

public class Task {

    private static Task instance = new Task();

    public static Task getInstance(){
        return instance;
    }

    public Task(){
    }

    /**
     * This will call in event after the given delay.
     * 
     * @param classIn The class the method is in.
     * @param methodName The name of the method [ex. callMe() would be
     *            "callMe"].
     * @param delayInServerTicks The delay in Server Ticks (there are 20 in 1
     *            second) until the command is called.
     */
    public void delayed(final Class<?> classIn, final String methodName, int delayInServerTicks){
        final Method method = getMethod(classIn, methodName);

        if(method == null) {
            return;
        }

        try {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ServerPlugin.getInstance(), new Runnable() {
                public void run(){
                    try {
                        method.invoke((Object) null, (Object[]) null);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayInServerTicks);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will call in event after the given delay.
     * 
     * @param classIn The class the method is in.
     * @param methodName The name of the method [ex. callMe() would be
     *            "callMe"].
     * @param parameters The parameters of the method [ex. callMe(String s)
     *            would be Object ob = {"maybe"}; and you would pass in ob].
     * @param delayInServerTicks The delay in Server Ticks (there are 20 in 1
     *            second) until the command is called.
     */
    public void delayed(final Class<?> classIn, final String methodName, final Object[] parameters, int delayInServerTicks){
        final Method method = getMethod(classIn, methodName);

        if(method == null) {
            return;
        }
        try {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ServerPlugin.getInstance(), new Runnable() {
                public void run(){
                    try {
                        method.invoke((Object) null, parameters);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayInServerTicks);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Method getMethod(Class<?> classIn, String methodName){
        Method method = null;

        for(Method m : classIn.getDeclaredMethods()) {
            if(m.getName().equals(methodName))
                method = m;
        }

        return method;
    }

    /**
     * This will call in event after the given delay and repeat it.
     * 
     * @param classIn The class the method is in.
     * @param methodName The name of the method [ex. callMe() would be
     *            "callMe"].
     * @param delayInServerTicks The delay in Server Ticks (there are 20 in 1
     *            second) until the command is called.
     * @param intervalInServerTicks The time in between each call of the method.
     * @return Returns the taskID of the task created. Returns -1 if the task
     *         fails to create.
     */
    public int repeatable(final Class<?> classIn, final String methodName, int delayInServerTicks, int intervalInServerTicks){
        final Method method = getMethod(classIn, methodName);

        if(method == null) {
            return -1;
        }

        try {
            int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ServerPlugin.getInstance(), new Runnable() {
                public void run(){
                    try {
                        method.invoke((Object) null, (Object[]) null);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayInServerTicks, intervalInServerTicks);
            return taskID;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This will call in event after the given delay.
     * 
     * @param classIn The class the method is in.
     * @param methodName The name of the method [ex. callMe() would be
     *            "callMe"].
     * @param parameters The parameters of the method [ex. callMe(String s)
     *            would be Object ob = {"maybe"}; and you would pass in ob].
     * @param delayInServerTicks The delay in Server Ticks (there are 20 in 1
     *            second) until the command is called.
     * @param intervalInServerTicks The time in between each call of the method.
     * @return Returns the taskID of the task created. Returns -1 if the task
     *         fails to create.
     */
    public int repeatable(final Class<?> classIn, final String methodName, final Object[] parameters, int delayInServerTicks, long intervalInServerTicks){
        final Method method = getMethod(classIn, methodName);

        if(method == null) {
            return -1;
        }

        try {
            int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ServerPlugin.getInstance(), new Runnable() {
                public void run(){
                    try {
                        method.invoke((Object) null, parameters);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayInServerTicks, intervalInServerTicks);
            return taskID;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This will call in event after the given delay and repeat it.
     * 
     * @param classIn The class the method is in.
     * @param methodName The name of the method [ex. callMe() would be
     *            "callMe"].
     * @param delayInServerTicks The delay in Server Ticks (there are 20 in 1
     *            second) until the command is called.
     * @param intervalInServerTicks The time in between each call of the method.
     * @return Returns the taskID of the task created. Returns -1 if the task
     *         fails to create.
     */
    public int repeatable(final Class<?> classIn, final String methodName, int delayInServerTicks, int intervalInServerTicks, int numberOfRepeats){
        final Method method = getMethod(classIn, methodName);

        if(method == null) {
            return -1;
        }

        try {
            int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ServerPlugin.getInstance(), new Runnable() {
                public void run(){
                    try {
                        method.invoke((Object) null, (Object[]) null);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayInServerTicks, intervalInServerTicks);
            Integer[] id = {taskID};
            delayed(this.getClass(), "removeRepeatableEvent", id, ((numberOfRepeats - 1) * intervalInServerTicks) + delayInServerTicks + 1); // add
                                                                                                                                             // 1
                                                                                                                                             // tick
                                                                                                                                             // to
                                                                                                                                             // make
                                                                                                                                             // sure
                                                                                                                                             // the
                                                                                                                                             // event
                                                                                                                                             // fires
            return taskID;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This will call in event after the given delay.
     * 
     * @param classIn The class the method is in.
     * @param methodName The name of the method [ex. callMe() would be
     *            "callMe"].
     * @param parameters The parameters of the method [ex. callMe(String s)
     *            would be Object ob = {"maybe"}; and you would pass in ob].
     * @param delayInServerTicks The delay in Server Ticks (there are 20 in 1
     *            second) until the command is called.
     * @param intervalInServerTicks The time in between each call of the method.
     * @return Returns the taskID of the task created. Returns -1 if the task
     *         fails to create.
     */
    public int repeatable(final Class<?> classIn, final String methodName, final Object[] parameters, int delayInServerTicks, int intervalInServerTicks, int numberOfRepeats){
        final Method method = getMethod(classIn, methodName);

        if(method == null) {
            return -1;
        }
        method.setAccessible(true);
        try {
            int taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ServerPlugin.getInstance(), new Runnable() {
                public void run(){
                    try {
                        method.invoke((Object) null, parameters);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delayInServerTicks, intervalInServerTicks);
            Integer[] id = {taskID};
            delayed(this.getClass(), "removeRepeatableEvent", id, ((numberOfRepeats - 1) * intervalInServerTicks + 1) + delayInServerTicks); // add
                                                                                                                                             // 1
                                                                                                                                             // tick
                                                                                                                                             // to
                                                                                                                                             // make
                                                                                                                                             // sure
                                                                                                                                             // the
                                                                                                                                             // event
                                                                                                                                             // fires
            return taskID;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void removeRepeatableEvent(int taskID){
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
