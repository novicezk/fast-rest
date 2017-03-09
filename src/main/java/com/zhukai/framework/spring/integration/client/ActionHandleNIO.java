package com.zhukai.framework.spring.integration.client;


import com.zhukai.framework.spring.integration.server.SpringIntegration;
import com.zhukai.framework.spring.integration.utils.JsonUtil;
import com.zhukai.framework.spring.integration.common.HttpParser;
import com.zhukai.framework.spring.integration.common.HttpRequest;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by zhukai on 17-1-12.
 */
public class ActionHandleNIO extends AbstractActionHandle {

    private SocketChannel socketChannel;

    public ActionHandleNIO(SocketChannel socketChannel, HttpRequest request) {
        this.socketChannel = socketChannel;
        this.request = request;
    }

    @Override
    protected void respond() {
        try {
            if (response == null) {
                return;
            }
            String httpHeader = HttpParser.parseHttpString(response);
            ByteBuffer buffer = ByteBuffer.allocate(SpringIntegration.BUFFER_SIZE);
            sendMessageByBuffer(httpHeader + "\r\n", buffer);
            if (response.getResult() instanceof FileInputStream) {
                FileChannel fileChannel = ((FileInputStream) response.getResult()).getChannel();
                fileChannel.transferTo(0, fileChannel.size(), socketChannel);
                fileChannel.close();
            } else if (response.getResult() instanceof InputStream) {
                InputStream in = (InputStream) response.getResult();
                int byteCount;
                byte[] bytes = new byte[SpringIntegration.BUFFER_SIZE];
                while ((byteCount = in.read(bytes)) != -1) {
                    buffer.clear();
                    buffer.put(bytes, 0, byteCount);
                    buffer.flip();
                    socketChannel.write(buffer);
                }
                in.close();
            } else {
                String json = JsonUtil.toJson(response.getResult());
                sendMessageByBuffer(json, buffer);
            }
            socketChannel.register(SpringIntegration.selector, SelectionKey.OP_WRITE);
        } catch (Exception e) {
            logger.error("Respond error", e);
        }
    }

    public void sendMessageByBuffer(String message, ByteBuffer buffer) throws Exception {
        int endIndex = 0;
        while (endIndex < message.length()) {
            buffer.clear();
            int startIndex = endIndex;
            endIndex = Math.min(endIndex + SpringIntegration.BUFFER_SIZE / 3, message.length());
            buffer.put(message.substring(startIndex, endIndex).getBytes());
            buffer.flip();
            socketChannel.write(buffer);
        }
    }

}
