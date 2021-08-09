package rainbow.kuzwlu.sql.statement;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/27 04:11
 * @Email kuzwlu@gmail.com
 */
public interface ColumnInfo {

    /**
     * key：注释
     * value：字段名
     */
    Map<String, String> columnInfoMap = new LinkedHashMap<>();

    /**
     * key：注释
     * value：字段名
     */
    default void clearColumnInfoMap(){
        columnInfoMap.clear();
    }

    /**
     * key：注释
     * value：字段名
     */
    void setColumnInfoMap();

    /**
     * key：注释
     * value：字段名
     */
    Map<String, String> getColumnInfoMap();

}
