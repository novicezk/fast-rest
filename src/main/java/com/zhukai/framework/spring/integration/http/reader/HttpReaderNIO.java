package com.zhukai.framework.spring.integration.http.reader;

import com.zhukai.framework.spring.integration.constant.IntegrationConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by homolo on 17-6-20.
 */
public class HttpReaderNIO extends AbstractHttpReader {

    private SocketChannel channel;
    private ByteBuffer buf;

    public HttpReaderNIO(SocketChannel channel) {
        this.channel = channel;
        buf = ByteBuffer.allocate(IntegrationConstants.BUFFER_SIZE);
    }

    @Override
    protected byte[] readByteArrayLimitSize(int size) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (size == 0) {
                int i = 0;
                while (channel.read(buf) != -1 && i != 10) {
                    buf.flip();
                    while (buf.hasRemaining() && (i = buf.get()) != 10) {
                        out.write(i);
                    }
                    buf.compact();
                }
            } else {
                int total = 0;
                while (channel.read(buf) != -1 && total < size) {
                    buf.flip();
                    while (buf.hasRemaining() && total < size) {
                        out.write(buf.get());
                        total++;
                    }
                    buf.compact();
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