package rainbow.kuzwlu.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import rainbow.kuzwlu.enums.DataSourceEnum;
import rainbow.kuzwlu.enums.DataSourceType;
import rainbow.kuzwlu.exception.PropertiesException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Author kuzwlu
 * @Description 获取配置文件的配置
 * @Date 2020/12/16 15:52
 * @Email kuzwlu@gmail.com
 */
@Configuration
public class EnvironmentConfig {

    private final static String PREFIX = "rainbow.kuzwlu.";
    private final static String DATASOURCE_PREFIX = PREFIX + "datasource.";
    private final static String DATASOURCE_MASTER_PREFIX = "master.";

    private final static String MAPPER_PACKAGE = PREFIX + "mapper-package";
    private final static String MAPPER_XML = PREFIX + "mapper-xml";
    private final static String SQL_SUBSIDIARY = PREFIX + "datasource.subsidiary";

    private final static String DATASOURCE_TYPE = DATASOURCE_PREFIX + "type";

    private final static String DATASOURCE_TRANSLATION_PREFIX = DATASOURCE_PREFIX + "transaction.";
    private final static String AOP_POINT_OUT = DATASOURCE_TRANSLATION_PREFIX+"aop-point-out";
    private final static String REQUIRE = DATASOURCE_TRANSLATION_PREFIX+"require";
    private final static String REQUIRE_READONLY = DATASOURCE_TRANSLATION_PREFIX+"require-readonly";

    private final static String MASTER_DATASOURCE_TABLE = DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + "datasource-table";
    private final static String MASTER_DATASOURCE_TABLE_TYPE = DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + "datasource-table-type";
    private final static String MASTER_DATASOURCE_TABLE_DBNAME = DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + "datasource-table-DBName";
    private final static String MASTER_DATASOURCE_TABLE_STATUS = DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + "datasource-table-status";
    private final static String MASTER_DATASOURCE_TABLE_STATUS_ENABLED = MASTER_DATASOURCE_TABLE_STATUS + "-enabled";

    @Bean("environmentProperties")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public EnvironmentProperties environmentBean(Environment environment) {
        EnvironmentProperties environmentProperties = new EnvironmentProperties();
        String mapperPackage = getPropertyWithException(environment, MAPPER_PACKAGE);
        String mapperXml = getPropertyWithException(environment, MAPPER_XML);
        String dataSourceType = getPropertyWithDefault(environment, DATASOURCE_TYPE, "NONE");
        DataSourceType dataSourceTypeE = DataSourceType.valueOf(dataSourceType.toUpperCase());
        if (DataSourceType.NONE.compareTo(dataSourceTypeE) != 0) {
            String masterDatasourceTable = getPropertyWithException(environment, MASTER_DATASOURCE_TABLE);
            String masterDatasourceTableType = getPropertyWithException(environment, MASTER_DATASOURCE_TABLE_TYPE);
            String masterDatasourceTableDBName = getPropertyWithException(environment, MASTER_DATASOURCE_TABLE_DBNAME);
            String masterDatasourceTableStatus = getPropertyWithException(environment, MASTER_DATASOURCE_TABLE_STATUS);
            String masterDatasourceTableStatusEnabled = getPropertyWithException(environment, MASTER_DATASOURCE_TABLE_STATUS_ENABLED);
            String sqlSubsidiary = getPropertyWithException(environment, SQL_SUBSIDIARY);
            String[] subsidiaryDataSourceNames = sqlSubsidiary.split(",");
            environmentProperties.setMasterDatasourceTable(masterDatasourceTable);
            environmentProperties.setMasterDatasourceTableType(masterDatasourceTableType);
            environmentProperties.setMasterDatasourceTableDBName(masterDatasourceTableDBName);
            environmentProperties.setMasterDatasourceTableStatus(masterDatasourceTableStatus);
            environmentProperties.setMasterDatasourceTableStatusEnabled(masterDatasourceTableStatusEnabled);
            environmentProperties.setSqlSubsidiary(Arrays.stream(subsidiaryDataSourceNames).collect(Collectors.toList()));
            environmentProperties.setSubsidiaryDataSourceMap(subsidiaryDataSourceMap(subsidiaryDataSourceNames, environment));
        }
        String aop_point_out = getPropertyWithDefault(environment, AOP_POINT_OUT,"execution(* " + mapperPackage + "..*.*(..))");
        String require = getPropertyWithDefault(environment, REQUIRE, "");
        String require_readonly = getPropertyWithDefault(environment, REQUIRE_READONLY, "");

        environmentProperties.setMapperPackage(mapperPackage);
        environmentProperties.setMapperXml(mapperXml);
        environmentProperties.setDataSourceType(dataSourceTypeE);
        environmentProperties.setAop_point_out(aop_point_out);
        environmentProperties.setRequire(Arrays.stream(require.split(",")).collect(Collectors.toList()));
        environmentProperties.setRequire_readonly(Arrays.stream(require_readonly.split(",")).collect(Collectors.toList()));
        environmentProperties.setMasterDatasource(masterDataSource(environment));
        return environmentProperties;
    }

