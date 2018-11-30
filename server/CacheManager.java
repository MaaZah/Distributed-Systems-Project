import java.util.HashMap;
import java.util.Map;

public class CacheManager{

    private static java.util.HashMap<Object, Object> hashMap = new HashMap();

    public CacheManager(){

    }


    public static void addToCache(Cacheable object){

        hashMap.put(object.getIdentifier(), object);

    }

    public static void removeFromCache(Object value){
        Object removalKey = null;

        for (HashMap.Entry<Object, Object> entry : hashMap.entrySet()) {
            if (value.equals(entry.getValue())) {
                removalKey = entry.getKey();
                break;
            }
        }

        if (removalKey != null) {
            hashMap.remove(removalKey);
        }
    }

    public static void removeByID(Object key){
        hashMap.remove(key);
        // Cacheable object = (Cacheable)hashMap.get(identifier);

        // if(object != null){
        //     hashMap.remove(identifier);
        // }
        
    }

    public static Cacheable getFromCache(Object identifier){

        Cacheable object = (Cacheable)hashMap.get(identifier);

        if(object != null){
            if(object.isExpired()){
                hashMap.remove(identifier);
                return null;
            }
            return object;
        }

        return null;
    }
}