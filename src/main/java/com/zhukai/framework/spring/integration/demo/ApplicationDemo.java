package com.zhukai.framework.spring.integration.demo;


import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.annotation.web.PathVariable;
import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;
import com.zhukai.framework.spring.integration.http.FileEntity;
import com.zhukai.framework.spring.integration.util.Resources;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
public class ApplicationDemo {

    public static void main(String[] args) {
        SpringIntegration.run(ApplicationDemo.class);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello,world";
    }

    @RequestMapping("/upload")
    public void upload(FileEntity fileEntity) throws IOException {
        Resources.saveFile(fileEntity);
    }

    @RequestMapping("/download/{path}")
    public FileEntity download(@PathVariable("path") String path) throws FileNotFoundException {
        return Resources.getFileEntityByTmp(path);
    }

}
