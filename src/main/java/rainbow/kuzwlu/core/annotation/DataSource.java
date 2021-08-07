package rainbow.kuzwlu.core.annotation;

import java.lang.annotation.*;

/**
 * @Author kuzwlu
 * @Description 切换数据库注解, 默认master
 * 一、无Service层
 * 1、需要mapper接口上    标明@DataSource注解
 * 二、有Service层
 * 3、需要在service接口或者service接口的实现类    标明@DataSource注解
 * @Date 2020/12/15 00:50
 * @Email kuzwlu@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String value() default "master";
}

