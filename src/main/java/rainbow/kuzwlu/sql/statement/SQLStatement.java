package rainbow.kuzwlu.sql.statement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/27 00:00
 * @Email kuzwlu@gmail.com
 */
public interface SQLStatement extends ColumnInfo {

    /**
     * 查询所有表的SQL语句
     *
     * @param dataSource dataSource
     * @return String
     * @throws SQLException SQLException
     */
    String getTableStatement(DataSource dataSource) throws Exception;

    /**
     * 查询一张表中所有字段名的SQL语句
     *
     * @param dataSource dataSource
     * @param tableName tableName
     * @return String
     * @throws SQLException SQLException
     */
    String getColumnNameStatement(DataSource dataSource, String tableName) throws Exception;

    /**
     * 查询一张表中所有字段详情的SQL语句
     *
     * @param dataSource dataSource
     * @param tableName tableName
     * @return String
     * @throws SQLException SQLException
     */
    String getColumnInfoStatement(DataSource dataSource, String tableName) throws Exception;

    /**
     * 查询一张表中所有数据的SQL语句
     *
     * @param dataSource dataSource
     * @param tableName tableName
     * @return String
     * @throws SQLException SQLException
     */
    String getValuesStatement(DataSource dataSource, String tableName) throws Exception;

}
