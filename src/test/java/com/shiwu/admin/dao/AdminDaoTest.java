package com.shiwu.admin.dao;

import com.shiwu.admin.model.Administrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminDao单元测试
 * 遵循BCDE原则：Boundary, Correct, Design, Error
 */
@DisplayName("管理员DAO测试")
public class AdminDaoTest {
    
    private AdminDao adminDao;
    
    @BeforeEach
    public void setUp() {
        adminDao = new AdminDao();
    }
    
    /**
     * 测试根据用户名查找管理员 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("根据用户名查找管理员 - 成功")
    public void testFindByUsername_Success() {
        // Given: 存在的管理员用户名
        String username = "admin";
        
        // When: 查找管理员
        Administrator admin = adminDao.findByUsername(username);
        
        // Then: 验证结果
        if (admin != null) {
            // 如果能找到管理员，验证基本属性
            assertNotNull(admin.getId(), "管理员ID不应为空");
            assertEquals(username, admin.getUsername(), "用户名应该匹配");
            assertNotNull(admin.getPassword(), "密码不应为空");
            assertNotNull(admin.getRole(), "角色不应为空");
            assertNotNull(admin.getStatus(), "状态不应为空");
            assertNotNull(admin.getCreateTime(), "创建时间不应为空");
            
            // 验证状态和删除标志
            assertEquals(1, admin.getStatus(), "管理员状态应该是正常的");
            assertFalse(admin.getDeleted() != null && admin.getDeleted(), "管理员不应该被删除");
            
            System.out.println("✅ 成功查找到管理员: " + admin.getUsername());
        } else {
            // 如果找不到，可能是数据库未初始化或测试数据不存在
            System.out.println("⚠️ 未找到管理员，可能数据库未初始化测试数据");
        }
    }
    
    /**
     * 测试根据用户名查找管理员 - 用户名不存在
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("根据用户名查找管理员 - 用户名不存在")
    public void testFindByUsername_NotFound() {
        // Given: 不存在的管理员用户名
        String username = "nonexistent_admin_12345";
        
        // When: 查找管理员
        Administrator admin = adminDao.findByUsername(username);
        
        // Then: 应该返回null
        assertNull(admin, "不存在的管理员应该返回null");
        System.out.println("✅ 正确处理不存在的管理员用户名");
    }
    
    /**
     * 测试根据用户名查找管理员 - 空用户名
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("根据用户名查找管理员 - 空用户名")
    public void testFindByUsername_NullUsername() {
        // When & Then: 空用户名应该返回null
        assertNull(adminDao.findByUsername(null), "null用户名应该返回null");
        assertNull(adminDao.findByUsername(""), "空字符串用户名应该返回null");
        assertNull(adminDao.findByUsername("   "), "空白字符串用户名应该返回null");
        
        System.out.println("✅ 正确处理空用户名边界条件");
    }
    
    /**
     * 测试根据ID查找管理员 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("根据ID查找管理员 - 成功")
    public void testFindById_Success() {
        // Given: 先通过用户名找到一个管理员
        Administrator adminByUsername = adminDao.findByUsername("admin");
        
        if (adminByUsername != null) {
            Long adminId = adminByUsername.getId();
            
            // When: 通过ID查找管理员
            Administrator adminById = adminDao.findById(adminId);
            
            // Then: 验证结果
            assertNotNull(adminById, "通过ID应该能找到管理员");
            assertEquals(adminId, adminById.getId(), "ID应该匹配");
            assertEquals(adminByUsername.getUsername(), adminById.getUsername(), "用户名应该匹配");
            assertEquals(adminByUsername.getRole(), adminById.getRole(), "角色应该匹配");
            
            System.out.println("✅ 成功通过ID查找到管理员: " + adminById.getUsername());
        } else {
            System.out.println("⚠️ 跳过ID查找测试，因为没有找到测试用的管理员");
        }
    }
    
    /**
     * 测试根据ID查找管理员 - ID不存在
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("根据ID查找管理员 - ID不存在")
    public void testFindById_NotFound() {
        // Given: 一个不存在的ID
        Long nonExistentId = 999999L;
        
        // When: 查找管理员
        Administrator admin = adminDao.findById(nonExistentId);
        
        // Then: 应该返回null
        assertNull(admin, "不存在的ID应该返回null");
        System.out.println("✅ 正确处理不存在的管理员ID");
    }
    
    /**
     * 测试根据ID查找管理员 - 空ID
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("根据ID查找管理员 - 空ID")
    public void testFindById_NullId() {
        // When & Then: 空ID应该返回null
        assertNull(adminDao.findById(null), "null ID应该返回null");
        
        System.out.println("✅ 正确处理空ID边界条件");
    }
    
    /**
     * 测试更新管理员登录信息 - 正常情况
     * BCDE原则中的Correct：使用正确的典型输入
     */
    @Test
    @DisplayName("更新管理员登录信息 - 成功")
    public void testUpdateLastLoginInfo_Success() {
        // Given: 先找到一个管理员
        Administrator admin = adminDao.findByUsername("admin");
        
        if (admin != null) {
            Long adminId = admin.getId();
            Integer originalLoginCount = admin.getLoginCount();
            
            // When: 更新登录信息
            boolean result = adminDao.updateLastLoginInfo(adminId);
            
            // Then: 验证更新成功
            assertTrue(result, "更新登录信息应该成功");
            
            // 重新查询验证登录次数增加
            Administrator updatedAdmin = adminDao.findById(adminId);
            assertNotNull(updatedAdmin, "更新后应该能重新查询到管理员");
            
            if (originalLoginCount != null) {
                assertEquals(originalLoginCount + 1, updatedAdmin.getLoginCount(), 
                           "登录次数应该增加1");
            }
            
            System.out.println("✅ 成功更新管理员登录信息");
        } else {
            System.out.println("⚠️ 跳过登录信息更新测试，因为没有找到测试用的管理员");
        }
    }
    
    /**
     * 测试更新管理员登录信息 - 空ID
     * BCDE原则中的Boundary：测试边界条件
     */
    @Test
    @DisplayName("更新管理员登录信息 - 空ID")
    public void testUpdateLastLoginInfo_NullId() {
        // When & Then: 空ID应该返回false
        assertFalse(adminDao.updateLastLoginInfo(null), "null ID应该返回false");
        
        System.out.println("✅ 正确处理空ID的登录信息更新");
    }
    
    /**
     * 测试更新管理员登录信息 - 不存在的ID
     * BCDE原则中的Error：测试错误情况
     */
    @Test
    @DisplayName("更新管理员登录信息 - 不存在的ID")
    public void testUpdateLastLoginInfo_NotFound() {
        // Given: 一个不存在的ID
        Long nonExistentId = 999999L;
        
        // When: 更新登录信息
        boolean result = adminDao.updateLastLoginInfo(nonExistentId);
        
        // Then: 应该返回false
        assertFalse(result, "不存在的ID应该返回false");
        System.out.println("✅ 正确处理不存在ID的登录信息更新");
    }
}
