package com.zhukai.framework.spring.integration.handle;


import com.zhukai.framework.spring.integration.http.HttpParser;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;


/**
 * Created by zhukai on 17-1-12.
 */
public class ActionHandle extends AbstractActionHandle {

    private Socket socket;

    public ActionHandle(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    @Override
    protected void respond() {
        PrintStream out = null;
        try {
            if (response == null) {
                return;
            }
            out = new PrintStream(socket.getOutputStream(), true);
            String httpHeader = HttpParser.parseHttpString(response);
            out.println(httpHeader);
            if (response.getResult() instanceof InputStream) {
                InputStream inputStream = (InputStream) response.getResult();
                int byteCount;
                byte[] bytes = new byte[1024 * 1024];
                while ((byteCount = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, byteCount);
                }
                inputStream.close();
            } else {
                out.println(JsonUtil.toJson(response.getResult()));
            }
        } catch (Exception e) {
            logger.error("Respond error", e);
        } finally {
            if (out != null)
                out.close();
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("socket close error", e);
            }
        }
    }

}
