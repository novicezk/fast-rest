package com.zhukai.spring.integration.server;


import com.zhukai.spring.integration.Application;
import com.zhukai.spring.integration.beans.impl.ComponentBean;
import com.zhukai.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.spring.integration.client.ClientAction;
import com.zhukai.spring.integration.commons.annotation.*;
import com.zhukai.spring.integration.commons.utils.JpaUtil;
import com.zhukai.spring.integration.commons.utils.ReflectUtil;
import com.zhukai.spring.integration.jdbc.DBConnectionPool;
import com.zhukai.spring.integration.jdbc.DataSource;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.commons.utils.ResourcesUtil;
import com.zhukai.spring.integration.logger.Logger;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 17-1-12.
 */
public class SpringIntegration {

    private static ServerSocket serverSocket;

    private static Integer port = 9000;

    private static boolean useDB = false;

    public static void run() {
        try {
            //初始化配置文件
            initConfig();
            //开启扫描
            scanComponent();
            //启动服务
            startServer();
        } catch (Exception e) {
            Logger.error();
            e.printStackTrace();
        }
    }

    private static void startServer() throws Exception {
        serverSocket = new ServerSocket(port);
        Logger.info("Application is start on " + port);
        ExecutorService service = Executors.newCachedThreadPool();
        while (true) {
            Socket client = serverSocket.accept();
            service.execute(new ClientAction(client));
        }
    }

    private static void initConfig() throws Exception {
        Map<String, Map> result = null;
        try {
            Yaml yaml = new Yaml();
            result = (Map<String, Map>) yaml.load(Application.class.getResourceAsStream("/application.yml"));
        } catch (Exception e) {
            //do nothing 没有该文件
        }
        if (result != null) {
            if (result.get("server") != null) {
                if (result.get("server").get("port") != null) {
                    port = Integer.parseInt(result.get("server").get("port").toString());
                }
            }
            if (result.get("datasource") != null) {
                useDB = true;
                DataSource dataSource = new DataSource();
                for (Object sourceProperty : result.get("datasource").keySet()) {
                    ReflectUtil.setFieldValue(dataSource, sourceProperty.toString(), result.get("datasource").get(sourceProperty));
                }
                Logger.info(dataSource);
                DBConnectionPool.init(dataSource);
            }
        }
    }

    private static void scanComponent() throws Exception {
        Map<String, Method> webMethods = new HashMap<>();
        Connection conn = null;
        if (useDB) {
            conn = DBConnectionPool.getConnection();
        }
        List<Class> classes = ResourcesUtil.getClassesFromPackage(Application.class.getPackage().getName());
        for (Class componentClass : classes) {

            if (componentClass.isAnnotationPresent(RestController.class)) {
                addWebMethod(webMethods, componentClass);
            }
            if (componentClass.isAnnotationPresent(Entity.class) && useDB) {
                checkDatabase(componentClass, conn);
            }
            String registerName = ReflectUtil.getBeanRegisterName(componentClass);
            if (registerName != null) {
                registerBean(componentClass, registerName);
            }
        }
        Logger.info("Useful web methods: " + webMethods.keySet());
        WebContext.setWebMethods(webMethods);
        if (useDB) {
            DBConnectionPool.freeConnection(conn);
        }
    }

    private static void checkDatabase(Class entityClass, Connection conn) throws Exception {
        String tableName = JpaUtil.getTableName(entityClass);

        ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null);
        if (rs.next()) {
            Logger.info("Table \'" + tableName + "\' is exists");
            return;
        }
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
        sql.append(");");
        Logger.info(sql);
        conn.prepareStatement(sql.toString()).execute();
    }

    private static final Pattern methodPattern = Pattern.compile("\\{*\\}");

    private static void addWebMethod(Map<String, Method> webMethods, Class webClass) {
        String webPath = "";
        if (webClass.isAnnotationPresent(RequestMapping.class)) {
            webPath = ((RequestMapping) webClass.getAnnotation(RequestMapping.class)).value();
        }
        Method[] methods = webClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                String methodPath = method.getAnnotation(RequestMapping.class).value();
                Matcher matcher = methodPattern.matcher(methodPath);
                if (matcher.find()) {
                    methodPath = methodPath.replaceAll("\\{[^}]*\\}", "*");
                    webMethods.put(webPath + methodPath, method);
                } else {
                    webMethods.put(webPath + methodPath, method);
                }
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
        Logger.info("Register in componentBeanFactory: " + registerName + " = " + beanClass.getSimpleName() + ".class");
    }

}
