package com.zhukai.framework.fast.rest.util;

import com.zhukai.framework.fast.rest.FastRestApplication;
import com.zhukai.framework.fast.rest.common.FileEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Resources {

    public static InputStream getResourceAsStream(String filePath) {
        return FastRestApplication.getRunClass().getResourceAsStream(filePath);
    }

    public static InputStream getResourceAsStreamByTmp(String filePath) throws FileNotFoundException {
        File file = getResourceByTmp(filePath);
        return new FileInputStream(file);
    }

    public static File getResourceByTmp(String filePath) throws FileNotFoundException {
        String tmpPath = FastRestApplication.getServerConfig().getFileTmp();
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
