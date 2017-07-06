package com.zhukai.framework.spring.integration;

import com.zhukai.framework.spring.integration.annotation.batch.Batcher;
import com.zhukai.framework.spring.integration.annotation.batch.Scheduled;
import com.zhukai.framework.spring.integration.annotation.core.*;
import com.zhukai.framework.spring.integration.annotation.jpa.Entity;
import com.zhukai.framework.spring.integration.annotation.jpa.Index;
import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;
import com.zhukai.framework.spring.integration.bean.BaseBean;
import com.zhukai.framework.spring.integration.bean.ChildBean;
import com.zhukai.framework.spring.integration.bean.component.ComponentBean;
import com.zhukai.framework.spring.integration.bean.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.bean.configure.ConfigureBean;
import com.zhukai.framework.spring.integration.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.spring.integration.bean.properties.PropertiesBeanFactory;
import com.zhukai.framework.spring.integration.config.DataSource;
import com.zhukai.framework.spring.integration.config.ServerConfig;
import com.zhukai.framework.spring.integration.constant.IntegrationConstants;
import com.zhukai.framework.spring.integration.jdbc.DBConnectionPool;
import com.zhukai.framework.spring.integration.jdbc.data.jpa.JpaUtil;
import com.zhukai.framework.spring.integration.util.PackageUtil;
import com.zhukai.framework.spring.integration.util.ReflectUtil;
import com.zhukai.framework.spring.integration.util.StringUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 17-2-14.
 */
public class Setup {

    private static List<Method> batchMethods = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(Setup.class);
    private static final Pattern webMethodPattern = Pattern.compile("\\{.*?}");

    static void init() {
        initProperties();
        initConfig();
        DataSource dataSource = ConfigureBeanFactory.getInstance().getBean(DataSource.class);
        if (dataSource != null) {
            DBConnectionPool.getInstance().init(dataSource);
        }
        try {
            scanComponent();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void initProperties() {
        List<String> propertiesList = new ArrayList<>();
        if (SpringIntegration.getRunClass().isAnnotationPresent(Configurable.class)) {
            Configurable configurable = (Configurable) SpringIntegration.getRunClass().getAnnotation(Configurable.class);
            String[] propertiesArr = configurable.value();
            for (String properties : propertiesArr) {
                propertiesList.add(properties);
            }
        }
        if (!propertiesList.contains(IntegrationConstants.DEFAULT_PROPERTIES)) {
            propertiesList.add(IntegrationConstants.DEFAULT_PROPERTIES);
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
        Connection conn = DBConnectionPool.getInstance().getConnection();
        List<Class> classes = PackageUtil.getClassesFromPackage(SpringIntegration.getRunClass().getPackage().getName());
        for (Class componentClass : classes) {
            if (componentClass.isAnnotation()) {
                continue;
            } else if (componentClass.isAnnotationPresent(RestController.class)) {
                addWebMethod(componentClass);
            } else if (componentClass.isAnnotationPresent(Entity.class)) {
                checkDatabase(componentClass, conn);
            } else if (componentClass.isAnnotationPresent(Batcher.class)) {
                for (Method method : componentClass.getMethods()) {
                    if (method.isAnnotationPresent(Scheduled.class)) {
                        batchMethods.add(method);
                    }
                }
            }
            String registerName = ReflectUtil.getBeanRegisterName(componentClass);
            if (registerName != null) {
                registerComponentBean(componentClass, registerName);
            } else if (componentClass.isAnnotationPresent(Configure.class)) {
                registerConfigureBean(componentClass);
            }
        }
        DBConnectionPool.getInstance().freeConnection(conn);
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
                logger.info("Useful web method: " + webPath + methodPath);
                Matcher matcher = webMethodPattern.matcher(methodPath);
                if (matcher.find()) {
                    methodPath = methodPath.replaceAll("\\{[^}]*}", "([^/]+)");
                }
                WebContext.getWebMethods().put(webPath + methodPath, method);
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
                    beanName = childBeanName.equals("") ? IntegrationConstants.DEFAULT_PROPERTIES : childBeanName;
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
            logger.info("Table '" + tableName + "' is exists");
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
        logger.info(sql);
        conn.createStatement().executeUpdate(sql.toString());
        addDBIndex(entityClass, tableName, conn);
    }

    private static void addDBIndex(Class entityClass, String tableName, Connection conn) throws SQLException {
        Entity entityAnnotation = (Entity) entityClass.getAnnotation(Entity.class);
        Index[] indexes = entityAnnotation.indexes();
        for (Index index : indexes) {
            StringBuilder indexName = new StringBuilder(index.name());
            if (StringUtil.isBlank(indexName)) {
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
            logger.info(indexSql);
            conn.createStatement().executeUpdate(indexSql.toString());
        }
    }

    static List<Method> getBatchMethods() {
        return batchMethods;
    }
}
