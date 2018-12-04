//Simple key/object (request/address) pair that will be stored in the HashMap

import java.util.Date;

public class CacheableObject implements Cacheable
{
    private Date expirationDate = null;
    private Object identifier = null;
    private Object object = null;

    //public constructor, sets id, object, and expirationDate
    public CacheableObject(Object object, Object id, int minutes){
        this.object = object;
        this.identifier = id;

        if(minutes != 0){
            expirationDate = new Date();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(expirationDate);
            cal.add(cal.MINUTE, minutes);
            expirationDate = cal.getTime();
        }
    }

    //public method to check if object has expired
    public boolean isExpired(){
        if(expirationDate != null){
            if(expirationDate.before(new Date())){
                System.out.println(identifier + " is Expired");
                return true;
            }else{
                System.out.println(identifier + " is not Expired");
                return false;
            }            
        }
        return false;
    }

    //return identifier
    public Object getIdentifier(){
        return identifier;
    }

    //return object
    public Object getObject(){
        return object;
    }






}