package rainbow.kuzwlu.enums;

/**
 * @Author kuzwlu
 * @Description DataSource配置
 * @Date 2020/12/17 00:43
 * @Email kuzwlu@gmail.com
 */
public enum DataSourceEnum {

    DRIVER_CLASS_NAME("driverClassName"),
    URL("url"),
    USERNAME("username"),
    PASSWORD("password"),
    INITIAL_SIZE("initialSize"),
    MIN_IDLE("minIdle"),
    MAX_ACTIVE("maxActive"),
    MAX_WAIT("maxWait"),
    TIME_BETWEEN_EVICTION_RUNS_MILLIS("timeBetweenEvictionRunsMillis"),
    MIN_EVICTABLE_IDLE_TIME_MILLIS("minEvictableIdleTimeMillis"),
    VALIDATION_QUERY("validationQuery");

    private final String value;

    DataSourceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
