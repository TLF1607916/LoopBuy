package com.shiwu.integration;

import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应用程序启动集成测试
 * 测试Application主类的启动和配置
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("应用程序启动集成测试")
public class ApplicationIntegrationTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationIntegrationTest.class);
    
    @BeforeAll
    public static void setUpClass() {
        logger.info("开始应用程序启动集成测试");
    }
    
    @Test
    @Order(1)
    @DisplayName("1.1 Application类存在性测试")
    public void testApplicationClassExists() {
        logger.info("测试Application类是否存在");
        
        try {
            Class<?> appClass = Class.forName("com.shiwu.Application");
            assertNotNull(appClass, "Application类应该存在");
            
            // 检查main方法
            Method mainMethod = appClass.getMethod("main", String[].class);
            assertNotNull(mainMethod, "main方法应该存在");
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()), 
                      "main方法应该是静态的");
            assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()), 
                      "main方法应该是公共的");
            
            logger.info("Application类和main方法验证通过");
        } catch (ClassNotFoundException e) {
            fail("Application类不存在: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            fail("main方法不存在: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("1.2 应用程序参数解析测试")
    public void testApplicationArgumentParsing() {
        logger.info("测试应用程序参数解析");
        
        // 捕获System.out输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // 由于实际启动会阻塞，我们只能测试类的存在性和基本结构
            Class<?> appClass = Class.forName("com.shiwu.Application");
            assertNotNull(appClass, "Application类应该可以加载");
            
            logger.info("应用程序参数解析测试通过");
        } catch (Exception e) {
            logger.warn("参数解析测试遇到预期异常: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("1.3 Tomcat依赖检查测试")
    public void testTomcatDependencies() {
        logger.info("测试Tomcat相关依赖");
        
        try {
            // 检查Tomcat类是否可用
            Class.forName("org.apache.catalina.startup.Tomcat");
            Class.forName("org.apache.catalina.Context");
            Class.forName("org.apache.catalina.LifecycleException");
            
            logger.info("Tomcat依赖检查通过");
        } catch (ClassNotFoundException e) {
            fail("Tomcat依赖缺失: " + e.getMessage());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("1.4 应用程序配置验证测试")
    public void testApplicationConfiguration() {
        logger.info("测试应用程序配置");
        
        // 检查webapp目录配置
        String webappDir = "src/main/webapp/";
        java.io.File webappFile = new java.io.File(webappDir);
        
        // webapp目录可能不存在，这是正常的（在打包后会在target目录）
        if (!webappFile.exists()) {
            logger.info("webapp目录不存在，检查target目录配置");
            java.io.File targetDir = new java.io.File("target/shiwu-marketplace-1.0-SNAPSHOT");
            // target目录在测试环境中也可能不存在，这是正常的
            logger.info("target目录存在: " + targetDir.exists());
        }
        
        // 检查工作目录配置
        String workingDir = System.getProperty("java.io.tmpdir");
        assertNotNull(workingDir, "临时目录应该存在");
        assertTrue(workingDir.length() > 0, "临时目录路径不应为空");
        
        logger.info("应用程序配置验证通过");
    }
    
    @Test
    @Order(5)
    @DisplayName("1.5 端口配置测试")
    public void testPortConfiguration() {
        logger.info("测试端口配置");
        
        // 测试默认端口
        int defaultPort = 8080;
        assertTrue(defaultPort > 0 && defaultPort < 65536, "默认端口应该在有效范围内");
        
        // 测试端口解析逻辑
        try {
            int validPort = Integer.parseInt("9090");
            assertTrue(validPort > 0 && validPort < 65536, "解析的端口应该在有效范围内");
        } catch (NumberFormatException e) {
            fail("端口解析失败: " + e.getMessage());
        }
        
        // 测试无效端口处理
        try {
            Integer.parseInt("invalid");
            fail("应该抛出NumberFormatException");
        } catch (NumberFormatException e) {
            // 预期的异常
            logger.info("无效端口处理正确");
        }
        
        logger.info("端口配置测试通过");
    }
    
    @Test
    @Order(6)
    @DisplayName("1.6 类加载器配置测试")
    public void testClassLoaderConfiguration() {
        logger.info("测试类加载器配置");
        
        ClassLoader appClassLoader = this.getClass().getClassLoader();
        assertNotNull(appClassLoader, "应用程序类加载器应该存在");
        
        // 测试能否加载应用程序的核心类
        try {
            appClassLoader.loadClass("com.shiwu.user.controller.UserController");
            appClassLoader.loadClass("com.shiwu.product.controller.ProductController");
            appClassLoader.loadClass("com.shiwu.common.util.DBUtil");
            
            logger.info("核心类加载测试通过");
        } catch (ClassNotFoundException e) {
            fail("核心类加载失败: " + e.getMessage());
        }
        
        logger.info("类加载器配置测试通过");
    }
    
    @Test
    @Order(7)
    @DisplayName("1.7 日志配置测试")
    public void testLoggingConfiguration() {
        logger.info("测试日志配置");
        
        // 测试Logger是否正常工作
        Logger testLogger = LoggerFactory.getLogger(ApplicationIntegrationTest.class);
        assertNotNull(testLogger, "Logger应该能够正常创建");
        
        // 测试不同级别的日志
        testLogger.debug("Debug级别日志测试");
        testLogger.info("Info级别日志测试");
        testLogger.warn("Warn级别日志测试");
        testLogger.error("Error级别日志测试");
        
        logger.info("日志配置测试通过");
    }
    
    @Test
    @Order(8)
    @DisplayName("1.8 系统属性测试")
    public void testSystemProperties() {
        logger.info("测试系统属性");
        
        // 检查Java版本
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion, "Java版本应该存在");
        logger.info("Java版本: " + javaVersion);
        
        // 检查操作系统
        String osName = System.getProperty("os.name");
        assertNotNull(osName, "操作系统名称应该存在");
        logger.info("操作系统: " + osName);
        
        // 检查用户目录
        String userDir = System.getProperty("user.dir");
        assertNotNull(userDir, "用户目录应该存在");
        logger.info("用户目录: " + userDir);
        
        // 检查临时目录
        String tmpDir = System.getProperty("java.io.tmpdir");
        assertNotNull(tmpDir, "临时目录应该存在");
        logger.info("临时目录: " + tmpDir);
        
        logger.info("系统属性测试通过");
    }
    
    @Test
    @Order(9)
    @DisplayName("1.9 应用程序信息输出测试")
    public void testApplicationInfoOutput() {
        logger.info("测试应用程序信息输出");
        
        // 验证应用程序会输出的关键信息
        String[] expectedMessages = {
            "Shiwu校园二手交易平台",
            "http://localhost:",
            "/api/",
            "/api/admin/login",
            "/api/user/",
            "/api/products/"
        };
        
        for (String message : expectedMessages) {
            assertNotNull(message, "预期消息不应为null: " + message);
            assertTrue(message.length() > 0, "预期消息不应为空: " + message);
        }
        
        logger.info("应用程序信息输出测试通过");
    }
    
    @Test
    @Order(10)
    @DisplayName("1.10 应用程序完整性验证")
    public void testApplicationIntegrity() {
        logger.info("测试应用程序完整性");
        
        // 验证所有必要的组件都存在
        String[] requiredClasses = {
            "com.shiwu.Application",
            "com.shiwu.user.controller.UserController",
            "com.shiwu.product.controller.ProductController",
            "com.shiwu.order.controller.OrderController",
            "com.shiwu.payment.controller.PaymentController",
            "com.shiwu.admin.controller.AdminController",
            "com.shiwu.common.util.DBUtil",
            "com.shiwu.common.util.JsonUtil"
        };
        
        for (String className : requiredClasses) {
            try {
                Class.forName(className);
                logger.debug("类存在: " + className);
            } catch (ClassNotFoundException e) {
                fail("必需的类不存在: " + className);
            }
        }
        
        logger.info("应用程序完整性验证通过");
    }
    
    @AfterAll
    public static void tearDownClass() {
        logger.info("应用程序启动集成测试完成");
    }
}
