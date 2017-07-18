package com.zhukai.framework.fast.rest.handle;


import com.zhukai.framework.fast.rest.constant.IntegrationConstants;
import com.zhukai.framework.fast.rest.http.HttpParser;
import com.zhukai.framework.fast.rest.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ActionHandle extends AbstractActionHandle {

    private Socket socket;

    public ActionHandle(Socket socket) {
        this.socket = socket;
        this.request = HttpParser.createRequest(socket);
    }

    @Override
    protected void respond() {
        PrintStream out = null;
        try {
            if (response == null) {
                return;
            }
            out = new PrintStream(socket.getOutputStream());
            String httpHeader = HttpParser.parseHttpString(response);
            out.print(httpHeader);
            if (response.getResult() instanceof InputStream) {
                InputStream inputStream = (InputStream) response.getResult();
                int byteCount;
                byte[] bytes = new byte[IntegrationConstants.BUFFER_SIZE * 1024];
                while ((byteCount = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, byteCount);
                }
                inputStream.close();
            } else {
                out.print(JsonUtil.toJson(response.getResult()));
            }
            out.flush();
        } catch (Exception e) {
            logger.error("Respond error", e);
        } finally {
            if (out != null)
                out.close();
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Socket close error", e);
            }
        }
    }

}
