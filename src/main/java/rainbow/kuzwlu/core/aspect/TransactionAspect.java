package rainbow.kuzwlu.core.aspect;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import rainbow.kuzwlu.core.config.EnvironmentProperties;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author kuzwlu
 * @Description 事物管理切面有bug
 * @Date 2020/12/14 04:32
 * @Email kuzwlu@gmail.com
 */
@Configuration
public class TransactionAspect {

    @Resource
    private EnvironmentProperties environmentProperties;

    @Bean
    public TransactionInterceptor txAdvice(
            @Autowired TransactionManager transactionManager
    ) {

        DefaultTransactionAttribute txAttr_REQUIRED = new DefaultTransactionAttribute();
        txAttr_REQUIRED.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        DefaultTransactionAttribute txAttr_REQUIRED_READONLY = new DefaultTransactionAttribute();
        txAttr_REQUIRED_READONLY.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        txAttr_REQUIRED_READONLY.setReadOnly(true);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        List<String> requires = environmentProperties.getRequire();
        //方法名规则限制，必须以下列开头才会加入事务管理当中
        for (String require : requires) {
            source.addTransactionalMethod(require, txAttr_REQUIRED);
        }

        //对于查询方法，根据实际情况添加事务管理 可能存在查询多个数据时，已查询出来的数据刚好被改变的情况
        List<String> require_readonlyS = environmentProperties.getRequire_readonly();
        for (String require_readonly : require_readonlyS) {
            source.addTransactionalMethod(require_readonly, txAttr_REQUIRED_READONLY);
        }

        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public Advisor txAdviceAdvisor(
            @Autowired TransactionInterceptor txAdvice
    ) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(environmentProperties.getAop_point_out());
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }


}
