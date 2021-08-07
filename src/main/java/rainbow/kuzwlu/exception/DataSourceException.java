package rainbow.kuzwlu.exception;

/**
 * @Author kuzwlu
 * @Description DataSourceException
 * @Date 2020/12/17 03:04
 * @Email kuzwlu@gmail.com
 */
public class DataSourceException extends RuntimeException {
    public DataSourceException(String msg) {
        super(msg);
    }

    public DataSourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DataSourceException(Throwable cause) {
        super(cause);
    }
}
