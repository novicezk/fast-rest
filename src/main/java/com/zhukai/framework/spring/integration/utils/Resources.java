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

    public static InputStream getResourceAsStreamByTmp(String filePath) {
        File file = getResourceByTmp(filePath);
        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getResourceByTmp(String filePath) {
        Object tmpPath = YmlUtil.getValue("server.fileTmp");
        if (tmpPath != null) {
            return new File(tmpPath.toString() + "/" + filePath);
        }
        return null;
    }
}
