package buwai.commons.network.proxy;

/**
 * 网络代理匿名类型
 *
 * @author 不歪
 * @version 创建时间：2019-03-23 14:22
 */
public enum ProxyAnonymousType {

    UNKNOWN("未知"),
    ANONYMOUS_HIGH("高匿"),
    ANONYMOUS("匿名"),
    TRANSPARENT("透明"),
    ;
    public String description;

    ProxyAnonymousType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ProxyAnonymousType get(String description) {
        if (null == description) {
            return ProxyAnonymousType.UNKNOWN;
        }
        for (ProxyAnonymousType type : ProxyAnonymousType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        return ProxyAnonymousType.UNKNOWN;
    }
    
}
