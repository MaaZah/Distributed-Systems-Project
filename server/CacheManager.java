import java.util.HashMap;

public class CacheManager{

    private static java.util.HashMap hashMap = new HashMap();

    public CacheManager(){

    }


    public static void addToCache(Cacheable object){

        hashMap.put(object.getIdentifier(), object);

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