package buwai.commons.log;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * 存放和取出日志跟踪信息
 *
 * @author 不歪
 * @version 创建时间：2019-03-26 16:56
 */
public class TraceUtils {

    public static final String KEY_TRACE_ID = "TRACE_ID";
    public static final String KEY_THREAD_ID = "THREAD_ID";

    public static void putThreadId() {
        MDC.put(KEY_THREAD_ID, UUID.randomUUID().toString().replace("-", ""));
    }

    public static String clearThreadId() {
        return clear(KEY_THREAD_ID);
    }

    public static void putTraceId() {
        MDC.put(KEY_TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
    }

    public static String getTraceId() {
        return MDC.get(KEY_TRACE_ID);
    }

    public static String clearTraceId() {
        return clear(KEY_TRACE_ID);
    }

    public static void clear() {
        MDC.clear();
    }

    public static String clear(String key) {
        String value = MDC.get(key);
        MDC.put(key, null);
        return value;
    }

}
