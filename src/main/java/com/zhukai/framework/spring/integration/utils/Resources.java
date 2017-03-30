package com.zhukai.framework.spring.integration.utils;

import com.zhukai.framework.spring.integration.server.SpringIntegration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by zhukai on 17-2-20.
 */
public class Resources {

    public static InputStream getResourceAsStream(String filePath) {
        return SpringIntegration.runClass.getResourceAsStream(filePath);
    }

    public static InputStream getResourceAsStreamByTmp(String filePath) throws FileNotFoundException {
        File file = getResourceByTmp(filePath);
        if (file != null) {
            return new FileInputStream(file);
        }
        return null;
    }

    public static File getResourceByTmp(String filePath) throws FileNotFoundException {
        Object tmpPath = YmlUtil.getValue("server.fileTmp");
        File file = null;
        if (tmpPath != null) {
            file = new File(tmpPath.toString() + "/" + filePath);
        }
        if (!file.exists()) {
            throw new FileNotFoundException(filePath + " is not a file");
        }
        return file;
    }
}
