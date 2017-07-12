package com.zhukai.framework.spring.integration.http.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpReader extends AbstractHttpReader {
    private InputStream inputStream;

    public HttpReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected byte[] readByteArrayLimitSize(int size) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (size == 0) {
                int i;
                while ((i = inputStream.read()) != 10 && i != -1) {
                    out.write(i);
                }
            } else {
                int total = 0;
                while (total < size) {
                    out.write(inputStream.read());
                    total++;
                }
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            out.close();
        }
    }

}
