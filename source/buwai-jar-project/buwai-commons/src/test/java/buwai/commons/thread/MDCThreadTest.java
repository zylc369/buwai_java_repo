package buwai.commons.thread;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * MDCThread测试
 *
 * @author 不歪
 * @version 创建时间：2019-03-27 13:39
 */
@Slf4j
public class MDCThreadTest {

    @Test
    public void test() throws InterruptedException {
        MDCThread thread = new MDCThread(() -> log.info("123"));
        thread.execute();
        Thread.sleep(3000);
        System.out.println("end");
    }

}
