package rainbow.kuzwlu.core.datasource;

import lombok.Data;

/**
 * @Author kuzwlu
 * @Description 一个抽象类
 * @Date 2020/12/17 00:31
 * @Email kuzwlu@gmail.com
 */
@Data
public abstract class AbsDataSource {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private Integer initialSize = 5;
    private Integer minIdle = 5;
    private Integer maxActive = 20;
    private Long maxWait = 60000L;
    private Long timeBetweenEvictionRunsMillis = 60000L;
    private Long minEvictableIdleTimeMillis = 300000L;
    private String validationQuery = "SELECT 1 FROM DUAL";


}
