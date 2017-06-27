package com.zhukai.framework.spring.integration.handle;


import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.server.WebServerNIO;

import java.nio.channels.ClosedChannelException;
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
        if (response == null) {
            return;
        }
        try {
            socketChannel.register(WebServerNIO.getSelector(), SelectionKey.OP_WRITE, response);
        } catch (ClosedChannelException e) {
            logger.error(e);
        }
    }
}
