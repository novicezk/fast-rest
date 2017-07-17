package com.zhukai.framework.spring.integration.util;

import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.common.FileEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Resources {

    public static InputStream getResourceAsStream(String filePath) {
        return SpringIntegration.getRunClass().getResourceAsStream(filePath);
    }

    public static InputStream getResourceAsStreamByTmp(String filePath) throws FileNotFoundException {
        File file = getResourceByTmp(filePath);
        if (file != null) {
            return new FileInputStream(file);
        }
        return null;
    }

    public static File getResourceByTmp(String filePath) throws FileNotFoundException {
        String tmpPath = SpringIntegration.getServerConfig().getFileTmp();
        File file = new File(tmpPath + filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        return file;
    }

    public static FileEntity getFileEntityByTmp(String filePath) throws FileNotFoundException {
        File file = getResourceByTmp(filePath);
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getName());
        fileEntity.setInputStream(new FileInputStream(file));
        return fileEntity;
    }

    private Resources() {
    }
}
