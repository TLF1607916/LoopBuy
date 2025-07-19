package com.shiwu.admin.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuditTargetTypeEnum单元测试
 */
public class AuditTargetTypeEnumTest {
    
    @Test
    void testEnumValues() {
        // 测试基本枚举值
        assertEquals("ADMIN", AuditTargetTypeEnum.ADMIN.getCode());
        assertEquals("管理员", AuditTargetTypeEnum.ADMIN.getDescription());
        
        assertEquals("USER", AuditTargetTypeEnum.USER.getCode());
        assertEquals("用户", AuditTargetTypeEnum.USER.getDescription());
        
        assertEquals("PRODUCT", AuditTargetTypeEnum.PRODUCT.getCode());
        assertEquals("商品", AuditTargetTypeEnum.PRODUCT.getDescription());
        
        assertEquals("SYSTEM", AuditTargetTypeEnum.SYSTEM.getCode());
        assertEquals("系统", AuditTargetTypeEnum.SYSTEM.getDescription());
        
        System.out.println("✓ 枚举值测试通过");
    }
    
    @Test
    void testFromCode_ValidCode() {
        // 测试有效代码
        assertEquals(AuditTargetTypeEnum.ADMIN, AuditTargetTypeEnum.fromCode("ADMIN"));
        assertEquals(AuditTargetTypeEnum.USER, AuditTargetTypeEnum.fromCode("USER"));
        assertEquals(AuditTargetTypeEnum.PRODUCT, AuditTargetTypeEnum.fromCode("PRODUCT"));
        assertEquals(AuditTargetTypeEnum.ORDER, AuditTargetTypeEnum.fromCode("ORDER"));
        assertEquals(AuditTargetTypeEnum.SYSTEM, AuditTargetTypeEnum.fromCode("SYSTEM"));
        
        System.out.println("✓ 有效代码转换测试通过");
    }
    
    @Test
    void testFromCode_InvalidCode() {
        // 测试无效代码
        assertNull(AuditTargetTypeEnum.fromCode("INVALID_TYPE"));
        assertNull(AuditTargetTypeEnum.fromCode(""));
        assertNull(AuditTargetTypeEnum.fromCode(null));
        
        System.out.println("✓ 无效代码处理测试通过");
    }
    
    @Test
    void testAllEnumsCovered() {
        // 确保所有枚举都有代码和描述
        for (AuditTargetTypeEnum targetType : AuditTargetTypeEnum.values()) {
            assertNotNull(targetType.getCode());
            assertNotNull(targetType.getDescription());
            assertFalse(targetType.getCode().trim().isEmpty());
            assertFalse(targetType.getDescription().trim().isEmpty());
        }
        
        System.out.println("✓ 所有枚举完整性测试通过，共" + AuditTargetTypeEnum.values().length + "个目标类型");
    }
    
    @Test
    void testEnumConsistency() {
        // 测试枚举的一致性
        for (AuditTargetTypeEnum targetType : AuditTargetTypeEnum.values()) {
            // 通过代码应该能找回原枚举
            assertEquals(targetType, AuditTargetTypeEnum.fromCode(targetType.getCode()));
        }
        
        System.out.println("✓ 枚举一致性测试通过");
    }
    
    @Test
    void testSpecificTargetTypes() {
        // 测试特定的目标类型
        AuditTargetTypeEnum[] expectedTypes = {
            AuditTargetTypeEnum.ADMIN,
            AuditTargetTypeEnum.USER,
            AuditTargetTypeEnum.PRODUCT,
            AuditTargetTypeEnum.ORDER,
            AuditTargetTypeEnum.CATEGORY,
            AuditTargetTypeEnum.SYSTEM,
            AuditTargetTypeEnum.CONFIG,
            AuditTargetTypeEnum.DATA,
            AuditTargetTypeEnum.PERMISSION,
            AuditTargetTypeEnum.ROLE,
            AuditTargetTypeEnum.AUDIT_LOG,
            AuditTargetTypeEnum.FILE,
            AuditTargetTypeEnum.BATCH
        };
        
        // 验证所有预期的类型都存在
        for (AuditTargetTypeEnum expectedType : expectedTypes) {
            assertNotNull(expectedType);
            assertNotNull(expectedType.getCode());
            assertNotNull(expectedType.getDescription());
        }
        
        System.out.println("✓ 特定目标类型测试通过");
    }
    
    @Test
    void testCaseInsensitivity() {
        // 测试代码查找的大小写敏感性
        assertEquals(AuditTargetTypeEnum.USER, AuditTargetTypeEnum.fromCode("USER"));
        assertNull(AuditTargetTypeEnum.fromCode("user")); // 应该是大小写敏感的
        assertNull(AuditTargetTypeEnum.fromCode("User"));
        
        System.out.println("✓ 大小写敏感性测试通过");
    }
    
    @Test
    void testTargetTypeCategories() {
        // 验证不同类别的目标类型
        
        // 实体类型
        assertTrue(AuditTargetTypeEnum.ADMIN.getDescription().contains("管理员"));
        assertTrue(AuditTargetTypeEnum.USER.getDescription().contains("用户"));
        assertTrue(AuditTargetTypeEnum.PRODUCT.getDescription().contains("商品"));
        assertTrue(AuditTargetTypeEnum.ORDER.getDescription().contains("订单"));
        
        // 系统类型
        assertTrue(AuditTargetTypeEnum.SYSTEM.getDescription().contains("系统"));
        assertTrue(AuditTargetTypeEnum.CONFIG.getDescription().contains("配置"));
        assertTrue(AuditTargetTypeEnum.DATA.getDescription().contains("数据"));
        
        // 权限类型
        assertTrue(AuditTargetTypeEnum.PERMISSION.getDescription().contains("权限"));
        assertTrue(AuditTargetTypeEnum.ROLE.getDescription().contains("角色"));
        
        // 特殊类型
        assertTrue(AuditTargetTypeEnum.BATCH.getDescription().contains("批量"));
        assertTrue(AuditTargetTypeEnum.FILE.getDescription().contains("文件"));
        
        System.out.println("✓ 目标类型分类测试通过");
    }
}
