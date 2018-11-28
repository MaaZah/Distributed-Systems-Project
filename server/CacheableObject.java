import java.util.Date;

public class CacheableObject implements Cacheable
{

    private Date expirationDate = null;
    private Object identifier = null;
    public Object object = null;

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

    public boolean isExpired(){
        if(expirationDate != null){
            if(expirationDate.before(new Date())){
                System.out.println(identifier + " is Expired");
                return true;
            }else{
                System.out.println(identifier + " is not Expired");
                return true;
            }            
        }
        return false;
    }

    public Object getIdentifier(){
        return identifier;
    }






}