package rainbow.kuzwlu.exception;

/**
 * @Author kuzwlu
 * @Description PropertiesException
 * @Date 2020/12/16 18:41
 * @Email kuzwlu@gmail.com
 */
public class PropertiesException extends RuntimeException {

    public PropertiesException(String msg) {
        super(msg);
    }

    public PropertiesException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PropertiesException(Throwable cause) {
        super(cause);
    }

}
