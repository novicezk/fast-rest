package com.zhukai.framework.fast.rest.handle;

import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.FastRestApplication;
import com.zhukai.framework.fast.rest.Setup;
import com.zhukai.framework.fast.rest.annotation.web.*;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import com.zhukai.framework.fast.rest.common.FileEntity;
import com.zhukai.framework.fast.rest.common.HttpHeaderType;
import com.zhukai.framework.fast.rest.common.HttpStatus;
import com.zhukai.framework.fast.rest.common.MultipartFile;
import com.zhukai.framework.fast.rest.exception.RequestNotAllowException;
import com.zhukai.framework.fast.rest.http.HttpContext;
import com.zhukai.framework.fast.rest.http.HttpParser;
import com.zhukai.framework.fast.rest.http.HttpResponse;
import com.zhukai.framework.fast.rest.http.request.HttpRequest;
import com.zhukai.framework.fast.rest.util.JsonUtil;
import com.zhukai.framework.fast.rest.util.Resources;
import com.zhukai.framework.fast.rest.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractActionHandle implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(AbstractActionHandle.class);

	protected HttpRequest request;
	protected HttpResponse response;

	private boolean pathVariable = false;

	protected abstract void respond();

	@Override
	public void run() {
		if (request == null) {
			return;
		}
		response = new HttpResponse();
		response.setProtocol(request.getProtocol());
		checkSession();
		try {
			Object returnData;
			if (request.getServletPath().equals("/")) {
				returnData = getProjectResourceWithHandleContentType("/public/" + FastRestApplication.getServerConfig().getIndexPage(), true);
			} else if (request.getServletPath().equals("/favicon.ico")) {
				returnData = getProjectResourceWithHandleContentType(request.getServletPath(), true);
			} else if (StringUtils.isNoneBlank(FastRestApplication.getStaticPath()) && request.getServletPath().startsWith("/static/")) {
				returnData = Resources.getResourceAsStreamByStatic(request.getServletPath().substring(7));
				handleContentType(request.getServletPath());
			} else {
				Method method = getMethodByRequestPath(request.getServletPath());
				if (method != null) {
					try {
						returnData = getInvokeResult(method);
					} catch (InvocationTargetException ite) {
						throw ite.getTargetException();
					}
				} else {
					returnData = getProjectResourceWithHandleContentType("/public" + request.getServletPath(), false);
				}
			}
			response.setResult(returnData);
		} catch (FileNotFoundException e) {
			response.setStatus(HttpStatus.NotFound);
		} catch (RequestNotAllowException e) {
			response.setStatus(HttpStatus.MethodNotAllowed);
		} catch (Throwable e) {
			logger.error("Request action error", e);
			response.setStatus(HttpStatus.InternalServerError);
		} finally {
			respond();
		}
	}

	private Object getProjectResourceWithHandleContentType(String path, boolean findDefault) throws FileNotFoundException {
		InputStream inputStream = Resources.getResourceAsStreamByProject(path);
		if (findDefault && inputStream == null) {
			inputStream = FastRestApplication.class.getResourceAsStream(path);
		}
		if (inputStream == null) {
			throw new FileNotFoundException();
		}
		handleContentType(path);
		return inputStream;
	}

	private void handleContentType(String path) {
		String[] arr = path.split("\\.");
		if (arr.length > 0) {
			String extensionName = arr[arr.length - 1];
			String contentType = HttpParser.getContentType(extensionName);
			response.setContentType(contentType + "; charset=" + FastRestApplication.getServerConfig().getCharset());
		}
	}

	private Object getInvokeResult(Method method) throws Throwable {
		pathVariable = method.getAnnotation(RequestMapping.class).value().contains("{");
		Object result = invokeRequestMethod(method);
		if (result instanceof FileEntity) {
			FileEntity fileBean = (FileEntity) result;
			String fileName = fileBean.getFileName();
			response.setHeader(HttpHeaderType.CONTENT_DISPOSITION, "filename=" + fileName);
			response.setContentType("application/octet-stream");
			return fileBean.getInputStream();
		}
		return result;
	}

	private Method getMethodByRequestPath(String requestPath) {
		for (String key : Setup.getWebMethods().keySet()) {
			if (Pattern.matches(key, requestPath)) {
				return Setup.getWebMethods().get(key);
			}
		}
		return null;
	}

	private void checkSession() {
		if (request.getRequestedSessionId() == null) {
			String sessionId = UUID.randomUUID().toString();
			Cookie sessionCookie = new Cookie(Constants.FAST_REST_SESSION, sessionId);
			sessionCookie.setMaxAge(Long.valueOf(FastRestApplication.getServerConfig().getSessionTimeout() / 1000).intValue());
			sessionCookie.setSecure(FastRestApplication.getServerConfig().isUseSSL());
			sessionCookie.setPath("/");
			request.addCookie(sessionCookie);
			response.addCookie(sessionCookie);
		}
		HttpContext.getInstance().refreshSession(request.getRequestedSessionId());
	}

	private Object invokeRequestMethod(Method method) throws Throwable {
		if (!ArrayUtils.contains(method.getAnnotation(RequestMapping.class).method(), request.getMethod())) {
			throw new RequestNotAllowException("Request: " + request.getServletPath() + " - " + request.getMethod() + " is not allow");
		}
		HttpContext.getInstance().setRequest(request);
		HttpContext.getInstance().setResponse(response);
		Object object = ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass());
		try {
			return method.invoke(object, getMethodParametersArr(method));
		} catch (InvocationTargetException ite) {
			Throwable e = ite.getTargetException();
			for (Method exceptionMethod : Setup.getExceptionHandlerMethods()) {
				for (Class<? extends Throwable> throwableClass : exceptionMethod.getAnnotation(ExceptionHandler.class).value()) {
					if (throwableClass.isAssignableFrom(e.getClass())) {
						return exceptionMethod.invoke(ComponentBeanFactory.getInstance().getBean(exceptionMethod.getDeclaringClass()), e);
					}
				}
			}
			throw e;
		}

	}

	private Object[] getMethodParametersArr(Method method) throws Exception {
		List<Object> paramValues = new ArrayList<>(5);
		Parameter[] parameters = method.getParameters();
		List<String> pathKeys = null;
		List<String> pathValues = null;
		if (pathVariable && method.isAnnotationPresent(RequestMapping.class)) {
			String requestMapperValue = method.getAnnotation(RequestMapping.class).value();
			pathKeys = getPathKeys(requestMapperValue);
			pathValues = getPathValues(requestMapperValue);
		}
		for (Parameter parameter : parameters) {
			paramValues.add(getParameterInstance(parameter, pathKeys, pathValues));
		}
		return paramValues.toArray();
	}

	private Object getParameterInstance(Parameter parameter, List<String> pathKeys, List<String> pathValues) throws Exception {
		if (HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
			return request;
		}
		if (HttpSession.class.isAssignableFrom(parameter.getType())) {
			return request.getSession();
		}
		if (HttpResponse.class.isAssignableFrom(parameter.getType())) {
			return response;
		}
		if (MultipartFile.class.isAssignableFrom(parameter.getType())) {
			String fileName = parameter.getAnnotation(RequestParam.class).value();
			return request.getMultipartFile(fileName);
		}
		if (MultipartFile[].class.isAssignableFrom(parameter.getType())) {
			return request.getAllMultipartFile();
		}
		Annotation[] annotations = parameter.getAnnotations();
		if (annotations.length == 0) {
			return null;
		}
		Annotation parameterAnnotation = parameter.getAnnotations()[0];
		Object parameterValue = null;
		if (parameterAnnotation instanceof RequestParam) {
			parameterValue = request.getParameter(((RequestParam) parameterAnnotation).value());
		} else if (parameterAnnotation instanceof RequestHeader) {
			parameterValue = request.getHeader(((RequestHeader) parameterAnnotation).value());
		} else if (parameterAnnotation instanceof RequestAttribute) {
			Object attributeValue = request.getAttribute(((RequestAttribute) parameterAnnotation).value());
			if (attributeValue != null)
				parameterValue = attributeValue.toString();
		} else if (parameterAnnotation instanceof RequestBody) {
			parameterValue = JsonUtil.convertObj(request.getRequestContext(), parameter.getType());
		} else if (parameterAnnotation instanceof PathVariable && pathVariable) {
			String value = ((PathVariable) parameterAnnotation).value();
			parameterValue = pathValues.get(pathKeys.indexOf(value));
		} else {
			return null;
		}
		return TypeUtil.convert(parameterValue, parameter.getType());
	}

	private List<String> getPathKeys(String requestMapperValue) {
		List<String> pathKeys = new ArrayList<>();
		Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(requestMapperValue);
		while (matcher.find()) {
			pathKeys.add(matcher.group(1));
		}
		return pathKeys;
	}

	private List<String> getPathValues(String requestMapperValue) {
		List<String> pathValues = new ArrayList<>();
		String regular = requestMapperValue.replaceAll("\\{[^}]*}", "([^/]+)");
		Pattern pattern = Pattern.compile(regular);
		Matcher restMatcher = pattern.matcher(request.getServletPath());
		if (restMatcher.find()) {
			for (int i = 1; i <= restMatcher.groupCount(); i++) {
				pathValues.add(restMatcher.group(i));
			}
		}
		return pathValues;
	}
}
