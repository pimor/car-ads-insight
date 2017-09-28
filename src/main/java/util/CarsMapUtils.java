package util;

import java.util.*;

/**
 * Created on 27/09/2017.
 */
public class CarsMapUtils {

    /**
     * Get all the keys for a specific value
     * @param map The source collection
     * @param value The value to search
     * @return Set with the keys that match with the value
     */
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet())
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        return keys;
    }


    /**
     * Format a Collection using commas as separator
     * @param c The input Collection
     * @return The output string after apply the print format
     */
    public static String formatCollection(Collection<?> c) {
        String s = null;
        Iterator itr = c.iterator();
        while(itr.hasNext()){
            Object element = itr.next();
            if(s==null) s = element.toString();
            else s = s + "," + element.toString();
        }
        return s;
    }
}
