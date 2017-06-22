package com.zhukai.framework.spring.integration.http.reader;

import com.zhukai.framework.spring.integration.Constants;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by homolo on 17-6-20.
 */
public class NIOHttpReaderFactory implements HttpReaderFactory {
    private static final Logger logger = Logger.getLogger(NIOHttpReaderFactory.class);

    private SocketChannel channel;
    private ByteBuffer buf;

    public NIOHttpReaderFactory(SocketChannel channel) {
        this.channel = channel;
        buf = ByteBuffer.allocate(Constants.BUFFER_SIZE);
    }

    @Override
    public String readLimitSize(int size) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int total = 0;
            if (size != 0) {
                while (channel.read(buf) != -1 && total < size) {
                    buf.flip();
                    while (buf.hasRemaining() && total < size) {
                        out.write(buf.get());
                        total++;
                    }
                    buf.compact();
                }
            } else {
                int i = 0;
                while (channel.read(buf) != -1 && i != 10) {
                    buf.flip();
                    while (buf.hasRemaining() && (i = buf.get()) != 10) {
                        out.write(i);
                    }
                    buf.compact();
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
