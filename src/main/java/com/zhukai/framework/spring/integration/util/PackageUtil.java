package com.zhukai.framework.spring.integration.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by zhukai on 17-1-12.
 */
public class PackageUtil {
    /**
     * @param runClass
     * @return runClass同包或子级包的所有类
     * @throws Exception
     */
    public static List<Class> getAllClassesByMainClass(Class runClass) throws Exception {
        List<Class> classes = new ArrayList();
        String packageName = runClass.getPackage().getName();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = runClass.getClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "utf-8");
                findClassInPackageByFile(packageName, filePath, classes);
            } else if ("jar".equals(protocol)) {
                findClassInPackageByJar(packageDirName, packageName, url, classes);
            }
        }
        return classes;
    }

    private static void findClassInPackageByFile(String packageName, String filePath, List<Class> classes) throws Exception {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(file -> {
            boolean acceptDir = file.isDirectory();
            boolean acceptClass = file.getName().endsWith("class");
            return acceptDir || acceptClass;
        });
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
            }
        }
    }

    private static void findClassInPackageByJar(String packageDirName, String packageName, URL url, List<Class> classes) throws IOException, ClassNotFoundException {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageName = name.substring(0, idx).replace('/', '.');
                } else if (name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    classes.add(Class.forName(packageName + '.' + className));
                }
            }
        }
    }

    private PackageUtil() {
    }
}
