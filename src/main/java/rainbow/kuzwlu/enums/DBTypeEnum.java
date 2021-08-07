package rainbow.kuzwlu.enums;

/**
 * @Author kuzwlu
 * @Description 数据源的数据库类型
 * @Date 2020/12/17 01:03
 * @Email kuzwlu@gmail.com
 */
public enum DBTypeEnum {

    MYSQL("mysql"),

    POSTGRESQL("postgresql");

    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
