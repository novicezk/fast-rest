package com.zhukai.framework.fast.rest.proxy;

import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InterceptorPoint {
	private Method around;
	private MethodSignature signature;
	private InterceptorPoint next;

	InterceptorPoint(MethodSignature signature, Method around) {
		this.signature = signature;
		this.around = around;
	}

	public MethodSignature getSignature() {
		return signature;
	}

	Method getAround() {
		return around;
	}

	InterceptorPoint setNext(InterceptorPoint next) {
		this.next = next;
		return next;
	}

	public Object proceed() throws Throwable {
		if (next != null) {
			try {
				return next.around.invoke(ComponentBeanFactory.getInstance().getBean(next.around.getDeclaringClass()), next);
			} catch (InvocationTargetException ite) {
				throw ite.getTargetException();
			}
		}
		return signature.proxy.invokeSuper(signature.object, signature.args);
	}

	public static class MethodSignature {
		private Method method;
		private Object object;
		private Object[] args;
		private MethodProxy proxy;

		MethodSignature(Method method, Object object, Object[] args, MethodProxy proxy) {
			this.method = method;
			this.object = object;
			this.args = args;
			this.proxy = proxy;
		}

		public Method getMethod() {
			return method;
		}

		public Object getObject() {
			return object;
		}

		public Object[] getArgs() {
			return args;
		}
	}
}

