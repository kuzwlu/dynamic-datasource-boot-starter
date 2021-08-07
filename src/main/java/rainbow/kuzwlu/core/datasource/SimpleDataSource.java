package rainbow.kuzwlu.core.datasource;

import lombok.*;

/**
 * @Author kuzwlu
 * @Description 对外提供的一个数据源配置类
 * @Date 2020/12/26 18:57
 * @Email kuzwlu@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleDataSource extends AbsDataSource {

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
