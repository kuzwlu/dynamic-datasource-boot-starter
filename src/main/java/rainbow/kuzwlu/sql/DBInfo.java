package rainbow.kuzwlu.sql;

import java.util.List;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/26 23:34
 * @Email kuzwlu@gmail.com
 */
public interface DBInfo {

    /**
     * 执行查询(一个字段--SHOW Table;)
     * @param sql
     * @param dataSourceName
     * @return
     */
    List<Object> executeOneColumnQuery(String sql, String dataSourceName);

    /**
     * 执行查询
     *
     * @param sql sql
     * @param dataSourceName dataSourceName
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> executeQuery(String sql, String dataSourceName);

    /**
     * 增删改
     *
     * @param sql sql
     * @param dataSourceName dataSourceName
     * @return int
     */
    int executeUpdate(String sql, String dataSourceName);

    /**
     * 从数据源中获取表名
     *
     * @param dataSourceName dataSourceName
     * @return List<Object>
     */
    List<Object> getTables(String dataSourceName);

    /**
     * 从表名获取所有字段名
     *
     * @param tableName tableName
     * @param dataSourceName dataSourceName
     * @return List<Object>
     */
    List<Object> getColumnName(String tableName, String dataSourceName);

    /**
     * 从表名获取所有字段详情
     *
     * @param tableName tableName
     * @param dataSourceName dataSourceName
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> getColumnInfo(String tableName, String dataSourceName);

    /**
     * 获取一张表中所有数据
     *
     * @param tableName tableName
     * @param dataSourceName dataSourceName
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> getValues(String tableName, String dataSourceName);

    /**
     * 获取多张表中所有数据
     *
     * @param tableNames tableNames
     * @param dataSourceName dataSourceName
     * @return List<List<Map<String, Object>>>
     */
    List<List<Map<String, Object>>> getValues(List<String> tableNames, String dataSourceName);

}
