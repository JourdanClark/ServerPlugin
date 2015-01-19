package server.plugin.common;

import server.plugin.types.Enums.Permission;
import server.plugin.types.Enums.StatisticType;

public class EnumHelper {

    /**
     * Gets a PlayerType from the given typeName.
     * 
     * @param typeName The name of the PlayerType to return.
     * @return Returns a PlayerType if it matches an existing one, null if
     *         otherwise.
     */
    public static Permission getPlayerType(String typeName){
        for(Permission type : Permission.values()) 
            if(type.name.equalsIgnoreCase(typeName))
                return type;
        return null;
    }

    /**
     * Gets a StatisticType from the given listenType.
     * 
     * @param typeName The name of the StatisticType to return.
     * @return Returns a StatisticType the listenType matches an existing one,
     *         null if otherwise.
     */
    public static StatisticType getStatisticTypeFromListenType(String listenType){
        return StatisticType.findStatisticTypeByListenType(listenType);
    }

    /**
     * Gets a StatisticType from the given typeName.
     * 
     * @param typeName The name of the StatisticType to return.
     * @return Returns a StatisticType if it matches an existing one, null if
     *         otherwise.
     */
    public static StatisticType getStatisticTypeFromName(String typeName){
        return StatisticType.findStatisticTypeByName(typeName);
    }

    /**
     * Gets a StatisticType from given path in the stats.yml
     * 
     * @param path The path to the statistic you are looking for.
     * @return Returns a StatisticType if the path matches an existing one, null
     *         if otherwise.
     */
    public static StatisticType getStatisticTypeFromID(int id){
        return StatisticType.findStatisticTypeByID(id);
    }
}
