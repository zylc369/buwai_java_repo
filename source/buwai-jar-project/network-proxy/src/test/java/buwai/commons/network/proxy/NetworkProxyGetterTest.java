package buwai.commons.network.proxy;

import buwai.commons.network.proxy.entity.NetworkProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * NetworkProxyGetter测试
 *
 * @author 不歪
 * @version 创建时间：2019-03-23 14:30
 */
@Slf4j
public class NetworkProxyGetterTest {

    @Test
    public void testGetProxy() {
        NetworkProxyGetter networkProxyGetter = new NetworkProxyGetter();
        List<NetworkProxy> proxyList = networkProxyGetter.getProxy(null, "http://www.data5u.com/");
        Assert.assertNotNull(proxyList);
        Assert.assertTrue(CollectionUtils.isNotEmpty(proxyList));
        log.info("proxyList={}", proxyList);
    }

}
