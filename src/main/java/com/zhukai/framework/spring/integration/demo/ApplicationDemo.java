package com.zhukai.framework.spring.integration.demo;

import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;
import com.zhukai.framework.spring.integration.http.FileEntity;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.util.Resources;

import java.io.IOException;

/**
 * Created by homolo on 17-7-14.
 */
@RestController
public class ApplicationDemo {
    public static void main(String[] args) {
        SpringIntegration.run(ApplicationDemo.class);
    }

    @RequestMapping("/upload")
    public String upload(FileEntity fileEntity) {
        try {
            Resources.saveFile(fileEntity);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @RequestMapping("/hello")
    public String hello(HttpRequest request) {
        return "hello,world";
    }
}
