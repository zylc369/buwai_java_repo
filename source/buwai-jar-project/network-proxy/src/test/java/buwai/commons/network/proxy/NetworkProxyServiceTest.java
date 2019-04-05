package buwai.commons.network.proxy;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.Proxy;

/**
 * NetworkProxyService测试
 *
 * @author 不歪
 * @version 创建时间：2019-03-24 00:30
 */
public class NetworkProxyServiceTest {

    private NetworkProxyService networkProxyService = new NetworkProxyService();

    @Test
    public void test() {
        Proxy proxy = networkProxyService.getProxy();
        Assert.assertNotNull(proxy);
        System.out.println("proxy=" + proxy);
    }

}
