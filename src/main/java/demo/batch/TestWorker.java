package demo.batch;

import com.zhukai.spring.integration.annotation.batch.Batcher;
import com.zhukai.spring.integration.annotation.batch.Scheduled;

/**
 * Created by zhukai on 17-2-14.
 */
@Batcher
public class TestWorker {
    @Scheduled(fixedRate = 5000)
    public void test() {
        System.out.println("test batch");
    }
}
