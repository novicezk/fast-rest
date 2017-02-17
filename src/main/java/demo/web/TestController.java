package demo.web;

import com.zhukai.spring.integration.annotation.core.Value;
import com.zhukai.spring.integration.annotation.web.RequestMapping;
import com.zhukai.spring.integration.annotation.web.RestController;

/**
 * Created by zhukai on 17-2-17.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Value(value = "integration.test.author.name", fileName = "test.yml")
    private String authorName;

    @Value(value = "integration.test.author.age", fileName = "test.yml")
    private int authorAge;

    @RequestMapping("/getAuthorInfo")
    public String getAuthorInfo() {
        return "name: " + authorName + "; age: " + authorAge;
    }
}
