package rainbow.kuzwlu.core.config;

import lombok.*;
import rainbow.kuzwlu.core.datasource.AbsDataSource;

import java.io.Serializable;

/**
 * @Author kuzwlu
 * @Description 副数据源
 * @Date 2020/12/16 23:43
 * @Email kuzwlu@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SubsidiaryDataSource extends AbsDataSource implements Serializable {

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
