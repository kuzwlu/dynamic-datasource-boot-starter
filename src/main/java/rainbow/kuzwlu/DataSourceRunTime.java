package rainbow.kuzwlu;

import org.springframework.stereotype.Component;
import rainbow.kuzwlu.core.config.EnvironmentProperties;
import rainbow.kuzwlu.core.datasource.DynamicDataSource;
import rainbow.kuzwlu.enums.DBTypeEnum;
import rainbow.kuzwlu.sql.DBInfo;
import rainbow.kuzwlu.sql.SQLTool;
import rainbow.kuzwlu.sql.statement.MysqlStatement;
import rainbow.kuzwlu.sql.statement.OracleStatement;
import rainbow.kuzwlu.sql.statement.PostgresqlPublicStatement;
import rainbow.kuzwlu.sql.statement.SQLStatement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/18 16:56
 * @Email kuzwlu@gmail.com
 */
@Component
public class DataSourceRunTime {

    @Resource
    private EnvironmentProperties environmentProperties;

    private Map<Object, SQLTool> sqlToolMap = new HashMap<>();

    /**
     * 应当唯一存在的runtime对象
     */
    private static final class SingletonHolder {
        private static final DataSourceRunTime runtime = new DataSourceRunTime();
    }

    private DataSourceRunTime() {
        sqlToolMap.put(DBTypeEnum.MYSQL.getValue(), new SQLTool(new MysqlStatement()));
        sqlToolMap.put(DBTypeEnum.POSTGRESQL.getValue(),new SQLTool(new PostgresqlPublicStatement()));
        sqlToolMap.put(DBTypeEnum.ORACLE.getValue(),new SQLTool(new OracleStatement()));
    }

    public static DataSourceRunTime getRuntime() {
        return SingletonHolder.runtime;
    }

    /**
     * 获取SQLTool
     *
     * @param dbTypeEnum
     * @return
     */
    public DBInfo getSQLTool(DBTypeEnum dbTypeEnum) {
        return sqlToolMap.get(dbTypeEnum.getValue());
    }

    /**
     * 获取SQLTool
     *
     * @param dbTypeName
     * @return
     */
    public DBInfo getSQLTool(String dbTypeName) {
        return sqlToolMap.get(dbTypeName);
    }

    /**
     * 获取
     *
     * @return
     */
    public EnvironmentProperties getEnvironmentProperties() {
        return environmentProperties;
    }

    /**
     * 注册一个SQLTool
     *
     * @param dbTypeEnum
     * @param sqlStatement
     * @return
     */
    public DBInfo registerSQLTool(DBTypeEnum dbTypeEnum, SQLStatement sqlStatement) {
        return registerSQLTool(dbTypeEnum,new SQLTool(sqlStatement));
    }

    /**
     * 注册一个SQLTool
     *
     * @param dbTypeEnum
     * @param sqlTool
     * @return
     */
    public DBInfo registerSQLTool(DBTypeEnum dbTypeEnum, SQLTool sqlTool) {
        if (!this.sqlToolMap.containsKey(dbTypeEnum.getValue())) {
            sqlToolMap.put(dbTypeEnum.getValue(), sqlTool);
        }
        return sqlToolMap.get(dbTypeEnum.getValue());
    }

    /**
     * 注册一个SQLTool
     *
     * @param dbTypeName
     * @param sqlStatement
     * @return
     */
    public DBInfo registerSQLTool(String dbTypeName, SQLStatement sqlStatement) {
        return registerSQLTool(dbTypeName,new SQLTool(sqlStatement));
    }

    /**
     * 注册一个SQLTool
     *
     * @param dbTypeName
     * @param sqlTool
     * @return
     */
    public DBInfo registerSQLTool(String dbTypeName, SQLTool sqlTool) {
        if (!this.sqlToolMap.containsKey(dbTypeName)) {
            sqlToolMap.put(dbTypeName, sqlTool);
        }
        return sqlToolMap.get(dbTypeName);
    }

    /**
     * 添加一条数据源进入数据源Map
     *
     * @param dataSourceName dataSourceName
     * @param dataSource dataSource
     */
    public boolean registerDataSource(String dataSourceName, DataSource dataSource) {
        try {
            DynamicDataSource.registerDataSource(dataSourceName, dataSource);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean removeDataSource(String dataSourceName){
        try {
            DynamicDataSource.removeDataSource(dataSourceName);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 获取当前主数据源
     *
     * @return
     */
    public DataSource getCurrentDataSource() {
        return DynamicDataSource.getCurrentDataSource();
    }

    /**
     * 获取单个数据源
     * @param dbTypeEnum
     * @return
     */
    public DataSource getDataSource(DBTypeEnum dbTypeEnum){
        return getDataSource(dbTypeEnum.getValue());
    }

    /**
     * 获取单个数据源
     * @param dataSourceName
     * @return
     */
    public DataSource getDataSource(String dataSourceName){
        return DynamicDataSource.switchKeyDataSourceMap(dataSourceName);
    }

    /**
     * 获取全部数据源
     *
     * @return
     */
    public Map<String, DataSource> getAllDataSources() {
        return DynamicDataSource.getDataSourceMap();
    }

}
