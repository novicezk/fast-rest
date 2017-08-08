package com.zhukai.framework.fast.rest;

import com.zhukai.framework.fast.rest.annotation.batch.Batcher;
import com.zhukai.framework.fast.rest.annotation.batch.Scheduled;
import com.zhukai.framework.fast.rest.annotation.core.*;
import com.zhukai.framework.fast.rest.annotation.jpa.Entity;
import com.zhukai.framework.fast.rest.annotation.jpa.Index;
import com.zhukai.framework.fast.rest.annotation.web.ControllerAdvice;
import com.zhukai.framework.fast.rest.annotation.web.ExceptionHandler;
import com.zhukai.framework.fast.rest.annotation.web.RequestMapping;
import com.zhukai.framework.fast.rest.annotation.web.RestController;
import com.zhukai.framework.fast.rest.bean.BaseBean;
import com.zhukai.framework.fast.rest.bean.ChildBean;
import com.zhukai.framework.fast.rest.bean.component.ComponentBean;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import com.zhukai.framework.fast.rest.bean.configure.ConfigureBean;
import com.zhukai.framework.fast.rest.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.fast.rest.bean.properties.PropertiesBeanFactory;
import com.zhukai.framework.fast.rest.config.DataSource;
import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.exception.SetupInitException;
import com.zhukai.framework.fast.rest.jdbc.DBConnectionPool;
import com.zhukai.framework.fast.rest.jdbc.data.jpa.JpaUtil;
import com.zhukai.framework.fast.rest.util.PackageUtil;
import com.zhukai.framework.fast.rest.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Setup {

    private static List<Method> batchMethods = new ArrayList<>();
    private static Map<String, Method> webMethods = new HashMap<>();
    private static List<Method> exceptionHandlerMethods = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(Setup.class);
    private static final Pattern webMethodPattern = Pattern.compile("\\{.*?}");
    private static DataSource dataSource;

    static void init() throws SetupInitException {
        try {
            initProperties();
            initConfig();
            dataSource = ConfigureBeanFactory.getInstance().getBean(DataSource.class);
            if (dataSource != null) {
                DBConnectionPool.init(dataSource);
            }
            scanComponent();
        } catch (Exception e) {
            throw new SetupInitException(e);
        }
    }

    private static void initProperties() {
        List<String> propertiesList = new ArrayList<>();
        if (FastRestApplication.getRunClass().isAnnotationPresent(EnableConfigure.class)) {
            EnableConfigure configurable = (EnableConfigure) FastRestApplication.getRunClass().getAnnotation(EnableConfigure.class);
            String[] propertiesArr = configurable.value();
            propertiesList.addAll(Arrays.asList(propertiesArr));
        }
        if (!propertiesList.contains(Constants.DEFAULT_PROPERTIES)) {
            propertiesList.add(Constants.DEFAULT_PROPERTIES);
        }
        for (String propertiesName : propertiesList) {
            BaseBean baseBean = new BaseBean();
            baseBean.setRegisterName(propertiesName);
            PropertiesBeanFactory.getInstance().registerBean(baseBean);
        }
    }

    private static void initConfig() {
        registerConfigureBean(ServerConfig.class);
        registerConfigureBean(DataSource.class);
    }

    private static void scanComponent() throws Exception {
        Connection conn = dataSource != null ? DBConnectionPool.getConnection() : null;
        List<Class> classes = PackageUtil.getAllClassesByMainClass(FastRestApplication.getRunClass());
        for (Class componentClass : classes) {
            if (componentClass.isAnnotation()) {
                continue;
            } else if (componentClass.isAnnotationPresent(RestController.class)) {
                addWebMethod(componentClass);
            } else if (componentClass.isAnnotationPresent(Entity.class)) {
                checkDatabase(componentClass, conn);
            } else if (componentClass.isAnnotationPresent(ControllerAdvice.class)) {
                addMethodToList(componentClass, exceptionHandlerMethods, ExceptionHandler.class);
                exceptionHandlerMethods.sort((method1, method2) -> {
                    int method1Seq = method1.getAnnotation(ExceptionHandler.class).catchSeq();
                    int method2Seq = method2.getAnnotation(ExceptionHandler.class).catchSeq();
                    if (method1Seq == method2Seq) return 0;
                    return method1Seq > method2Seq ? 1 : -1;
                });
            } else if (componentClass.isAnnotationPresent(Batcher.class)) {
                addMethodToList(componentClass, batchMethods, Scheduled.class);
            }
            String registerName = ReflectUtil.getComponentValue(componentClass);
            if (registerName != null) {
                registerComponentBean(componentClass, registerName);
            } else if (componentClass.isAnnotationPresent(Configure.class)) {
                registerConfigureBean(componentClass);
            }
        }
        if (conn != null) {
            DBConnectionPool.freeConnection(conn);
        }
    }

    private static void addMethodToList(Class componentClass, List<Method> list, Class<? extends Annotation> methodAnnotation) {
        Method[] methods = componentClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(methodAnnotation)) {
                list.add(method);
            }
        }
    }

    private static void addWebMethod(Class webClass) {
        String webPath = "";
        if (webClass.isAnnotationPresent(RequestMapping.class)) {
            webPath = ((RequestMapping) webClass.getAnnotation(RequestMapping.class)).value();
        }
        Method[] methods = webClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                String methodPath = method.getAnnotation(RequestMapping.class).value();
                logger.info("Useful web method: {}{}", webPath, methodPath);
                Matcher matcher = webMethodPattern.matcher(methodPath);
                if (matcher.find()) {
                    methodPath = methodPath.replaceAll("\\{[^}]*}", "([^/]+)");
                }
                webMethods.put(webPath + methodPath, method);
            }
        }
    }

    private static void registerComponentBean(Class beanClass, String registerName) throws ClassNotFoundException {
        registerName = registerName.equals("") ? beanClass.getName() : registerName;
        ComponentBean componentBean = new ComponentBean();
        componentBean.setBeanClass(beanClass);
        componentBean.setSingleton(ReflectUtil.existAnnotation(beanClass, Singleton.class));
        List<ChildBean> children = new ArrayList<>();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String childBeanName = field.getAnnotation(Autowired.class).value();
                String beanName = childBeanName.equals("") ? field.getType().getName() : childBeanName;
                ChildBean childBean = new ChildBean();
                childBean.setFieldName(field.getName());
                childBean.setBeanClass(field.getType());
                if (ReflectUtil.existAnnotation(field.getType(), Component.class)) {
                    childBean.setBeanFactory(ComponentBeanFactory.getInstance());
                } else if (ReflectUtil.existAnnotation(field.getType(), Configure.class)) {
                    childBean.setBeanFactory(ConfigureBeanFactory.getInstance());
                } else if (Properties.class.isAssignableFrom(field.getType())) {
                    beanName = childBeanName.equals("") ? Constants.DEFAULT_PROPERTIES : childBeanName;
                    childBean.setBeanFactory(PropertiesBeanFactory.getInstance());
                }
                childBean.setRegisterName(beanName);
                children.add(childBean);
            }
        }
        componentBean.setChildren(children);
        componentBean.setRegisterName(registerName);
        ComponentBeanFactory.getInstance().registerBean(componentBean);
    }

    private static void registerConfigureBean(Class beanClass) {
        Configure configure = (Configure) beanClass.getAnnotation(Configure.class);
        String registerName = configure.value().equals("") ? beanClass.getName() : configure.value();
        String prefix = configure.prefix();
        String propertiesName = configure.properties();
        ConfigureBean configureBean = new ConfigureBean();
        configureBean.setBeanClass(beanClass);
        configureBean.setRegisterName(registerName);
        configureBean.setPrefix(prefix);
        Properties properties = PropertiesBeanFactory.getProperties(propertiesName);
        configureBean.setProperties(properties);
        ConfigureBeanFactory.getInstance().registerBean(configureBean);
    }

    private static void checkDatabase(Class entityClass, Connection conn) throws SQLException {
        if (conn == null) {
            return;
        }
        String tableName = JpaUtil.getTableName(entityClass);
        ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
        if (rs.next()) {
            logger.info("Table '{}' is exists", tableName);
            rs.close();
            return;
        }
        rs.close();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(tableName);
        sql.append("(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAnnotationPresent(Entity.class)) {
                checkDatabase(field.getType(), conn);
            }
            sql.append(JpaUtil.convertToSqlColumn(field));
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        logger.info("Sql: {}", sql);
        conn.createStatement().executeUpdate(sql.toString());
        addDBIndex(entityClass, tableName, conn);
    }

    private static void addDBIndex(Class entityClass, String tableName, Connection conn) throws SQLException {
        Entity entityAnnotation = (Entity) entityClass.getAnnotation(Entity.class);
        Index[] indexes = entityAnnotation.indexes();
        for (Index index : indexes) {
            StringBuilder indexName = new StringBuilder(index.name());
            if (StringUtils.isBlank(indexName)) {
                indexName.append(tableName).append("_INDEX_");
                for (int i = 0; i < index.columns().length; i++) {
                    indexName.append(JpaUtil.getColumnName(entityClass, index.columns()[i]));
                    if (i != index.columns().length - 1) {
                        indexName.append("_");
                    }
                }
            }
            StringBuilder indexSql = new StringBuilder("CREATE ");
            if (index.unique()) {
                indexSql.append(" UNIQUE ");
            }
            if (index.isFull()) {
                indexSql.append(" FULLTEXT ");
            }
            indexSql.append(" INDEX ").append(indexName).append(" ON ").append(tableName)
                    .append("(");
            for (int i = 0; i < index.columns().length; i++) {
                indexSql.append(JpaUtil.getColumnName(entityClass, index.columns()[i]));
                if (i != index.columns().length - 1) {
                    indexSql.append(",");
                }
            }
            indexSql.append(")");
            logger.info("Sql: {}", indexSql);
            conn.createStatement().executeUpdate(indexSql.toString());
        }
    }

    static List<Method> getBatchMethods() {
        return batchMethods;
    }

    public static List<Method> getExceptionHandlerMethods() {
        return exceptionHandlerMethods;
    }

    public static Map<String, Method> getWebMethods() {
        return webMethods;
    }

}
