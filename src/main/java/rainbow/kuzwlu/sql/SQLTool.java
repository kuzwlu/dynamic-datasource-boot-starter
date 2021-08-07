package rainbow.kuzwlu.sql;

import com.alibaba.druid.pool.DruidAbstractDataSource;
import lombok.extern.slf4j.Slf4j;
import rainbow.kuzwlu.core.datasource.DynamicDataSource;
import rainbow.kuzwlu.exception.SqlException;
import rainbow.kuzwlu.sql.statement.SQLStatement;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/26 23:42
 * @Email kuzwlu@gmail.com
 */
@Slf4j
public class SQLTool implements DBInfo {

    private final SQLStatement statements;

    private final Map<String, String> columnInfoMap = new LinkedHashMap<>();

    public SQLTool(SQLStatement sqlStatement) {
        this.statements = sqlStatement;
        sqlStatement.clearColumnInfoMap();
        statements.setColumnInfoMap();
        columnInfoMap.putAll(statements.getColumnInfoMap());
    }

    @Override
    public List<Object> executeOneColumnQuery(String sql, String dataSourceName) {
        return executeOneColumnQuery(sql, getDataSource(dataSourceName));
    }

    private List<Object> executeOneColumnQuery(String sql, DataSource dataSource) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Object> result = new LinkedList<>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
            return result;
        } catch (SQLException e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, statement);
        }
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql, String dataSourceName) {
        DataSource dataSource = getDataSource(dataSourceName);
        return executeQuery(sql, dataSource,dataSourceName);
    }

    private List<Map<String, Object>> executeQuery(String sql, DataSource dataSource,String dataSourceName ) {
        List<Map<String, Object>> valuesList = new LinkedList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            ResultSetMetaData metaData = statement.getMetaData();
            ResultSet resultSet = statement.executeQuery();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> result = new LinkedHashMap<>(resultSet.getMetaData().getColumnCount());
                for (int i = 1; i <= columnCount; i++) {
                    result.put(metaData.getColumnName(i), resultSet.getObject(metaData.getColumnName(i)));
                }
                valuesList.add(result);
            }
        } catch (SQLException e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, statement);
        }
        return valuesList;
    }

    @Override
    public int executeUpdate(String sql, String dataSourceName) {
        DataSource dataSource = getDataSource(dataSourceName);
        return executeUpdate(sql, dataSource,dataSourceName);
    }

    private int executeUpdate(String sql, DataSource dataSource,String dataSourceName) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            int i = statement.executeUpdate();
            return i;
        } catch (SQLException e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, statement);
        }
    }

    @Override
    public List<Object> getTables(String dataSourceName) {
        return getTables(getDataSource(dataSourceName));
    }

    private List<Object> getTables(DataSource dataSource) {
        try {
            return executeOneColumnQuery(statements.getTableStatement(dataSource), dataSource);
        } catch (Exception e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        }
    }

    @Override
    public List<Object> getColumnName(String tableName, String dataSourceName) {
        return getColumnName(tableName, getDataSource(dataSourceName));
    }

    private List<Object> getColumnName(String tableName, DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return executeOneColumnQuery(statements.getColumnNameStatement(dataSource, tableName), dataSource);
        } catch (Exception e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, null);
        }
    }

    @Override
    public List<Map<String, Object>> getColumnInfo(String tableName, String dataSourceName) {
        DataSource dataSource = getDataSource(dataSourceName);
        return getColumnInfo(tableName, dataSource);
    }

    private List<Map<String, Object>> getColumnInfo(String tableName, DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        List<Map<String, Object>> result = new LinkedList<>();
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(statements.getColumnInfoStatement(dataSource, tableName));
            if (this.columnInfoMap.isEmpty()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> value = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        value.put(metaData.getColumnName(i), resultSet.getObject(metaData.getColumnName(i)));
                    }
                    result.add(value);
                }
            } else {
                while (resultSet.next()) {
                    Map<String, Object> value = new LinkedHashMap<>();
                    for (String key : this.columnInfoMap.keySet()) {
                        value.put(key, resultSet.getObject(this.columnInfoMap.get(key)));
                    }
                    result.add(value);
                }
            }
        } catch (Exception e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, statement);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getValues(String tableName, String dataSourceName) {
        return getValues(tableName, getDataSource(dataSourceName));
    }

    private List<Map<String, Object>> getValues(String tableName, DataSource dataSource) {
        List<Object> tableColumnList = getColumnName(tableName, dataSource);
        List<Map<String, Object>> valuesList = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(statements.getValuesStatement(dataSource, tableName));
            while (resultSet.next()) {
                Map<String, Object> result = new LinkedHashMap<>(resultSet.getMetaData().getColumnCount());
                for (Object tableColumn : tableColumnList) {
                    result.put((String) tableColumn, resultSet.getString((String) tableColumn));
                }
                valuesList.add(result);
            }
        } catch (Exception e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, statement);
        }
        return valuesList;
    }

    @Override
    public List<List<Map<String, Object>>> getValues(List<String> tableNames, String dataSourceName) {
        return getValues(tableNames, getDataSource(dataSourceName));
    }

    private List<List<Map<String, Object>>> getValues(List<String> tableNames, DataSource dataSource) {
        List<List<Map<String, Object>>> resultList = new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            for (String tableName : tableNames) {
                List<Object> tableColumnList = getColumnName(tableName, dataSource);
                List<Map<String, Object>> valuesList = new LinkedList<>();
                ResultSet resultSet = statement.executeQuery(statements.getValuesStatement(dataSource, tableName));
                while (resultSet.next()) {
                    Map<String, Object> result = new LinkedHashMap<>(resultSet.getMetaData().getColumnCount());
                    for (Object tableColumn : tableColumnList) {
                        result.put((String) tableColumn, resultSet.getString((String) tableColumn));
                    }
                    valuesList.add(result);
                }
                resultList.add(valuesList);
            }
        } catch (Exception e) {
            throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
        } finally {
            close(connection, statement);
        }
        return resultList;
    }


    private DataSource getDataSource(String dataSourceName) {
        DataSource dataSource = DynamicDataSource.switchKeyDataSourceMap(dataSourceName);
        if (dataSource == null) {
            dataSource = DynamicDataSource.getCurrentDataSource();
            log.debug("["+dataSourceName+"] 数据源获取失败，重置为 [master]数据源");
        }
        return dataSource;
    }

    private void close(Connection connection, Statement statement) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new SqlException("执行SQL语句时发生错误：" + e.getLocalizedMessage());
            }
        }
    }

}
