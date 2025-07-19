package com.shiwu.test;

import com.shiwu.user.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试3: UserController控制器测试
 * 测试用户控制器的基本结构和配置
 */
public class Test3_UserControllerTest {
    
    private UserController userController;
    
    @BeforeEach
    public void setUp() {
        userController = new UserController();
        System.out.println("=== 测试3: UserController控制器测试 ===");
    }
    
    /**
     * 测试3.1: Controller实例化
     */
    @Test
    public void test3_1_ControllerInstantiation() {
        System.out.println("测试3.1: Controller实例化");
        
        assertNotNull(userController, "UserController应该能正常实例化");
        System.out.println("✅ UserController实例化成功");
        System.out.println("✅ 测试3.1完成");
    }
    
    /**
     * 测试3.2: 验证Controller是HttpServlet的子类
     */
    @Test
    public void test3_2_ControllerInheritance() {
        System.out.println("测试3.2: 验证Controller继承关系");
        
        assertTrue(userController instanceof javax.servlet.http.HttpServlet, 
                  "UserController应该继承HttpServlet");
        System.out.println("✅ UserController正确继承HttpServlet");
        System.out.println("✅ 测试3.2完成");
    }
    
    /**
     * 测试3.3: 验证WebServlet注解配置
     */
    @Test
    public void test3_3_WebServletAnnotation() {
        System.out.println("测试3.3: 验证WebServlet注解配置");
        
        Class<?> controllerClass = userController.getClass();
        javax.servlet.annotation.WebServlet webServletAnnotation = 
            controllerClass.getAnnotation(javax.servlet.annotation.WebServlet.class);
        
        assertNotNull(webServletAnnotation, "UserController应该有@WebServlet注解");
        
        String[] urlPatterns = webServletAnnotation.value();
        assertTrue(urlPatterns.length > 0, "应该配置URL模式");
        
        boolean hasUserPattern = false;
        for (String pattern : urlPatterns) {
            System.out.println("发现URL模式: " + pattern);
            if (pattern.contains("/api/user")) {
                hasUserPattern = true;
                break;
            }
        }
        
        assertTrue(hasUserPattern, "应该配置/api/user相关的URL模式");
        System.out.println("✅ WebServlet注解配置正确");
        System.out.println("✅ 测试3.3完成");
    }
    
    /**
     * 测试3.4: 验证Controller有必要的HTTP方法
     */
    @Test
    public void test3_4_ControllerMethods() {
        System.out.println("测试3.4: 验证Controller方法");
        
        Class<?> controllerClass = userController.getClass();
        
        try {
            // 检查是否有doGet方法
            controllerClass.getDeclaredMethod("doGet", 
                javax.servlet.http.HttpServletRequest.class, 
                javax.servlet.http.HttpServletResponse.class);
            System.out.println("✅ 找到doGet方法");
            
            // 检查是否有doPost方法
            controllerClass.getDeclaredMethod("doPost", 
                javax.servlet.http.HttpServletRequest.class, 
                javax.servlet.http.HttpServletResponse.class);
            System.out.println("✅ 找到doPost方法");
            
            // 检查是否有doDelete方法
            try {
                controllerClass.getDeclaredMethod("doDelete", 
                    javax.servlet.http.HttpServletRequest.class, 
                    javax.servlet.http.HttpServletResponse.class);
                System.out.println("✅ 找到doDelete方法");
            } catch (NoSuchMethodException e) {
                System.out.println("⚠️ 未找到doDelete方法（可选）");
            }
            
            System.out.println("✅ 测试3.4完成");
            
        } catch (NoSuchMethodException e) {
            System.out.println("⚠️ 缺少某些HTTP方法: " + e.getMessage());
            fail("Controller应该有基本的HTTP方法");
        }
    }
    
    /**
     * 测试3.5: 验证Controller的服务依赖
     */
    @Test
    public void test3_5_ServiceDependency() {
        System.out.println("测试3.5: 验证服务依赖");
        
        try {
            // 通过反射检查是否有UserService字段
            Class<?> controllerClass = userController.getClass();
            java.lang.reflect.Field[] fields = controllerClass.getDeclaredFields();
            
            boolean hasUserService = false;
            for (java.lang.reflect.Field field : fields) {
                System.out.println("发现字段: " + field.getName() + " (类型: " + field.getType().getSimpleName() + ")");
                if (field.getType().getSimpleName().contains("UserService")) {
                    hasUserService = true;
                    System.out.println("✅ 找到UserService依赖: " + field.getName());
                    break;
                }
            }
            
            if (hasUserService) {
                System.out.println("✅ Controller正确依赖UserService");
            } else {
                System.out.println("⚠️ 未找到UserService依赖，Controller可能直接使用DAO");
            }
            
            System.out.println("✅ 测试3.5完成");
            
        } catch (Exception e) {
            System.out.println("⚠️ 检查服务依赖时出错: " + e.getMessage());
        }
    }
    
    /**
     * 测试3.6: 验证Controller类的基本信息
     */
    @Test
    public void test3_6_ControllerInfo() {
        System.out.println("测试3.6: 验证Controller类信息");
        
        Class<?> controllerClass = userController.getClass();
        
        System.out.println("Controller类名: " + controllerClass.getSimpleName());
        System.out.println("Controller包名: " + controllerClass.getPackage().getName());
        
        // 检查是否是public类
        assertTrue(java.lang.reflect.Modifier.isPublic(controllerClass.getModifiers()), 
                  "Controller应该是public类");
        System.out.println("✅ Controller是public类");
        
        // 检查构造函数
        try {
            controllerClass.getDeclaredConstructor();
            System.out.println("✅ Controller有默认构造函数");
        } catch (NoSuchMethodException e) {
            System.out.println("⚠️ Controller没有默认构造函数");
        }
        
        System.out.println("✅ 测试3.6完成");
    }
}