    public Map<String, SubsidiaryDataSource> subsidiaryDataSourceMap(String[] subsidiaryDataSourceNames, Environment environment) {
        Map<String, SubsidiaryDataSource> subsidiaryDataSourceMap = new HashMap<>();
        for (String subsidiaryDataSourceName : subsidiaryDataSourceNames) {
            String driverClassName = getPropertyWithException(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.DRIVER_CLASS_NAME.getValue(), subsidiaryDataSourceName);
            String url = getPropertyWithException(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.URL.getValue(), subsidiaryDataSourceName);
            String username = getPropertyWithException(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.USERNAME.getValue(), subsidiaryDataSourceName);
            String password = getPropertyWithException(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.PASSWORD.getValue(), subsidiaryDataSourceName);
            String initialSizes = getPropertyWithDefault(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.INITIAL_SIZE.getValue(), "5");
            String minIdles = getPropertyWithDefault(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.MIN_IDLE.getValue(), "5");
            String maxActives = getPropertyWithDefault(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.MAX_ACTIVE.getValue(), "20");
            String maxWaits = getPropertyWithDefault(environment, DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.MAX_WAIT.getValue(), "60000");
            String timeBetweenEvictionRunsMillisS = environment.getProperty(DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.TIME_BETWEEN_EVICTION_RUNS_MILLIS.getValue(), "60000");
            String minEvictableIdleTimeMillisS = environment.getProperty(DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.MIN_EVICTABLE_IDLE_TIME_MILLIS.getValue(), "300000");
            String validationQuery = environment.getProperty(DATASOURCE_PREFIX + subsidiaryDataSourceName + "." + DataSourceEnum.VALIDATION_QUERY.getValue(), "SELECT 1 FROM DUAL");
            if (driverClassName == null || url == null || username == null || password == null ) {
                continue;
            }
            SubsidiaryDataSource subsidiaryDataSource = new SubsidiaryDataSource(driverClassName, url, username, password, Integer.valueOf(initialSizes), Integer.valueOf(minIdles), Integer.valueOf(maxActives), Long.valueOf(maxWaits), Long.valueOf(timeBetweenEvictionRunsMillisS), Long.valueOf(minEvictableIdleTimeMillisS), validationQuery);
            subsidiaryDataSourceMap.put(subsidiaryDataSourceName, subsidiaryDataSource);
        }
        return subsidiaryDataSourceMap;
    }

    public MasterDataSource masterDataSource(Environment environment) {
        String driverClassName = getPropertyWithException(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.DRIVER_CLASS_NAME.getValue(), "master");
        String url = getPropertyWithException(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.URL.getValue(), "master");
        String username = getPropertyWithException(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.USERNAME.getValue(), "master");
        String password = getPropertyWithException(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.PASSWORD.getValue(), "master");
        String initialSizes = getPropertyWithDefault(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.INITIAL_SIZE.getValue(), "5");
        String minIdles = getPropertyWithDefault(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.MIN_IDLE.getValue(), "5");
        String maxActives = getPropertyWithDefault(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.MAX_ACTIVE.getValue(), "20");
        String maxWaits = getPropertyWithDefault(environment, DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.MAX_WAIT.getValue(), "60000");
        String timeBetweenEvictionRunsMillisS = environment.getProperty(DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.TIME_BETWEEN_EVICTION_RUNS_MILLIS.getValue(), "60000");
        String minEvictableIdleTimeMillisS = environment.getProperty(DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.MIN_EVICTABLE_IDLE_TIME_MILLIS.getValue(), "300000");
        String validationQuery = environment.getProperty(DATASOURCE_PREFIX + DATASOURCE_MASTER_PREFIX + DataSourceEnum.VALIDATION_QUERY.getValue(), "SELECT 1 FROM DUAL");
        return new MasterDataSource(driverClassName, url, username, password, Integer.valueOf(initialSizes), Integer.valueOf(minIdles), Integer.valueOf(maxActives), Long.valueOf(maxWaits), Long.valueOf(timeBetweenEvictionRunsMillisS), Long.valueOf(minEvictableIdleTimeMillisS), validationQuery);
    }

    public String getPropertyWithException(Environment environment, String propertyName) {
        String property = environment.getProperty(propertyName);
        if (property == null) throw new PropertiesException("配置文件缺少: " + propertyName);
        return property;
    }

    public String getPropertyWithException(Environment environment, String propertyName, String dataSourceName) {
        String property = environment.getProperty(propertyName);
        String dataSourceType = getPropertyWithDefault(environment, DATASOURCE_TYPE, "NONE");
        DataSourceType dataSourceTypeE = DataSourceType.valueOf(dataSourceType.toUpperCase());
        if (DataSourceType.TOGETHER.compareTo(dataSourceTypeE) != 0) {
            if (property == null) throw new PropertiesException("数据源[" + dataSourceName + "]未配置: " + propertyName);
        }
        return property;
    }

    public String getPropertyWithDefault(Environment environment, String propertyName, String defaultValue) {
        return environment.getProperty(propertyName, defaultValue);
    }

}

