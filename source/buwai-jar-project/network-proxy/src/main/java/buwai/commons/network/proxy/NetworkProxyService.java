package buwai.commons.network.proxy;

import buwai.commons.thread.MDCThread;
import buwai.commons.network.proxy.entity.NetworkProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.net.Proxy;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 网络代理服务
 *
 * @author 不歪
 * @version 创建时间：2019-03-23 15:50
 */
@Slf4j
public class NetworkProxyService {

    private static final int RETRY_COUNT = 3;
    private NetworkProxyGetter networkProxyGetter = new NetworkProxyGetter();
    private List<NetworkProxy> networkProxyList = Collections.synchronizedList(new LinkedList<>());

    private final Object LOCK_FOR_LIST = new Object();
    private final Object LOCK_FOR_GET_PROXY = new Object();
    private final AtomicBoolean threadIsRun = new AtomicBoolean(false);

    public Proxy getProxy() {
        Proxy result = getFromList();
        if (null != result) {
            // 当列表中代理IP的个数少于一定数量情况下，异步爬取新的代理数据
            if (networkProxyList.size() < 5) {
                if (threadIsRun.compareAndSet(false, true)) {
                    new MDCThread(() -> {
                        MutablePair<Proxy, NetworkProxy> pair = getFromGetter();
                        if (null == pair) {
                            log.error("异步获取代理失败");
                        } else {
                            networkProxyList.add(pair.getRight());
                        }
                        threadIsRun.set(false);
                    }).execute();
                }
            }
            return result;
        }
        return Optional.ofNullable(getFromGetter()).map(MutablePair::getLeft).orElse(null);
    }

    private Proxy getFromList() {
        if (CollectionUtils.isNotEmpty(networkProxyList)) {
            synchronized (LOCK_FOR_LIST) {
                if (CollectionUtils.isNotEmpty(networkProxyList)) {
                    return NetworkProxyGetter.networkProxy2Proxy(networkProxyList.remove(0));
                }
            }
        }
        return null;
    }

    private MutablePair<Proxy, NetworkProxy> getFromGetter() {
        synchronized (LOCK_FOR_GET_PROXY) {
            List<NetworkProxy> networkProxyList = getNetworkProxyListFromGetter();
            if (CollectionUtils.isEmpty(networkProxyList)) {
                return null;
            }
            NetworkProxy networkProxy = networkProxyList.remove(0);
            Proxy proxy = NetworkProxyGetter.networkProxy2Proxy(networkProxy);
            this.networkProxyList.addAll(networkProxyList);
            return new MutablePair<>(proxy, networkProxy);
        }
    }

    private List<NetworkProxy> getNetworkProxyListFromGetter() {
        List<NetworkProxy> networkProxyList = getNetworkProxyListFromGetterWithFilter(null);
        if (CollectionUtils.isNotEmpty(networkProxyList)) {
            return networkProxyList;
        }

        // 当通过本机IP未能获取代理数据时，使用代理IP获取代理数据
        Proxy proxy = getFromList();
        if (null == proxy) {
            return new LinkedList<>();
        }
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                networkProxyList = getNetworkProxyListFromGetterWithFilter(proxy);
                if (CollectionUtils.isNotEmpty(networkProxyList)) {
                    return networkProxyList;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return new LinkedList<>();
    }

    private List<NetworkProxy> getNetworkProxyListFromGetterWithFilter(Proxy proxy) {
        List<NetworkProxy> networkProxyList = networkProxyGetter.get(proxy);
        if (CollectionUtils.isNotEmpty(networkProxyList)) {
            networkProxyList = networkProxyList.stream().filter(this::isValidNetworkProxy).collect(Collectors.toCollection(LinkedList::new));
        }
        if (CollectionUtils.isNotEmpty(networkProxyList)) {
            return networkProxyList;
        }
        return new LinkedList<>();
    }

    /**
     * 是否是有效的NetworkProxy对象
     *
     * @param networkProxy 对象
     * @return true: 有效；false: 无效
     */
    private boolean isValidNetworkProxy(NetworkProxy networkProxy) {
        if (null == networkProxy.getProxyAnonymousType()) {
            return false;
        }
        if (networkProxy.getProxyAnonymousType() == ProxyAnonymousType.TRANSPARENT) {
            return false;
        }
        if (StringUtils.isBlank(networkProxy.getIp())) {
            return false;
        }
        if (null == networkProxy.getPort()) {
            return false;
        }
        if (StringUtils.isBlank(networkProxy.getProxyType())) {
            return false;
        }
        return true;
    }

}
