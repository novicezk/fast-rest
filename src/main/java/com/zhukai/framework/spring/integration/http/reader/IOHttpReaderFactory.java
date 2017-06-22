package com.zhukai.framework.spring.integration.http.reader;

import com.zhukai.framework.spring.integration.Constants;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by homolo on 17-6-20.
 */
public class IOHttpReaderFactory implements HttpReaderFactory {
    private static final Logger logger = Logger.getLogger(IOHttpReaderFactory.class);

    private InputStream inputStream;

    public IOHttpReaderFactory(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public String readLimitSize(int size) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int total = 0;
            if (size != 0) {
                while (total < size) {
                    out.write(inputStream.read());
                    total++;
                }
            } else {
                int i;
                while ((i = inputStream.read()) != 10 && i != -1) {
                    out.write(i);
                }
            }
            String result = new String(out.toByteArray(), Constants.CHARSET);
            if (result.length() > 0 && result.charAt(result.length() - 1) == 13) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            logger.error("Read Http error", e);
            return null;
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
