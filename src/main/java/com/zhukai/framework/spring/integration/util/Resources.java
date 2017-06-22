package com.zhukai.framework.spring.integration.util;

import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.spring.integration.config.ServerConfig;
import com.zhukai.framework.spring.integration.http.FileEntity;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

/**
 * Created by zhukai on 17-2-20.
 */
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
        String tmpPath = ConfigureBeanFactory.getInstance().getBean(ServerConfig.class).getFileTmp();
        File file = new File(tmpPath + "/" + filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath + " is not a file");
        }
        return file;
    }

    public static File saveFile(FileEntity fileEntity) throws IOException {
        return saveFile(fileEntity, true);
    }

    public static File saveFile(FileEntity fileEntity, boolean cover) throws IOException {
        String fileTmp = ConfigureBeanFactory.getInstance().getBean(ServerConfig.class).getFileTmp();
        return saveFile(fileEntity, fileTmp + "/" + fileEntity.getFileName(), cover);
    }

    public static File saveFile(FileEntity fileEntity, String path) throws IOException {
        return saveFile(fileEntity, path, true);
    }

    /**
     * @param fileEntity
     * @param path       文件的全名，如/home/username/test.zip
     * @param cover      是否覆盖重名文件
     * @return
     * @throws IOException
     */
    public static File saveFile(FileEntity fileEntity, String path, boolean cover) throws IOException {
        File file = new File(path);
        if (file.exists() && !cover) {
            throw new FileAlreadyExistsException(path);
        } else if (cover) {
            new File(path).delete();
        }
        InputStream in = fileEntity.getInputStream();
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int readIndex;
            while ((readIndex = in.read(buffer)) != -1) {
                out.write(buffer, 0, readIndex);
            }
            out.flush();
            return file;
        } catch (Exception e) {
            throw e;
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    private Resources() {
    }
}
