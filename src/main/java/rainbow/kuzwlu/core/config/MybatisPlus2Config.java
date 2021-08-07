package rainbow.kuzwlu.core.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/30 12:40
 * @Email kuzwlu@gmail.com
 */
@Configuration
@Slf4j
@EnableTransactionManagement
public class MybatisPlus2Config {

    @Resource
    private EnvironmentProperties environmentProperties;

    /**
     * 设置MapperScan
     */
    @Bean
    public void initMapperScan() {
        String mapperPackage = environmentProperties.getMapperPackage() + ".*";
        new MapperScannerConfigurer().setBasePackage(mapperPackage);
        log.debug("Setting MapperScan [" + mapperPackage + "]");
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager multipleTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 自动填充
     *
     * @return GlobalConfig
     */
    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        conf.setMetaObjectHandler(new MyMetaObjectHandler());
        return conf;
    }

}

class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("gmtCreate", new Date(), metaObject);
        this.setFieldValByName("gmtModified", new Date(), metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtModified", new Date(), metaObject);
    }
}
