package com.zhukai.framework.fast.rest.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class LogFactory {

    public static Log getLog(Class<?> clazz) {
        return JavaUtilDelegate.createLog(clazz.getName());
    }

    private static class JavaUtilDelegate {
        private static Log createLog(String name) {
            return new JavaUtilLog(name);
        }
    }

    private static class JavaUtilLog implements Log {

        private String name;

        private transient Logger logger;

        private JavaUtilLog(String name) {
            this.name = name;
            this.logger = Logger.getLogger(name);
        }

        @Override
        public void error(String message) {
            log(Level.SEVERE, message, null);
        }

        @Override
        public void error(String message, Throwable exception, Object... params) {
            log(Level.SEVERE, convert(message, params), exception);
        }

        @Override
        public void warn(String message, Object... params) {
            log(Level.WARNING, convert(message, params), null);
        }

        @Override
        public void info(String message, Object... params) {
            log(Level.INFO, convert(message, params), null);
        }

        private String convert(String message, Object... params) {
            for (Object param : params) {
                message = message.replaceFirst("\\{}", param == null ? "null" : String.valueOf(param));
            }
            return message;
        }

        private void log(Level level, String message, Throwable exception) {
            LogRecord rec = new LocationResolvingLogRecord(level, message);
            rec.setLoggerName(this.name);
            rec.setResourceBundleName(logger.getResourceBundleName());
            rec.setResourceBundle(logger.getResourceBundle());
            rec.setThrown(exception);
            logger.log(rec);
        }

    }

    private static class LocationResolvingLogRecord extends LogRecord {

        private static final String FQCN = JavaUtilLog.class.getName();

        private volatile boolean resolved;

        private LocationResolvingLogRecord(Level level, String msg) {
            super(level, msg);
        }

        public String getSourceClassName() {
            if (!this.resolved) {
                resolve();
            }
            return super.getSourceClassName();
        }

        public void setSourceClassName(String sourceClassName) {
            super.setSourceClassName(sourceClassName);
            this.resolved = true;
        }

        public String getSourceMethodName() {
            if (!this.resolved) {
                resolve();
            }
            return super.getSourceMethodName();
        }

        public void setSourceMethodName(String sourceMethodName) {
            super.setSourceMethodName(sourceMethodName);
            this.resolved = true;
        }

        private void resolve() {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            String sourceClassName = null;
            String sourceMethodName = null;
            boolean found = false;
            for (StackTraceElement element : stack) {
                String className = element.getClassName();
                if (FQCN.equals(className)) {
                    found = true;
                } else if (found) {
                    sourceClassName = className;
                    sourceMethodName = element.getMethodName();
                    break;
                }
            }
            setSourceClassName(sourceClassName);
            setSourceMethodName(sourceMethodName);
        }
    }

}
