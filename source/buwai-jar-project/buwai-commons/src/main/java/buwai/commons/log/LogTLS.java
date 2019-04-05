package buwai.commons.log;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 日志线程局部存储
 *
 * @author 不歪
 * @version 创建时间：2019-03-26 16:47
 */
public class LogTLS {

    private static final Map<String, Stack<String>> THREAD_LOCAL = new HashMap<>();

    /**
     * 将值存入栈中
     *
     * @param key   键
     * @param value 值
     */
    public static void push(String key, String value) {
        Stack<String> stack = THREAD_LOCAL.computeIfAbsent(key, v -> new Stack<>());
        String oldValue = MDC.get(key);
        if (null != oldValue) {
            stack.push(oldValue);
        }
        MDC.put(key, value);
    }

    /**
     * 将栈顶数据弹出，然后将栈顶元素存入线程局部存储中
     *
     * @param key 键
     * @return 返回弹出的值
     */
    public static String pop(String key) {
        Stack<String> stack = THREAD_LOCAL.computeIfAbsent(key, v -> new Stack<>());
        if (stack.isEmpty()) {
            return null;
        }
        String value = stack.pop();
        if (null == value) {
            return null;
        }
        MDC.put(key, stack.isEmpty() ? null : stack.peek());
        return value;
    }

}
