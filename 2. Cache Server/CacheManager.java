//CacheManager. This stores the HashMap and handles all HashMap related operations

import java.util.HashMap;
import java.util.Map;

public class CacheManager{

    private static java.util.HashMap<Object, Object> hashMap = new HashMap();

    public CacheManager(){

    }

    //add a new object to hashMap
    public static void addToCache(Cacheable object){

        hashMap.put(object.getIdentifier(), object);

    }

    //remove an object from hashMap
    public static void removeByID(Object key){
        hashMap.remove(key);
        System.out.println(key.toString() + " has been removed from cache");        
    }

    //retreive an object from hashMap
    public static Cacheable getFromCache(Object identifier){

        Cacheable object = (Cacheable)hashMap.get(identifier);

        if(object != null){
            //check if object is expired before returning
            if(object.isExpired()){
                //remove from map if expired
                removeByID(identifier);
                return null;
            }
            return object;
        }
        return null;
    }
}