package com.zhukai.framework.fast.rest.handle;


import com.zhukai.framework.fast.rest.http.request.HttpRequest;
import com.zhukai.framework.fast.rest.server.WebServerNIO;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

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
