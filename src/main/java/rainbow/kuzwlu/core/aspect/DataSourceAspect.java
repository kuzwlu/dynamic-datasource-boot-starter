package rainbow.kuzwlu.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.*;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import rainbow.kuzwlu.core.annotation.DataSource;
import rainbow.kuzwlu.core.config.EnvironmentProperties;
import rainbow.kuzwlu.core.datasource.DynamicDataSource;
import rainbow.kuzwlu.exception.DataSourceException;
import rainbow.kuzwlu.exception.MybatisPlusException;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @Author kuzwlu
 * @Description 用于切换数据库注解的切面
 * @Date 2020/12/15 01:11
 * @Email kuzwlu@gmail.com
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSourceAspect {

    @Resource
    private EnvironmentProperties environmentProperties;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public DefaultPointcutAdvisor defaultPointcutAdvisor2() {
        String aop = "execution(* " + environmentProperties.getMapperPackage() + "..*.*(..)) " +
                "";
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(aop);

        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(new DataSourceAdvice(environmentProperties));
        return advisor;
    }

}

@Slf4j
class DataSourceAdvice implements MethodInterceptor {

    private final EnvironmentProperties environmentProperties;

    public DataSourceAdvice(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = methodInvocation.getMethod();
        DataSourceException dataSourceException = new DataSourceException("[" + method.getDeclaringClass().getName() + "]：切换数据源失败 请检查以下配置: " +
                " 1、配置文件数据源的(type)设置是否正确" +
                " 2、配置文件声明的副数据源名称(subsidiary)是否填写" +
                " 3、@DataSource注解标识的数据源名称和配置文件声明的副数据源名称不匹配");
        DataSource DBDataSource;
        //获取方法上的@DataSource注解
        if (method.isAnnotationPresent(DataSource.class)) {
            DBDataSource = method.getAnnotation(DataSource.class);
            if (DynamicDataSource.containsDataSource(DBDataSource.value())) {
                DynamicDataSource.setMasterDataSource(DBDataSource.value());
            } else {
                throw dataSourceException;
            }
            //获取类上的@DataSource注解
        } else if (method.getDeclaringClass().isAnnotationPresent(DataSource.class)) {
            DBDataSource = method.getDeclaringClass().getAnnotation(DataSource.class);
            if (DynamicDataSource.containsDataSource(DBDataSource.value())) {
                DynamicDataSource.setMasterDataSource(DBDataSource.value());
            } else {
                throw dataSourceException;
            }

        } else {
            //获取到接口上的@DataSource注解
            //忘记ProxyFactory，好像是springboot的代理
            Object target = methodInvocation.getThis();
            Assert.notNull(target, dataSourceException.getMessage());
            ProxyFactory proxyFactory = new ProxyFactory(target);
            Class<?>[] proxiedInterfaces = proxyFactory.getProxiedInterfaces();
            for (Class<?> proxiedInterface : proxiedInterfaces) {
                String name = proxiedInterface.getName();
                if (name.startsWith(environmentProperties.getMapperPackage())) {
                    boolean annotationPresent = proxiedInterface.isAnnotationPresent(DataSource.class);
                    if (annotationPresent) {
                        DBDataSource = proxiedInterface.getAnnotation(DataSource.class);
                        if (DynamicDataSource.containsDataSource(DBDataSource.value())) {
                            DynamicDataSource.setMasterDataSource(DBDataSource.value());
                        } else {
                            throw dataSourceException;
                        }
                    }
                }
            }
        }
        //先注解，避免每次查询（不使用&& (@within(rainbow.kuzwlu.core.annotation.DataSource) || @annotation(rainbow.kuzwlu.core.annotation.DataSource))）
        //1.首先从数据库获取t_sys_sql的数据，判断对应数据是否存在关联的mapperName字段（以,分割）
        //  存在，则检查在缓存中是否存在该条数据，如果存在，直接返回数据源，否则创建对应的dataSource加入DynamicDataSource的dataSourceMap
        //  不存在，则检测注解
        Object proceed;
        try {
            proceed = methodInvocation.proceed();
        } catch (Exception e) {
            throw new MybatisPlusException(e.getMessage());
        }
        //重置为Master数据源
        DynamicDataSource.reset2MasterDataSource();
        return proceed;
    }

}
