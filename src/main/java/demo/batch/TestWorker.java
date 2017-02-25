package demo.batch;

import com.zhukai.spring.integration.annotation.batch.Batcher;
import com.zhukai.spring.integration.annotation.batch.Scheduled;
import org.apache.log4j.Logger;

/**
 * Created by zhukai on 17-2-14.
 */
@Batcher
public class TestWorker {
    private static Logger logger = Logger.getLogger(TestWorker.class);

    @Scheduled(fixedRate = 3000000)
    public void test() {
        logger.info("test batch");
    }
}
