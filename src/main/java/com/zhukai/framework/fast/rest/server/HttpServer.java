package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.constant.IntegrationConstants;
import com.zhukai.framework.fast.rest.handle.ActionHandleNIO;
import com.zhukai.framework.fast.rest.http.HttpParser;
import com.zhukai.framework.fast.rest.http.HttpResponse;
import com.zhukai.framework.fast.rest.http.request.HttpRequest;
import com.zhukai.framework.fast.rest.util.JsonUtil;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = Logger.getLogger(HttpServer.class);
    private static final ExecutorService service = Executors.newCachedThreadPool();
    private static Selector selector;

    public static void start(ServerConfig config) {
        try {
            selector = Selector.open();
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(config.getPort()));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Http server start on port: " + config.getPort() + " with nio");
            while (true) {
                if (selector.selectNow() == 0) continue;
                Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
                while (ite.hasNext()) {
                    SelectionKey key = ite.next();
                    ite.remove();
                    if (key.isAcceptable()) {
                        acceptKey(key);
                    } else if (key.isReadable()) {
                        readKey(key);
                    } else if (key.isWritable()) {
                        writeKey(key);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void acceptKey(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private static void readKey(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        HttpRequest request = HttpParser.createRequest(channel);
        if (request != null) {
            service.execute(new ActionHandleNIO(request, key));
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        } else {
            channel.shutdownInput();
            channel.close();
        }
    }

    private static void writeKey(SelectionKey key) throws Exception {
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) key.channel();
            HttpResponse response = (HttpResponse) key.attachment();
            String httpHeader = HttpParser.parseHttpString(response);
            ByteBuffer buffer = ByteBuffer.allocate(IntegrationConstants.BUFFER_SIZE);
            sendMessage(socketChannel, httpHeader, buffer);
            if (response.getResult() instanceof InputStream) {
                sendInputStream(socketChannel, (InputStream) response.getResult());
            } else {
                String json = JsonUtil.toJson(response.getResult());
                sendMessage(socketChannel, json, buffer);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (socketChannel != null) {
                socketChannel.shutdownInput();
                socketChannel.close();
            }
        }
    }

    private static void sendMessage(SocketChannel socketChannel, String message, ByteBuffer buffer) throws Exception {
        int endIndex = 0;
        while (endIndex < message.length()) {
            buffer.clear();
            int startIndex = endIndex;
            endIndex = Math.min(endIndex + IntegrationConstants.BUFFER_SIZE / 3, message.length());
            buffer.put(message.substring(startIndex, endIndex).getBytes());
            buffer.flip();
            socketChannel.write(buffer);
        }
    }

    private static void sendInputStream(SocketChannel socketChannel, InputStream in) throws Exception {
        int inputSize = in.available();
        if (inputSize < IntegrationConstants.BUFFER_SIZE) {
            byte[] bytes = new byte[inputSize];
            in.read(bytes);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            socketChannel.write(byteBuffer);
        } else {
            int length;
            byte tempByte[] = new byte[IntegrationConstants.BUFFER_SIZE * 1024];
            while ((length = in.read(tempByte)) != -1) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                bout.write(tempByte, 0, length);
                byte[] b = bout.toByteArray();
                ByteBuffer byteBuffer = ByteBuffer.allocate(b.length);
                byteBuffer.put(b);
                byteBuffer.flip();
                while (byteBuffer.hasRemaining() && socketChannel.isOpen()) {
                    socketChannel.write(byteBuffer);
                }
            }
        }
        in.close();
    }

}
