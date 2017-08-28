package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.handle.ActionHandleNIO;
import com.zhukai.framework.fast.rest.http.HttpParser;
import com.zhukai.framework.fast.rest.http.HttpResponse;
import com.zhukai.framework.fast.rest.http.request.HttpRequest;
import com.zhukai.framework.fast.rest.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class HttpServer extends Server {
	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
	private final ExecutorService service = Executors.newCachedThreadPool();
	private Selector selector;

	public HttpServer(ServerConfig config) throws Exception {
		super(config);
	}

	@Override
	public String getServerName() {
		return "Http";
	}

	@Override
	protected void init(ServerConfig config) throws Exception {
		selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.socket().bind(new InetSocketAddress(config.getPort()));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (selector.selectNow() == 0)
					continue;
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
			logger.error("Http server run error", e);
			System.exit(1);
		}
	}

	private void acceptKey(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel channel = server.accept();
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
	}

	private void readKey(SelectionKey key) throws IOException {
		try {
			SocketChannel channel = (SocketChannel) key.channel();

			HttpRequest request = HttpParser.createRequest(channel);
			if (request != null) {
				service.execute(new ActionHandleNIO(request, key));
				key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
			} else {
				channel.shutdownInput();
				channel.close();
			}
		} catch (Exception e) {
			logger.error("Read Request error", e);
		}
	}

	private void writeKey(SelectionKey key) throws IOException {
		SocketChannel socketChannel = null;
		try {
			socketChannel = (SocketChannel) key.channel();
			HttpResponse response = (HttpResponse) key.attachment();
			String httpHeader = HttpParser.parseHttpString(response);
			ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
			sendMessage(socketChannel, httpHeader, buffer, response.getCharacterEncoding());
			if (response.getResult() == null) {
				return;
			}
			if (response.getResult() instanceof InputStream) {
				sendInputStream(socketChannel, (InputStream) response.getResult());
			} else {
				String json = JsonUtil.toJson(response.getResult());
				sendMessage(socketChannel, json, buffer, response.getCharacterEncoding());
			}
		} catch (Exception e) {
			logger.error("Write response error", e);
		} finally {
			if (socketChannel != null) {
				socketChannel.shutdownInput();
				socketChannel.close();
			}
		}
	}

	private void sendMessage(SocketChannel socketChannel, String message, ByteBuffer buffer, String charset) throws Exception {
		int endIndex = 0;
		while (endIndex < message.length()) {
			buffer.clear();
			int startIndex = endIndex;
			endIndex = Math.min(endIndex + Constants.BUFFER_SIZE / 3, message.length());
			buffer.put(message.substring(startIndex, endIndex).getBytes(charset));
			buffer.flip();
			socketChannel.write(buffer);
		}
	}

	private void sendInputStream(SocketChannel socketChannel, InputStream in) throws Exception {
		int inputSize = in.available();
		if (inputSize < Constants.BUFFER_SIZE) {
			byte[] bytes = new byte[inputSize];
			in.read(bytes);
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			socketChannel.write(byteBuffer);
		} else {
			int length;
			byte tempByte[] = new byte[Constants.BUFFER_SIZE * 1024];
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
