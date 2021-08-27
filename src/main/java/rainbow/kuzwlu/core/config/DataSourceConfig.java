package rainbow.kuzwlu.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import rainbow.kuzwlu.core.datasource.DynamicDataSource;
import rainbow.kuzwlu.enums.DataSourceEnum;
import rainbow.kuzwlu.enums.DataSourceType;
import rainbow.kuzwlu.utils.DataSourceUtil;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * @Author kuzwlu
 * @Description DataSource配置
 * @Date 2020/12/17 13:26
 * @Email kuzwlu@gmail.com
 */
@Configuration
@Slf4j
public class DataSourceConfig implements TransactionManagementConfigurer{

    @Resource
    private EnvironmentProperties environmentProperties;

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        DataSource masterDataSource = initMasterDataSource();
        Map<String, DataSource> dataSourceMap = null;
        try {
            dataSourceMap = initSubsidiaryDataSource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataSourceMap.put("master", masterDataSource);
        DynamicDataSource.setMasterDataSource(masterDataSource);
        DynamicDataSource.setDataSourceMap(dataSourceMap);

        Map<Object, Object> targetDataSources = new HashMap<>();
        dataSourceMap.forEach((key, value) -> {
            targetDataSources.put(key, value);
            log.info("Injected dataSource: [{}]", key);
        });
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(dataSourceMap.get("master"));
        return dynamicDataSource;
    }

    /**
     * 初始化MasterDataSource
     *
     * @return
     */
    public DataSource initMasterDataSource() {
        return DataSourceUtil.createDataSource(environmentProperties.getMasterDatasource());
    }

    /**
     * 初始化SubsidiaryDataSource
     *
     * @return
     * @throws Exception
     */
    public Map<String, DataSource> initSubsidiaryDataSource() throws Exception {
        if (environmentProperties.getDataSourceType().compareTo(DataSourceType.PROPERTY) == 0) {
            log.info("Reading the subsidiary dataSource from the PROPERTY");
            return createSubsidiaryDataSourcesFromProperty();
        } else if (environmentProperties.getDataSourceType().compareTo(DataSourceType.DB) == 0) {
            log.info("Reading the subsidiary dataSource from the DB");
            return createSubsidiaryDataSourcesFromDB();
        } else if (environmentProperties.getDataSourceType().compareTo(DataSourceType.TOGETHER) == 0) {
            Map<String, DataSource> subsidiaryDataSourcesFromDB = createSubsidiaryDataSourcesFromDB();
            Map<String, DataSource> subsidiaryDataSourcesFromProperty = createSubsidiaryDataSourcesFromProperty();
            subsidiaryDataSourcesFromDB.putAll(subsidiaryDataSourcesFromProperty);
            log.info("Reading the subsidiary dataSource from the PROPERTY and DB");
            return subsidiaryDataSourcesFromDB;
        } else if (environmentProperties.getDataSourceType().compareTo(DataSourceType.NONE) == 0) {
            log.info("Subsidiary dataSource not set");
            return new HashMap<>();
        }
        return new HashMap<>();
    }

    /**
     * 从配置文件创建SubsidiaryDataSources
     *
     * @return
     */
    public Map<String, DataSource> createSubsidiaryDataSourcesFromProperty() {
        Map<String, DataSource> subsidiaryDataSourceMap = new HashMap<>();
        for (String subsidiaryDataSourceName : environmentProperties.getSqlSubsidiary()) {
            SubsidiaryDataSource subsidiaryDataSource = environmentProperties.getSubsidiaryDataSourceMap().get(subsidiaryDataSourceName);
            if (subsidiaryDataSource != null) {
                subsidiaryDataSourceMap.put(subsidiaryDataSourceName, DataSourceUtil.createDataSource(subsidiaryDataSource));
            }
        }
        return subsidiaryDataSourceMap;
    }

    /**
     * 从主数据库的数据源的表创建SubsidiaryDataSources
     *
     * @return
     * @throws Exception
     */
    public Map<String, DataSource> createSubsidiaryDataSourcesFromDB() throws Exception {
        Map<String, DataSource> subsidiaryDataSourceMap = new HashMap<>();
        DataSource dataSource = initMasterDataSource();
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = null;
        try {
            StringBuffer sb = new StringBuffer();
            Arrays.stream(DataSourceEnum.values()).forEach(dataSourceEnum -> sb.append(dataSourceEnum.getValue()).append(","));
            sb.append(environmentProperties.getMasterDatasourceTableType()).append(",");
            sb.append(environmentProperties.getMasterDatasourceTableDBName()).append(",");
            sb.append(environmentProperties.getMasterDatasourceTableStatus());
            PreparedStatement preparedStatement = connection.prepareStatement("select " + sb.toString() + " from " + environmentProperties.getMasterDatasourceTable() + " where " + environmentProperties.getMasterDatasourceTableStatus() + " = " + environmentProperties.getMasterDatasourceTableStatusEnabled());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String dbName = resultSet.getString(environmentProperties.getMasterDatasourceTableDBName());
                List<String> sqlSubsidiary = environmentProperties.getSqlSubsidiary();
                if (sqlSubsidiary.contains(dbName)) {
                    SubsidiaryDataSource subsidiaryDataSource = new SubsidiaryDataSource();
                    subsidiaryDataSource.setDriverClassName(resultSet.getString(DataSourceEnum.DRIVER_CLASS_NAME.getValue()));
                    subsidiaryDataSource.setUrl(resultSet.getString(DataSourceEnum.URL.getValue()));
                    subsidiaryDataSource.setUsername(resultSet.getString(DataSourceEnum.USERNAME.getValue()));
                    subsidiaryDataSource.setPassword(resultSet.getString(DataSourceEnum.PASSWORD.getValue()));
                    subsidiaryDataSource.setInitialSize(resultSet.getInt(DataSourceEnum.INITIAL_SIZE.getValue()));
                    subsidiaryDataSource.setMinIdle(resultSet.getInt(DataSourceEnum.MIN_IDLE.getValue()));
                    subsidiaryDataSource.setMaxActive(resultSet.getInt(DataSourceEnum.MAX_ACTIVE.getValue()));
                    subsidiaryDataSource.setMaxWait(resultSet.getLong(DataSourceEnum.MAX_WAIT.getValue()));
                    subsidiaryDataSource.setTimeBetweenEvictionRunsMillis(resultSet.getLong(DataSourceEnum.TIME_BETWEEN_EVICTION_RUNS_MILLIS.getValue()));
                    subsidiaryDataSource.setMinEvictableIdleTimeMillis(resultSet.getLong(DataSourceEnum.MIN_EVICTABLE_IDLE_TIME_MILLIS.getValue()));
                    subsidiaryDataSource.setValidationQuery(resultSet.getString(DataSourceEnum.VALIDATION_QUERY.getValue()));
                    subsidiaryDataSourceMap.put(dbName, DataSourceUtil.createDataSource(subsidiaryDataSource));
                }
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
        return subsidiaryDataSourceMap;
    }

    @Bean(name = "txManager")
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Override
    public TransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }
}

