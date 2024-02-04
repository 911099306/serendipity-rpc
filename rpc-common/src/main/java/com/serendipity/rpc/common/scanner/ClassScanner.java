package com.serendipity.rpc.common.scanner;

import java.io.File;
import java.io.FileFilter;
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
 * 类的扫描器
 *
 * @author serendipity
 * @version 1.0
 * @date 2024/2/4
 **/
public class ClassScanner {

    /**
     * 文件：扫描当前工程指定包下所有类信息
     */
    private static final String PROTOCOL_FILE = "file";


    /**
     * jar包：缩表Jar文件中指定包下的所有类信息
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * class 文件后缀：扫描的过程中执行需要处理的文件的后缀信息
     */
    private static final String CLASS_FILE_SUFFIX = ".class";


    /**
     * 扫描当前工程中，指定包下的所有类信息
     *
     * @param packageName   扫描的包名
     * @param packagePath   包的完整路径
     * @param recursive     是否递归调用
     * @param classNameList 类名称集合
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<String> classNameList) {
        // 获取该包的目录，并创建一个File
        File dir = new File(packagePath);

        // 检测当前路径是否为有效路径
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 获取包下的所有 文件 及 目录
        File[] dirFiles = dir.listFiles(new FileFilter() {
            /**
             * 自定义过滤规则， 若是可循环的则可以包含子目录，或者是以.class结尾的文件
             * @param file  The abstract pathname to be tested
             * @return
             */
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        // 循环所有文件
        assert dirFiles != null;
        for (File file : dirFiles) {
            // 如果是目录，则递归扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive, classNameList);
            } else {
                // 否则，则去掉最后的 .class 只保留类目
                String className = file.getName().substring(0, file.getName().length() - 6);
                classNameList.add(packageName + '.' + className);
            }

        }
    }


    /**
     * 扫描 jar 文件中指定包下的所有类信息
     *
     * @param packageName    扫描的包名
     * @param classNameList  扫描的类名存放的 List 集合
     * @param recursive      是否递归调用
     * @param packageDirName 当前包的前面部分的名称
     * @param url            包的 url 地址
     * @return 处理后的包名，一共下次调用使用
     * @throws IOException
     */
    private static String findAndAddClassesInPackageByJar(String packageName, List<String> classNameList, boolean recursive, String packageDirName, URL url) throws IOException {

        // 如果是一个jar包文件 ,定义一个 JarFile
        JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
        // 从改 jar 包中，得到一个枚举类 ？
        Enumeration<JarEntry> entries = jarFile.entries();
        // 进行循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar包里的一个实体，可以是目录，也可以是一些jar包里的其他文件，如META-INF文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            // 如果是以 ”/“ 开头
            if (name.charAt(0) == '/') {
                // 获取后面的字符串
                name = name.substring(1);
            }

            // 判断前半部分是否和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                // 如果以 ”/“ 结尾， 说明这是一个包
                if (idx != -1) {
                    // 获取包名，把 ”/“ 替换成 ”."
                    packageName = name.substring(0, idx).replace('/', '.');
                }
                // 如果可以迭代下去，并且是一个包
                if ((idx != -1) || recursive) {
                    // 如果是一个 .class 文件，并且不是目录
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()) {
                        // 去掉最后的.class 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNameList.add(packageName + "." + className);

                    }
                }
            }
        }
        return packageName;
    }

    /**
     * 扫描并获取包下的所有类信息
     *
     * @param packageName 指定的包名
     * @return 指定报下所有类名的 List 集合
     * @throws IOException
     */
    public static List<String> getClassNameList(String packageName) throws IOException {

        // 第一个 class 类的集合
        ArrayList<String> classNameList = new ArrayList<>();
        // 是否得带循环
        boolean recursive = true;
        // 扫描包的名字，并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举集合，并进行循环处理这个目录下的文件
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

        // 顺序迭代下去进行处理
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            // 得到协议的名称
            String protocol = url.getProtocol();
            // 如果协议是以文件的形式保存在服务器
            if (PROTOCOL_FILE.equals(protocol)) {
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件，并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);

            } else if (PROTOCOL_JAR.equals(protocol)) {
                // 是以jar包的形式保存。
                packageName = findAndAddClassesInPackageByJar(packageName, classNameList, recursive, packageDirName, url);
            }

        }
        return classNameList;
    }

}
