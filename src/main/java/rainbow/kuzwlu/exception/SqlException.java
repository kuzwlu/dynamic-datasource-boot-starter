package rainbow.kuzwlu.exception;

/**
 * @Author kuzwlu
 * @Description SqlException
 * @Date 2020/12/26 19:31
 * @Email kuzwlu@gmail.com
 */
public class SqlException extends RuntimeException {

    public SqlException(String msg) {
        super(msg);
    }

    public SqlException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }

}
