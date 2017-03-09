package com.zhukai.framework.spring.integration.demo;


import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;
import com.zhukai.framework.spring.integration.common.FileBean;
import com.zhukai.framework.spring.integration.server.SpringIntegration;

import java.io.*;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
public class TestApplication {

    public static void main(String[] args) {
        SpringIntegration.run(TestApplication.class);
    }

    @RequestMapping("/hello")
    public String hello(FileBean uploadFile) throws IOException {
        if (uploadFile == null) {
            return "hello,world";
        }
        String filePath = SpringIntegration.getServerConfig().getFileTmp() + "/" + uploadFile.getFileName();
        File file = new File(filePath);
        if (file.exists()) {
            new File(filePath).delete();
        }
        InputStream in = uploadFile.getInputStream();
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int readIndex;
            while ((readIndex = in.read(buffer)) != -1) {
                out.write(buffer, 0, readIndex);
            }
            out.flush();
            return "upload success";
        } catch (Exception e) {
            e.printStackTrace();
            return "upload error";
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }

    }

}
