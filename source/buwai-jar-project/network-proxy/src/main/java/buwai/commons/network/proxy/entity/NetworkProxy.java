package buwai.commons.network.proxy.entity;

import buwai.commons.network.proxy.ProxyAnonymousType;
import lombok.Data;

/**
 * @author 不歪
 * @version 创建时间：2019-03-23 14:20
 */
@Data
public class NetworkProxy {

    /**
     * 网络代理类型
     */
    private ProxyAnonymousType proxyAnonymousType;

    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 代理类型
     */
    private String proxyType;

    /**
     * 国家
     */
    private String county;

    /**
     * 响应速度
     * <p>
     * 单位：秒
     */
    private Double responseSpeed;

}
