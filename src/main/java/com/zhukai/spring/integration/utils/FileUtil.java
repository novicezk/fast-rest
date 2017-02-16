package com.zhukai.spring.integration.utils;

import com.zhukai.spring.integration.server.SpringIntegration;

import java.io.File;
import java.io.InputStream;
/**
 * Created by zhukai on 17-2-16.
 */
public class FileUtil {

    public static File getFileByTmp(String fileName) {
        return new File(SpringIntegration.getServerConfig().getFileTmp() + "/" + fileName);
    }

    public static InputStream getInputStreamByProject(String pathFileName) {
        return SpringIntegration.runClass.getResourceAsStream(pathFileName);
    }
}
