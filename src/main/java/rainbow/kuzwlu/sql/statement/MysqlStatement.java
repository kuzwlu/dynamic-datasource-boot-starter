package rainbow.kuzwlu.sql.statement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/26 23:59
 * @Email kuzwlu@gmail.com
 */
public class MysqlStatement implements SQLStatement {

    @Override
    public String getColumnNameStatement(DataSource dataSource, String tableName) throws Exception {
        Connection connection = dataSource.getConnection();
        String catalog = connection.getCatalog();
        connection.close();
        return "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '" + tableName + "' AND table_schema = '" + catalog + "';";
    }

    @Override
    public String getTableStatement(DataSource dataSource) {
        return "SHOW TABLES;";
    }

    @Override
    public String getColumnInfoStatement(DataSource dataSource, String tableName) throws Exception {
        Connection connection = dataSource.getConnection();
        String catalog = connection.getCatalog();
        connection.close();
        return "SELECT * FROM information_schema.COLUMNS WHERE table_name = '" + tableName + "' AND table_schema = '" + catalog + "';";
    }

    @Override
    public String getValuesStatement(DataSource dataSource, String tableName) {
        return "SELECT * FROM " + tableName + ";";
    }

    @Override
    public void clearColumnInfoMap() {
        columnInfoMap.clear();
    }

    @Override
    public void setColumnInfoMap() {
        columnInfoMap.put("数据库", "TABLE_SCHEMA");
        columnInfoMap.put("表名", "TABLE_NAME");
        columnInfoMap.put("列名", "COLUMN_NAME");
        columnInfoMap.put("默认值", "COLUMN_DEFAULT");
        columnInfoMap.put("是否为NULL", "IS_NULLABLE");
        columnInfoMap.put("类型", "DATA_TYPE");
        columnInfoMap.put("最大长度", "CHARACTER_MAXIMUM_LENGTH");
        columnInfoMap.put("字符集名称", "CHARACTER_SET_NAME");
        columnInfoMap.put("列类型", "COLUMN_TYPE");
        columnInfoMap.put("列关键字", "COLUMN_KEY");
        columnInfoMap.put("额外说明", "EXTRA");
    }

    @Override
    public Map<String, String> getColumnInfoMap() {
        return columnInfoMap;
    }
}
