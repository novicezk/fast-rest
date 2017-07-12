package com.zhukai.framework.spring.integration.util;

import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.constant.IntegrationConstants;
import com.zhukai.framework.spring.integration.http.FileEntity;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

public class Resources {
    private static final Logger logger = Logger.getLogger(Resources.class);

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
        File file = new File(tmpPath + "/" + filePath);
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

    /**
     * @param fileEntity
     * @return 保存后的文件
     * @throws IOException
     */
    public static File saveFile(FileEntity fileEntity) throws IOException {
        return saveFile(fileEntity, true);
    }

    /**
     * @param fileEntity
     * @param cover      是否覆盖重名文件
     * @return 保存后的文件
     * @throws IOException
     */
    public static File saveFile(FileEntity fileEntity, boolean cover) throws IOException {
        String fileTmp = SpringIntegration.getServerConfig().getFileTmp();
        return saveFile(fileEntity, fileTmp + "/" + fileEntity.getFileName(), cover);
    }

    /**
     * @param fileEntity
     * @param path       文件的全名，如/home/username/test.zip
     * @return 保存后的文件
     * @throws IOException
     */
    public static File saveFile(FileEntity fileEntity, String path) throws IOException {
        return saveFile(fileEntity, path, true);
    }

    /**
     * @param fileEntity
     * @param path       文件的全名，如/home/username/test.zip
     * @param cover      是否覆盖重名文件
     * @return 保存后的文件
     * @throws IOException
     */
    public static File saveFile(FileEntity fileEntity, String path, boolean cover) throws IOException {
        File file = new File(path);
        if (file.exists() && !cover) {
            throw new FileAlreadyExistsException(path);
        } else if (cover) {
            file.delete();
        }
        InputStream in = fileEntity.getInputStream();
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buffer = new byte[IntegrationConstants.BUFFER_SIZE];
            int readIndex;
            while ((readIndex = in.read(buffer)) != -1) {
                out.write(buffer, 0, readIndex);
            }
            out.flush();
            return file;
        } catch (Exception e) {
            logger.error("save file error", e);
            return null;
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    private Resources() {
    }
}
