package buwai.commons.network.proxy;

import buwai.commons.thread.MDCThread;
import buwai.commons.network.proxy.entity.NetworkProxy;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.requests.Methods;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.RequestBuilder;
import net.dongliu.requests.Requests;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 网络代理获取器
 *
 * @author 不歪
 * @version 创建时间：2019-03-23 14:14
 */
@Slf4j
public class NetworkProxyGetter {

    private static final String[] PROXY_URLS = {
            "http://www.data5u.com/",
//            "http://www.data5u.com/free/",
            "http://www.data5u.com/free/gngn/index.shtml",
//            "http://www.data5u.com/free/gnpt/index.shtml",
            "http://www.data5u.com/free/gwgn/index.shtml"
    };

    private static final int URL_COUNT = PROXY_URLS.length;

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";

    private static final Map<String, String> HEADER = new HashMap<>();

    static {
        HEADER.put("Host", "www.data5u.com");
        HEADER.put("Connection", "keep-alive");
        HEADER.put("Pragma", "no-cache");
        HEADER.put("Cache-Control", "no-cache");
        HEADER.put("Upgrade-Insecure-Requests", "1");
        HEADER.put("User-Agent", USER_AGENT);
        HEADER.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        HEADER.put("Accept-Encoding", "gzip, deflate");
        HEADER.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
//        HEADER.put("Cookie", "JSESSIONID=7BF543DB2D6575A730214E94C473A7E8; Hm_lvt_3406180e5d656c4789c6c08b08bf68c2=1553321222; Hm_lpvt_3406180e5d656c4789c6c08b08bf68c2=1553321222; UM_distinctid=169a928e2203f6-09ea022518f2e6-36667905-13c680-169a928e2245bc; CNZZDATA1260383977=1676873619-1553320346-%7C1553320346");
    }

    private AtomicInteger urlIndex = new AtomicInteger(0);

    public List<NetworkProxy> get(Proxy proxy) {
        List<NetworkProxy> result = Collections.synchronizedList(new ArrayList<>());
        int index = urlIndex.incrementAndGet() % (URL_COUNT - 1);
        String url = PROXY_URLS[index];
        MDCThread thread = new MDCThread(() -> {
            try {
                result.addAll(getProxy(proxy, url));
            } catch (Exception e) {
                log.error("", e);
            }
        });
        thread.execute();
        try {
            thread.await();
        } catch (InterruptedException e) {
            log.error("", e);
        }
        if (urlIndex.get() > URL_COUNT) {
            urlIndex.set(0);
        }
        return result;
    }

    /**
     * 获取代理数据
     *
     * @param proxy 代理对象。可为null
     * @param url   链接
     * @return 返回代理列表
     */
    List<NetworkProxy> getProxy(Proxy proxy, String url) {
        List<NetworkProxy> result = new ArrayList<>();
        RequestBuilder requestBuilder = Requests.newRequest(Methods.GET, url).timeout(10000).headers(HEADER);
        if (null != proxy) {
            requestBuilder.proxy(proxy);
        }
        RawResponse response = requestBuilder.userAgent(USER_AGENT).send().charset(StandardCharsets.UTF_8);
        String data = new String(response.readToBytes());
        Document rootDoc = Jsoup.parse(data);
        Elements uls = rootDoc.getElementsByTag("ul").stream().filter(this::isProxyUl).collect(Collectors.toCollection(Elements::new));
        if (CollectionUtils.isEmpty(uls)) {
            log.warn("uls is empty. url={},data={}", url, data);
            return new ArrayList<>();
        }
        for (Element ul : uls) {
            Elements spans = ul.getElementsByTag("span");
            String ip = spans.get(0).text().trim();

            if (StringUtils.isBlank(ip)) {
                log.warn("ip is blank. ul={}", ul);
                continue;
            }

            String strPort = spans.get(1).text();
            if (StringUtils.isBlank(strPort)) {
                log.warn("port is blank. ul={}", ul);
                continue;
            }
//            String portKey = spans.get(1).getElementsByTag("li").get(0).attr("class").trim().split(" ")[1];
            String portKey = spans.get(1).getElementsByClass("port").get(0).attr("class").trim().split(" ")[1];
            if (StringUtils.isBlank(portKey)) {
                log.warn("portKey is blank. ul={}", ul);
                continue;
            }
            int port = portDecode(portKey);

            String strProxyAnonymousType = spans.get(2).text();
            if (StringUtils.isBlank(strProxyAnonymousType)) {
                log.warn("proxyAnonymousType is blank. ul={}", ul);
                continue;
            }
            ProxyAnonymousType proxyAnonymousType = ProxyAnonymousType.get(strProxyAnonymousType.trim());

            String proxyType = spans.get(3).text().trim();
            if (StringUtils.isBlank(proxyType)) {
                log.warn("proxyType is blank. ul={}", ul);
                continue;
            }
            String country = spans.get(4).text().trim();
            String strResponseSpeed = spans.get(7).text().trim();
            Double responseSpeed = null;
            if (StringUtils.isNotBlank(strResponseSpeed) && strResponseSpeed.endsWith("秒")) {
                responseSpeed = Double.parseDouble(strResponseSpeed.substring(0, strResponseSpeed.indexOf("秒")));
            }

            NetworkProxy networkProxy = new NetworkProxy();
            networkProxy.setIp(ip);
            networkProxy.setPort(port);
            networkProxy.setProxyAnonymousType(proxyAnonymousType);
            networkProxy.setProxyType(StringUtils.isBlank(proxyType) ? null : proxyType);
            networkProxy.setCounty(StringUtils.isBlank(country) ? null : country);
            networkProxy.setResponseSpeed(responseSpeed);

            result.add(networkProxy);
        }
        return result;
    }

    /**
     * 端口解密
     *
     * @param encoded  已加密的端口
     * @return 返回解密后的结果
     */
    private int portDecode(String encoded) {
        String[] splitArray = encoded.split("");
        List<Integer> list = new ArrayList<>();
        for (String s : splitArray) {
            list.add("ABCDEFGHIZ".indexOf(s));
        }
        StringBuilder str = new StringBuilder();
        for (Integer integer : list) {
            str.append(integer);
        }
        int decoded = Integer.parseInt(str.toString());
        return decoded >> 3;
    }

    private boolean isProxyUl(Element ul) {
        if (!ul.hasClass("l2")) {
            return false;
        }
        if (9 != ul.getElementsByTag("span").size()) {
            return false;
        }
        return true;
    }

    public static Proxy.Type parseProxyType(String proxyType) {
        if (null == proxyType) {
            return Proxy.Type.HTTP;
        }
        if ("socks5".equals(proxyType)) {
            return Proxy.Type.SOCKS;
        } else {
            return Proxy.Type.HTTP;
        }
    }

    public static Proxy networkProxy2Proxy(NetworkProxy networkProxy) {
        return new Proxy(parseProxyType(networkProxy.getProxyType()),
                new InetSocketAddress(networkProxy.getIp(), networkProxy.getPort()));
    }

}
