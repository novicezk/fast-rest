package com.zhukai.framework.spring.integration.server;

import com.zhukai.framework.spring.integration.annotation.batch.Batcher;
import com.zhukai.framework.spring.integration.annotation.batch.Scheduled;
import com.zhukai.framework.spring.integration.annotation.core.Autowired;
import com.zhukai.framework.spring.integration.annotation.core.Singleton;
import com.zhukai.framework.spring.integration.annotation.jpa.Entity;
import com.zhukai.framework.spring.integration.annotation.jpa.Index;
import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;
import com.zhukai.framework.spring.integration.beans.impl.ComponentBean;
import com.zhukai.framework.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.context.WebContext;
import com.zhukai.framework.spring.integration.jdbc.DBConnectionPool;
import com.zhukai.framework.spring.integration.jdbc.DataSource;
import com.zhukai.framework.spring.integration.jdbc.JpaUtil;
import com.zhukai.framework.spring.integration.utils.PackageUtil;
import com.zhukai.framework.spring.integration.utils.ReflectUtil;
import com.zhukai.framework.spring.integration.utils.StringUtil;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 17-2-14.
 */
public class SpringCore {

    private static boolean useDB = false;
    private static List<Method> batchMethods = new ArrayList<>();
    private static Logger logger = Logger.getLogger(SpringCore.class);

    protected static void init() throws Exception {
        //初始化配置文件
        initConfig();
        //开启扫描
        scanComponent();
    }

    protected static void initConfig() throws Exception {
        Map<String, Map> result = null;
        try {
            Yaml yaml = new Yaml();
            result = (Map<String, Map>) yaml.load(SpringIntegration.runClass.
                    getResourceAsStream("/application.yml"));
        } catch (Exception e) {
            logger.warn("Have no application.yml");
        }
        ServerConfig config = new ServerConfig();
        SpringIntegration.setServerConfig(config);
        if (result != null) {
            if (result.get("server") != null) {
                for (Object sourceProperty : result.get("server").keySet()) {
                    ReflectUtil.setFieldValue(config, sourceProperty.toString(), result.get("server").get(sourceProperty));
                }
                logger.info(config);
            }
            if (result.get("datasource") != null) {
                useDB = true;
                DataSource dataSource = new DataSource();
                for (Object sourceProperty : result.get("datasource").keySet()) {
                    ReflectUtil.setFieldValue(dataSource, sourceProperty.toString(), result.get("datasource").get(sourceProperty));
                }
                logger.info(dataSource);
                DBConnectionPool.getInstance().init(dataSource);
            }
        }
    }

    protected static void scanComponent() throws Exception {
        Map<String, Method> webMethods = new HashMap<>();
        Connection conn = null;
        if (useDB) {
            conn = DBConnectionPool.getInstance().getConnection();
        }
        List<Class> classes = PackageUtil.getClassesFromPackage(SpringIntegration.runClass.getPackage().getName());
        for (Class componentClass : classes) {
            if (componentClass.isAnnotationPresent(RestController.class)) {
                addWebMethod(webMethods, componentClass);
            }
            if (componentClass.isAnnotationPresent(Entity.class) && useDB) {
                checkDatabase(componentClass, conn);
            }
            if (componentClass.isAnnotationPresent(Batcher.class)) {
                for (Method method : componentClass.getMethods()) {
                    if (method.isAnnotationPresent(Scheduled.class)) {
                        batchMethods.add(method);
                    }
                }
            }
            String registerName = ReflectUtil.getBeanRegisterName(componentClass);
            if (registerName != null) {
                registerBean(componentClass, registerName);
            }
        }
        WebContext.setWebMethods(webMethods);
        if (conn != null)
            DBConnectionPool.getInstance().freeConnection(conn);
    }

    private static void checkDatabase(Class entityClass, Connection conn) throws Exception {
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
        //添加索引
        Entity entityAnnotation = (Entity) entityClass.getAnnotation(Entity.class);
        if (entityAnnotation != null) {
            Index[] indexs = entityAnnotation.indexes();
            for (Index index : indexs) {
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
    }

    private static final Pattern methodPattern = Pattern.compile("\\{.*?}");

    private static void addWebMethod(Map<String, Method> webMethods, Class webClass) {
        String webPath = "";
        if (webClass.isAnnotationPresent(RequestMapping.class)) {
            webPath = ((RequestMapping) webClass.getAnnotation(RequestMapping.class)).value();
        }
        Method[] methods = webClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                String methodPath = method.getAnnotation(RequestMapping.class).value();
                logger.info("Useful web method: " + webPath + methodPath);
                Matcher matcher = methodPattern.matcher(methodPath);
                if (matcher.find()) {
                    methodPath = methodPath.replaceAll("\\{[^}]*}", "([^/]+)");
                }
                webMethods.put(webPath + methodPath, method);
            }
        }
    }

    private static void registerBean(Class beanClass, String registerName) throws ClassNotFoundException {
        registerName = registerName.equals("") ? beanClass.getName() : registerName;
        ComponentBean componentBean = new ComponentBean();
        componentBean.setBeanClassName(beanClass.getName());
        componentBean.setSingleton(ReflectUtil.existAnnotation(beanClass, Singleton.class));
        Map<String, String> childBeanNames = new HashMap<>();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String childBeanName = field.getAnnotation(Autowired.class).value();
                childBeanName = childBeanName.equals("") ? field.getType().getName() : childBeanName;
                childBeanNames.put(childBeanName, field.getName());
            }
        }
        componentBean.setChildBeanNames(childBeanNames);
        ComponentBeanFactory.getInstance().registerBeanDefinition(registerName, componentBean);
        logger.info("Register in componentBeanFactory: " + registerName + " = " + beanClass.getSimpleName() + ".class");
    }

    protected static List<Method> getBatchMethods() {
        return batchMethods;
    }
}
