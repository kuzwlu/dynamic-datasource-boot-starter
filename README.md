# dynamic-datasource-boot-starter
##### 一个基于SpringBoot的Mybatis-Plus多数据源依赖包
    
    基于JDK1.8版本，SpringBoot2.4.0，mybatis-plus-boot-starter3.4.1

    可使用注解和指定数据源来切换数据源，达到使用不同数据库的效果（可导入其他SQL驱动，动态注册SQLTool-支持其他类型数据源）

    #四种状态  一、数据库中读取副数据源 二、配置文件读取数据源 三、两者一起 四、没有副数据源(默认  NONE)
    通过配置文件添加数据源
    通过指定数据库和指定表字段添加数据源
    
## 使用方法：（可见sqlRun项目：https://github.com/kuzwlu/sqlRun ）
 #### 1、引入依赖包（本地依赖库路径：rainbow/kuzwlu/）
    <dependency>
            <groupId>rainbow.kuzwlu</groupId>
            <artifactId>dynamic-datasource-boot-starter</artifactId>
            <version>1.2</version>
    </dependency>
#### 2、application.yml配置示例
    rainbow:
        kuzwlu:
            #mapper包名
            mapper-package: rainbow.kuzwlu.web.mapper
            mapper-xml: classpath*:mapper/*.xml
            #设置数据库(动态数据源)
            # 一、无Service层（无事物管理）
            # 1、需要mapper接口上    标明@DataSource注解
            # 二、有Service层（有事物管理，可在Serviec层使用@Transactional注解）
            # 1、需要在service接口或者service接口的实现类    标明@DataSource注解
            datasource:
            #四种状态  一、数据库中读取副数据源 二、配置文件读取数据源 三、两者一起 四、没有副数据源(默认  NONE)
            #在DataSourceType枚举类下
            #DB PROPERTY TOGETHER NONE
            type: TOGETHER

          #如果不设置则不设置事务
          transaction:
            #切面
            aop-point-out: execution(* ${rainbow.kuzwlu.mapper-package}..*.*(..))
            #方法名规则限制，以下会加入事务管理当中
            require: add*,save*,create*,insert*,submit*,del*,remove*,update*,exec*,set*
            #对于查询方法，根据实际情况添加事务管理 可能存在查询多个数据时，已查询出来的数据刚好被改变的情况
            require-readonly: get*,select*,query*,find*,list*,count*,is*
          #主
          master:
            #副数据源---表 如果type为NONE可以不填写或者删除datasource-table等五个配置
            datasource-table: t_sys_sql
            #数据库自定义字段名，用来判断类型，数据库名称，状态
            datasource-table-type: type
            datasource-table-DBName: DBName
            datasource-table-status: status
            #status启用状态
            datasource-table-status-enabled: 1
            ###########################################################
            # mysql默认数据库的配置
            driverClassName: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://localhost:3306/sql?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
            username: root
            password: 20001102zzw
            initialSize: 5
            minIdle: 5
            maxActive: 20
            # 配置获取连接等待超时的时间
            maxWait: 60000
            # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            timeBetweenEvictionRunsMillis: 60000
            # 配置一个连接在池中最小生存的时间，单位是毫秒
            minEvictableIdleTimeMillis: 300000
            validationQuery: SELECT 1 FROM DUAL
    
          #副---未声明则数据库的数据源数据不生效，如果type为NONE可以不填写subsidiary
          subsidiary: sqls,postgresql,test2
          sqls:
            # mysql默认数据库的配置
            driverClassName: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://localhost:3306/sqls?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
            username: sqls
            password: sqls
            initialSize: 5
            minIdle: 5
            maxActive: 20
            # 配置获取连接等待超时的时间
            maxWait: 60000
            # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            timeBetweenEvictionRunsMillis: 60000
            # 配置一个连接在池中最小生存的时间，单位是毫秒
            minEvictableIdleTimeMillis: 300000
            validationQuery: SELECT 1 FROM DUAL
          postgresql:
            # mysql默认数据库的配置
            driverClassName: org.postgresql.Driver
            url: jdbc:postgresql://localhost:5432/postgres
            username: postgres
            password: 20001102zzw
            initialSize: 5
            minIdle: 5
            maxActive: 20
            # 配置获取连接等待超时的时间
            maxWait: 60000
            # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            timeBetweenEvictionRunsMillis: 60000
            # 配置一个连接在池中最小生存的时间，单位是毫秒
            minEvictableIdleTimeMillis: 300000
            validationQuery: SELECT 1 FROM DUAL

#### 3、使用注解@DataSource注解在Mapper接口上即可自动切换数据源
    支持@Mapper和XML文件形式


### 提供一个接口SQLStatement-->实现ColumnInfo（用于存储列名和相对于的描述）【用于注册SQLTool】
    实现该接口用于从information_schema查询数据源信息
    -包含表名、列名、列信息，值

### 提供工具类SQLTool-->实现DBinfo 创建一个数据源的快捷操作工具 【可实例化SQLTool实现匿名内部类SQLStatement】
    可通过SQL语句实现增删改查
    1、查询单条记录
    2、查询多条记录
    3、增删改
    4、获取DBTypeEnum枚举类的相关SQLTool
    5、查询该数据源的所有表
    6、查询该数据源的表的列名
    7、查询该数据源的表的相关列名的值

### 提供DataSourceRunTime 
    1、获取DBTypeEnum枚举类的相关SQLTool
    2、动态注册相关数据源的SQLTool工具类
    3、动态注册数据源
    4、移除数据源
    5、获取当前主数据源
    6、获取全部数据源

## BUG：
    事务管理器只能通过配置文件实现，使用注解和类无效