package rainbow.kuzwlu.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import rainbow.kuzwlu.core.datasource.AbsDataSource;

import javax.sql.DataSource;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/18 18:07
 * @Email kuzwlu@gmail.com
 */
public class DataSourceUtil {

    public static DataSource createDataSource(AbsDataSource absDataSource) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setUrl(absDataSource.getUrl());
        dataSource.setUsername(absDataSource.getUsername());
        dataSource.setPassword(absDataSource.getPassword());
        dataSource.setDriverClassName(absDataSource.getDriverClassName());
        dataSource.setInitialSize(absDataSource.getInitialSize());
        dataSource.setMinIdle(absDataSource.getMinIdle());
        dataSource.setMaxActive(absDataSource.getMaxActive());
        dataSource.setMaxWait(absDataSource.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(absDataSource.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(absDataSource.getMinEvictableIdleTimeMillis());
        return dataSource;
    }

}

