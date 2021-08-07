package rainbow.kuzwlu.exception;

/**
 * @Author kuzwlu
 * @Description MybatisPlusException
 * @Date 2020/12/19 03:04
 * @Email kuzwlu@gmail.com
 */
public class MybatisPlusException extends RuntimeException {

    public MybatisPlusException(String msg) {
        super(msg);
    }

    public MybatisPlusException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MybatisPlusException(Throwable cause) {
        super(cause);
    }
}
