package demo;


import com.zhukai.spring.integration.server.SpringIntegration;

import java.io.IOException;

/**
 * Created by zhukai on 17-1-12.
 */
public class Application {

    public static void main(String[] args) throws IOException {
        SpringIntegration.run(Application.class);
    }
}
