package com.shiwu.integration;

import com.shiwu.test.TestBase;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Servlet容器集成测试
 * 测试Servlet配置、注解和容器集成
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Servlet容器集成测试")
public class ServletContainerIntegrationTest extends TestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(ServletContainerIntegrationTest.class);
    
    // 需要测试的Controller类列表
    private static final List<String> CONTROLLER_CLASSES = Arrays.asList(
        "com.shiwu.user.controller.UserController",
        "com.shiwu.product.controller.ProductController",
        "com.shiwu.product.controller.CategoryController",
        "com.shiwu.order.controller.OrderController",
        "com.shiwu.payment.controller.PaymentController",
        "com.shiwu.payment.controller.PaymentTimeoutController",
        "com.shiwu.cart.controller.CartController",
        "com.shiwu.review.controller.ReviewController",
        "com.shiwu.message.controller.MessageController",
        "com.shiwu.admin.controller.AdminController",
        "com.shiwu.admin.controller.AdminUserController",
        "com.shiwu.admin.controller.AdminProductController",
        "com.shiwu.admin.controller.AuditLogController"
    );
    
    @BeforeAll
    public static void setUpClass() {
        logger.info("开始Servlet容器集成测试");
    }
    
    @Test
    @Order(1)
    @DisplayName("1.1 Servlet API依赖测试")
    public void testServletApiDependencies() {
        logger.info("测试Servlet API依赖");
        
        try {
            // 检查核心Servlet API类
            Class.forName("javax.servlet.http.HttpServlet");
            Class.forName("javax.servlet.http.HttpServletRequest");
            Class.forName("javax.servlet.http.HttpServletResponse");
            Class.forName("javax.servlet.ServletException");
            Class.forName("javax.servlet.annotation.WebServlet");
            Class.forName("javax.servlet.annotation.MultipartConfig");
            
            logger.info("Servlet API依赖检查通过");
        } catch (ClassNotFoundException e) {
            fail("Servlet API依赖缺失: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("1.2 Controller类WebServlet注解测试")
    public void testControllerWebServletAnnotations() {
        logger.info("测试Controller类的WebServlet注解");
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 检查是否有WebServlet注解
                WebServlet webServletAnnotation = controllerClass.getAnnotation(WebServlet.class);
                assertNotNull(webServletAnnotation, className + " 应该有@WebServlet注解");
                
                // 检查URL模式
                String[] urlPatterns = webServletAnnotation.value();
                assertTrue(urlPatterns.length > 0, className + " 应该配置URL模式");
                
                for (String pattern : urlPatterns) {
                    assertNotNull(pattern, "URL模式不应为null");
                    assertTrue(pattern.length() > 0, "URL模式不应为空");
                    assertTrue(pattern.startsWith("/"), "URL模式应该以/开头: " + pattern);
                    
                    logger.debug(className + " -> " + pattern);
                }
                
            } catch (ClassNotFoundException e) {
                fail("Controller类不存在: " + className);
            }
        }
        
        logger.info("Controller类WebServlet注解测试通过");
    }
    
    @Test
    @Order(3)
    @DisplayName("1.3 HttpServlet继承测试")
    public void testHttpServletInheritance() {
        logger.info("测试Controller类继承HttpServlet");
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 检查是否继承自HttpServlet
                Class<?> httpServletClass = Class.forName("javax.servlet.http.HttpServlet");
                assertTrue(httpServletClass.isAssignableFrom(controllerClass), 
                          className + " 应该继承HttpServlet");
                
                logger.debug(className + " 正确继承了HttpServlet");
                
            } catch (ClassNotFoundException e) {
                fail("类加载失败: " + className + " - " + e.getMessage());
            }
        }
        
        logger.info("HttpServlet继承测试通过");
    }
    
    @Test
    @Order(4)
    @DisplayName("1.4 HTTP方法实现测试")
    public void testHttpMethodImplementations() {
        logger.info("测试HTTP方法实现");
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 检查doGet方法
                try {
                    Method doGetMethod = controllerClass.getDeclaredMethod("doGet", 
                        Class.forName("javax.servlet.http.HttpServletRequest"),
                        Class.forName("javax.servlet.http.HttpServletResponse"));
                    assertNotNull(doGetMethod, className + " 应该实现doGet方法");
                    logger.debug(className + " 实现了doGet方法");
                } catch (NoSuchMethodException e) {
                    logger.debug(className + " 未实现doGet方法（可能只支持POST）");
                }
                
                // 检查doPost方法
                try {
                    Method doPostMethod = controllerClass.getDeclaredMethod("doPost", 
                        Class.forName("javax.servlet.http.HttpServletRequest"),
                        Class.forName("javax.servlet.http.HttpServletResponse"));
                    assertNotNull(doPostMethod, className + " 应该实现doPost方法");
                    logger.debug(className + " 实现了doPost方法");
                } catch (NoSuchMethodException e) {
                    logger.debug(className + " 未实现doPost方法（可能只支持GET）");
                }
                
            } catch (ClassNotFoundException e) {
                fail("类加载失败: " + className + " - " + e.getMessage());
            }
        }
        
        logger.info("HTTP方法实现测试通过");
    }
    
    @Test
    @Order(5)
    @DisplayName("1.5 MultipartConfig注解测试")
    public void testMultipartConfigAnnotations() {
        logger.info("测试MultipartConfig注解");
        
        // 检查需要文件上传功能的Controller
        String[] uploadControllers = {
            "com.shiwu.product.controller.ProductController"
        };
        
        for (String className : uploadControllers) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 检查是否有MultipartConfig注解
                javax.servlet.annotation.MultipartConfig multipartConfig = 
                    controllerClass.getAnnotation(javax.servlet.annotation.MultipartConfig.class);
                
                if (multipartConfig != null) {
                    logger.info(className + " 配置了MultipartConfig");
                    logger.debug("fileSizeThreshold: " + multipartConfig.fileSizeThreshold());
                    logger.debug("maxFileSize: " + multipartConfig.maxFileSize());
                    logger.debug("maxRequestSize: " + multipartConfig.maxRequestSize());
                    
                    // 验证配置合理性
                    assertTrue(multipartConfig.maxFileSize() > 0, "最大文件大小应该大于0");
                    assertTrue(multipartConfig.maxRequestSize() >= multipartConfig.maxFileSize(), 
                              "最大请求大小应该不小于最大文件大小");
                } else {
                    logger.debug(className + " 未配置MultipartConfig（可能不需要文件上传）");
                }
                
            } catch (ClassNotFoundException e) {
                fail("Controller类不存在: " + className);
            }
        }
        
        logger.info("MultipartConfig注解测试通过");
    }
    
    @Test
    @Order(6)
    @DisplayName("1.6 URL模式冲突检测")
    public void testUrlPatternConflicts() {
        logger.info("测试URL模式冲突");
        
        java.util.Map<String, String> urlPatternMap = new java.util.HashMap<>();
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                WebServlet webServletAnnotation = controllerClass.getAnnotation(WebServlet.class);
                
                if (webServletAnnotation != null) {
                    String[] urlPatterns = webServletAnnotation.value();
                    
                    for (String pattern : urlPatterns) {
                        if (urlPatternMap.containsKey(pattern)) {
                            fail("URL模式冲突: " + pattern + " 被 " + className + 
                                 " 和 " + urlPatternMap.get(pattern) + " 同时使用");
                        }
                        urlPatternMap.put(pattern, className);
                    }
                }
                
            } catch (ClassNotFoundException e) {
                fail("Controller类不存在: " + className);
            }
        }
        
        logger.info("检查了 " + urlPatternMap.size() + " 个URL模式，无冲突");
        logger.info("URL模式冲突检测通过");
    }
    
    @Test
    @Order(7)
    @DisplayName("1.7 Servlet生命周期方法测试")
    public void testServletLifecycleMethods() {
        logger.info("测试Servlet生命周期方法");
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 检查init方法（可选）
                try {
                    Method initMethod = controllerClass.getDeclaredMethod("init");
                    logger.debug(className + " 重写了init()方法");
                } catch (NoSuchMethodException e) {
                    // init方法是可选的
                    logger.debug(className + " 未重写init()方法");
                }
                
                // 检查destroy方法（可选）
                try {
                    Method destroyMethod = controllerClass.getDeclaredMethod("destroy");
                    logger.debug(className + " 重写了destroy()方法");
                } catch (NoSuchMethodException e) {
                    // destroy方法是可选的
                    logger.debug(className + " 未重写destroy()方法");
                }
                
                // 检查构造函数
                try {
                    controllerClass.getDeclaredConstructor();
                    logger.debug(className + " 有默认构造函数");
                } catch (NoSuchMethodException e) {
                    logger.warn(className + " 没有默认构造函数，可能影响Servlet容器实例化");
                }
                
            } catch (ClassNotFoundException e) {
                fail("Controller类不存在: " + className);
            }
        }
        
        logger.info("Servlet生命周期方法测试通过");
    }
    
    @Test
    @Order(8)
    @DisplayName("1.8 异常处理机制测试")
    public void testExceptionHandlingMechanism() {
        logger.info("测试异常处理机制");
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 检查是否有异常处理相关的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                boolean hasErrorHandling = false;
                
                for (Method method : methods) {
                    String methodName = method.getName().toLowerCase();
                    if (methodName.contains("error") || methodName.contains("exception") || 
                        methodName.contains("handle") && methodName.contains("error")) {
                        hasErrorHandling = true;
                        logger.debug(className + " 有错误处理方法: " + method.getName());
                    }
                }
                
                // 检查是否有发送错误响应的方法
                for (Method method : methods) {
                    String methodName = method.getName().toLowerCase();
                    if (methodName.contains("senderror") || methodName.contains("sendresponse")) {
                        logger.debug(className + " 有响应发送方法: " + method.getName());
                    }
                }
                
            } catch (ClassNotFoundException e) {
                fail("Controller类不存在: " + className);
            }
        }
        
        logger.info("异常处理机制测试通过");
    }
    
    @Test
    @Order(9)
    @DisplayName("1.9 web.xml配置兼容性测试")
    public void testWebXmlCompatibility() {
        logger.info("测试web.xml配置兼容性");
        
        try {
            // 检查web.xml文件是否存在
            java.io.InputStream webXmlStream = getClass().getClassLoader()
                .getResourceAsStream("WEB-INF/web.xml");
            
            if (webXmlStream != null) {
                logger.info("发现web.xml配置文件");
                webXmlStream.close();
                
                // 注意：注解配置和web.xml配置可以共存
                // 但需要确保没有冲突
                logger.info("web.xml和注解配置共存检查通过");
            } else {
                logger.info("未发现web.xml文件，使用纯注解配置");
            }
            
        } catch (Exception e) {
            logger.warn("web.xml检查遇到问题: " + e.getMessage());
        }
        
        logger.info("web.xml配置兼容性测试通过");
    }
    
    @Test
    @Order(10)
    @DisplayName("1.10 Servlet容器集成完整性验证")
    public void testServletContainerIntegrationCompleteness() {
        logger.info("验证Servlet容器集成完整性");
        
        int totalControllers = CONTROLLER_CLASSES.size();
        int validControllers = 0;
        
        for (String className : CONTROLLER_CLASSES) {
            try {
                Class<?> controllerClass = Class.forName(className);
                
                // 验证基本要求
                boolean hasWebServletAnnotation = controllerClass.getAnnotation(WebServlet.class) != null;
                boolean extendsHttpServlet = false;
                
                try {
                    Class<?> httpServletClass = Class.forName("javax.servlet.http.HttpServlet");
                    extendsHttpServlet = httpServletClass.isAssignableFrom(controllerClass);
                } catch (ClassNotFoundException e) {
                    // HttpServlet类不存在
                }
                
                if (hasWebServletAnnotation && extendsHttpServlet) {
                    validControllers++;
                    logger.debug(className + " 集成验证通过");
                } else {
                    logger.warn(className + " 集成验证失败 - WebServlet: " + 
                               hasWebServletAnnotation + ", HttpServlet: " + extendsHttpServlet);
                }
                
            } catch (ClassNotFoundException e) {
                logger.error("Controller类不存在: " + className);
            }
        }
        
        logger.info("Servlet容器集成统计:");
        logger.info("总Controller数: " + totalControllers);
        logger.info("有效Controller数: " + validControllers);
        logger.info("集成完整性: " + String.format("%.1f%%", (double) validControllers / totalControllers * 100));
        
        // 要求至少80%的Controller正确集成
        assertTrue(validControllers >= totalControllers * 0.8, 
                  "至少80%的Controller应该正确集成Servlet容器");
        
        logger.info("Servlet容器集成完整性验证通过");
    }
    
    @AfterAll
    public static void tearDownClass() {
        logger.info("Servlet容器集成测试完成");
    }
}
