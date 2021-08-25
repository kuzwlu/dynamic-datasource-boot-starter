package rainbow.kuzwlu.sql.statement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description 添加Oracle的数据
 * @Date 2021/8/25 08:53
 * @Version 1.0
 */
public class OracleStatement implements SQLStatement{
    @Override
    public void setColumnInfoMap() {
        // TODO Auto-generated method stub
        columnInfoMap.put("表名", "table_name");
        columnInfoMap.put("列名", "column_name");
        columnInfoMap.put("描述", "comments");
        columnInfoMap.put("容器的id", "origin_con_id");
    }

    @Override
    public String getTableStatement(DataSource dataSource) throws Exception {
        return "select table_name from user_tables";
    }

    @Override
    public String getColumnNameStatement(DataSource dataSource, String tableName) throws Exception {
        return "select column_name from user_tab_columns where table_name = '"+tableName+"' ";
    }

    @Override
    public String getColumnInfoStatement(DataSource dataSource, String tableName) throws Exception {
        return "select * from user_tab_columns t where t.table_name = '"+tableName+"' ";
    }

    @Override
    public String getValuesStatement(DataSource dataSource, String tableName) throws Exception {
        return "select * from "+tableName+"";
    }
}
