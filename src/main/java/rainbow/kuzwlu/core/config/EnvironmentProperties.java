package rainbow.kuzwlu.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rainbow.kuzwlu.enums.DataSourceType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description 配置文件的配置
 * @Date 2020/12/16 18:32
 * @Email kuzwlu@gmail.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentProperties implements Serializable {

    private String mapperPackage;

    private String mapperXml;

    private DataSourceType dataSourceType;

    private String aop_point_out;

    private List<String> require;

    private List<String> require_readonly;

    private MasterDataSource masterDatasource;

    private String masterDatasourceTable;

    private List<String> sqlSubsidiary;

    private String masterDatasourceTableType;

    private String masterDatasourceTableDBName;

    private String masterDatasourceTableStatus;

    private String masterDatasourceTableStatusEnabled;

    private Map<String, SubsidiaryDataSource> subsidiaryDataSourceMap;

}

