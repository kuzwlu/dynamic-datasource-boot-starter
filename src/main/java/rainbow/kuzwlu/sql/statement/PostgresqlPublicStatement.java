package rainbow.kuzwlu.sql.statement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/27 23:31
 * @Email kuzwlu@gmail.com
 */
public class PostgresqlPublicStatement implements SQLStatement {
    @Override
    public String getTableStatement(DataSource dataSource) {
        return "select tablename from pg_tables where schemaname='public'";
    }

    @Override
    public String getColumnNameStatement(DataSource dataSource, String tableName) {
        return "select col.column_name from information_schema.columns col left join pg_description des on col.table_name::regclass = des.objoid and col.ordinal_position = des.objsubid where table_schema = 'public' and table_name = '" + tableName + "' order by ordinal_position;";
    }

    @Override
    public String getColumnInfoStatement(DataSource dataSource, String tableName) {
        return "select * from information_schema.columns col left join pg_description des on col.table_name::regclass = des.objoid and col.ordinal_position = des.objsubid where table_schema = 'public' and table_name = '" + tableName + "' order by ordinal_position;";
    }

    @Override
    public String getValuesStatement(DataSource dataSource, String tableName) {
        return "SELECT * FROM \"" + tableName + "\"";
    }

    @Override
    public void clearColumnInfoMap() {
        columnInfoMap.clear();
    }

    @Override
    public void setColumnInfoMap() {
        columnInfoMap.put("数据库名", "table_catalog");
        columnInfoMap.put("表模式", "table_schema");
        columnInfoMap.put("表名称", "table_name");
        columnInfoMap.put("顺序位置", "ordinal_position");
        columnInfoMap.put("字段名称", "column_name");
        columnInfoMap.put("类型", "data_type");
        columnInfoMap.put("长度", "character_maximum_length");
        columnInfoMap.put("数值精度", "numeric_precision");
        columnInfoMap.put("数字刻度", "numeric_scale");
        columnInfoMap.put("是否为NULL", "is_nullable");
        columnInfoMap.put("默认值", "column_default");
        columnInfoMap.put("描述", "description");
    }

    @Override
    public Map<String, String> getColumnInfoMap() {
        return columnInfoMap;
    }
}
