import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private int code;
    public ServiceException(String msg, int code){
        super(msg);
        this.code = code;
    }
}
