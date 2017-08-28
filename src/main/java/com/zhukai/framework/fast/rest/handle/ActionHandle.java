package com.zhukai.framework.fast.rest.handle;

import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.http.HttpParser;
import com.zhukai.framework.fast.rest.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ActionHandle extends AbstractActionHandle {

	private static final Logger logger = LoggerFactory.getLogger(ActionHandle.class);
	private Socket socket;

	public ActionHandle(Socket socket) throws IOException {
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
			out = new PrintStream(socket.getOutputStream(), true, response.getCharacterEncoding());
			String httpHeader = HttpParser.parseHttpString(response);
			out.print(httpHeader);
			if (response.getResult() == null) {
				return;
			}
			if (response.getResult() instanceof InputStream) {
				InputStream inputStream = (InputStream) response.getResult();
				int byteCount;
				byte[] bytes = new byte[Constants.BUFFER_SIZE * 1024];
				while ((byteCount = inputStream.read(bytes)) != -1) {
					out.write(bytes, 0, byteCount);
				}
				inputStream.close();
			} else {
				out.print(JsonUtil.toJson(response.getResult()));
			}
		} catch (Exception e) {
			logger.error("Respond error", e);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(socket);
		}
	}

}
