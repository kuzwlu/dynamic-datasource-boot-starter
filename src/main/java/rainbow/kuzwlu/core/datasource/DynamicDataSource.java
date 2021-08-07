package rainbow.kuzwlu.core.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description 动态数据源实现
 * @Date 2020/12/15 00:53
 * @Email kuzwlu@gmail.com
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<DataSource> masterDataSource = new ThreadLocal<>();

    private static Map<String, DataSource> dataSourceMap = new HashMap<>();

    /**
     * 设置主数据源
     *
     * @param dataSource dataSource
     */
    public static void setMasterDataSource(DataSource dataSource) {
        DynamicDataSource.masterDataSource.set(dataSource);
    }

    /**
     * 设置主数据源
     *
     * @param dataSourceName dataSourceName
     */
    public static void setMasterDataSource(String dataSourceName) {
        DataSource dataSource = switchKeyDataSourceMap(dataSourceName);
        if (dataSource != null) {
            DynamicDataSource.setMasterDataSource(dataSource);
        } else {
            DynamicDataSource.reset2MasterDataSource();
            log.debug("[" + dataSourceName + "]数据源 设置失败，重置为 [master]数据源");
        }
    }

    /**
     * 获取当前使用的哪个数据源
     *
     * @return DataSource
     */
    public static DataSource getCurrentDataSource() {
        return DynamicDataSource.masterDataSource.get();
    }

    /**
     * 设置数据源Map
     *
     * @param dataSources dataSources
     */
    public static void setDataSourceMap(Map<String, DataSource> dataSources) {
        DynamicDataSource.dataSourceMap = dataSources;
    }

    /**
     * 添加数据源进入数据源Map
     *
     */
    public static void addDataSourceMap(Map<String, DataSource> dataSources) {
        DynamicDataSource.dataSourceMap.putAll(dataSources);
    }

    public static void removeDataSource(String dataSourceName){
        DynamicDataSource.dataSourceMap.remove(dataSourceName);
    }

    private static void removeDataSource(DataSource dataSource){
        String s = switchValueDataSourceMap(dataSource);
        removeDataSource(s);
    }

    /**
     * 添加一条数据源进入数据源Map
     *
     * @param dataSourceName dataSourceName
     * @param dataSource dataSource
     */
    public static void registerDataSource(String dataSourceName, DataSource dataSource) {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put(dataSourceName, dataSource);
        DynamicDataSource.addDataSourceMap(dataSourceMap);
        log.debug("添加数据源 [{}] 成功", dataSourceName);
    }

    /**
     * 获取数据源Map
     *
     * @return Map<String, DataSource>
     */
    public static Map<String, DataSource> getDataSourceMap() {
        return DynamicDataSource.dataSourceMap;
    }

    /**
     * 通过一个DataSource返回数据源名称
     *
     * @param dataSource dataSource
     * @return String
     */
    public static String switchValueDataSourceMap(DataSource dataSource) {
        if (dataSource != null) {
            for (String key : DynamicDataSource.dataSourceMap.keySet()) {
                if (dataSource.equals(DynamicDataSource.dataSourceMap.get(key))) {
                    return key;
                }
            }
        }
        return null;
    }

    /**
     * 通过数据源名称返回数据源
     *
     * @param dataSourceName dataSourceName
     * @return DataSource
     */
    public static DataSource switchKeyDataSourceMap(String dataSourceName) {
        if (dataSourceName != null && !dataSourceName.equals("")) {
            for (String key : DynamicDataSource.dataSourceMap.keySet()) {
                if (dataSourceName.equals(key)) {
                    return DynamicDataSource.dataSourceMap.get(key);
                }
            }
        }
        return null;
    }

    /**
     * 重置为默认Master数据源
     */
    public static void reset2MasterDataSource() {
        DynamicDataSource.setMasterDataSource("master");
    }

    /**
     * 判断是否存在该数据源
     *
     * @param dataSourceName dataSourceName
     * @return boolean
     */
    public static boolean containsDataSource(String dataSourceName) {
        DataSource dataSource = switchKeyDataSourceMap(dataSourceName);
        return dataSource != null;
    }

    @Override
    @NonNull
    protected DataSource determineTargetDataSource() {
        Assert.notNull(masterDataSource, "DataSource router not initialized");
        DataSource dataSource = DynamicDataSource.getCurrentDataSource();
        if (dataSource == null) {
            log.debug("当前使用数据源: [{}] ", "master");
            return switchKeyDataSourceMap("master");
        } else {
            String dataSourceName = DynamicDataSource.switchValueDataSourceMap(dataSource);
            log.debug("当前使用数据源: [{}] ", dataSourceName);
        }
        return dataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }

}
