package com.shiwu.admin.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuditActionEnum单元测试
 */
public class AuditActionEnumTest {
    
    @Test
    void testEnumValues() {
        // 测试基本枚举值
        assertEquals("ADMIN_LOGIN", AuditActionEnum.ADMIN_LOGIN.getCode());
        assertEquals("管理员登录", AuditActionEnum.ADMIN_LOGIN.getDescription());
        
        assertEquals("USER_BAN", AuditActionEnum.USER_BAN.getCode());
        assertEquals("封禁用户", AuditActionEnum.USER_BAN.getDescription());
        
        assertEquals("PRODUCT_APPROVE", AuditActionEnum.PRODUCT_APPROVE.getCode());
        assertEquals("审核通过商品", AuditActionEnum.PRODUCT_APPROVE.getDescription());
        
        System.out.println("✓ 枚举值测试通过");
    }
    
    @Test
    void testFromCode_ValidCode() {
        // 测试有效代码
        assertEquals(AuditActionEnum.ADMIN_LOGIN, AuditActionEnum.fromCode("ADMIN_LOGIN"));
        assertEquals(AuditActionEnum.USER_BAN, AuditActionEnum.fromCode("USER_BAN"));
        assertEquals(AuditActionEnum.PRODUCT_APPROVE, AuditActionEnum.fromCode("PRODUCT_APPROVE"));
        
        System.out.println("✓ 有效代码转换测试通过");
    }
    
    @Test
    void testFromCode_InvalidCode() {
        // 测试无效代码
        assertNull(AuditActionEnum.fromCode("INVALID_CODE"));
        assertNull(AuditActionEnum.fromCode(""));
        assertNull(AuditActionEnum.fromCode(null));
        
        System.out.println("✓ 无效代码处理测试通过");
    }
    
    @Test
    void testIsSensitiveOperation() {
        // 所有操作都应该是敏感操作
        assertTrue(AuditActionEnum.ADMIN_LOGIN.isSensitiveOperation());
        assertTrue(AuditActionEnum.USER_BAN.isSensitiveOperation());
        assertTrue(AuditActionEnum.PRODUCT_DELETE.isSensitiveOperation());
        assertTrue(AuditActionEnum.SYSTEM_CONFIG_UPDATE.isSensitiveOperation());
        
        System.out.println("✓ 敏感操作检查测试通过");
    }
    
    @Test
    void testIsHighRiskOperation() {
        // 测试高风险操作
        assertTrue(AuditActionEnum.ADMIN_DELETE.isHighRiskOperation());
        assertTrue(AuditActionEnum.USER_DELETE.isHighRiskOperation());
        assertTrue(AuditActionEnum.USER_BATCH_BAN.isHighRiskOperation());
        assertTrue(AuditActionEnum.PRODUCT_DELETE.isHighRiskOperation());
        assertTrue(AuditActionEnum.SYSTEM_CONFIG_UPDATE.isHighRiskOperation());
        assertTrue(AuditActionEnum.DATA_DELETE.isHighRiskOperation());
        assertTrue(AuditActionEnum.SECURITY_POLICY_UPDATE.isHighRiskOperation());
        assertTrue(AuditActionEnum.BULK_OPERATION.isHighRiskOperation());
        assertTrue(AuditActionEnum.CRITICAL_FUNCTION_EXECUTE.isHighRiskOperation());
        
        // 测试非高风险操作
        assertFalse(AuditActionEnum.ADMIN_LOGIN.isHighRiskOperation());
        assertFalse(AuditActionEnum.USER_BAN.isHighRiskOperation());
        assertFalse(AuditActionEnum.PRODUCT_APPROVE.isHighRiskOperation());
        assertFalse(AuditActionEnum.AUDIT_LOG_VIEW.isHighRiskOperation());
        
        System.out.println("✓ 高风险操作检查测试通过");
    }
    
    @Test
    void testAllEnumsCovered() {
        // 确保所有枚举都有代码和描述
        for (AuditActionEnum action : AuditActionEnum.values()) {
            assertNotNull(action.getCode());
            assertNotNull(action.getDescription());
            assertFalse(action.getCode().trim().isEmpty());
            assertFalse(action.getDescription().trim().isEmpty());
            assertTrue(action.isSensitiveOperation()); // 所有操作都应该是敏感的
        }
        
        System.out.println("✓ 所有枚举完整性测试通过，共" + AuditActionEnum.values().length + "个操作类型");
    }
    
    @Test
    void testEnumConsistency() {
        // 测试枚举的一致性
        for (AuditActionEnum action : AuditActionEnum.values()) {
            // 通过代码应该能找回原枚举
            assertEquals(action, AuditActionEnum.fromCode(action.getCode()));
        }
        
        System.out.println("✓ 枚举一致性测试通过");
    }
    
    @Test
    void testSpecificOperationCategories() {
        // 测试管理员相关操作
        assertTrue(AuditActionEnum.ADMIN_LOGIN.getCode().startsWith("ADMIN_"));
        assertTrue(AuditActionEnum.ADMIN_LOGOUT.getCode().startsWith("ADMIN_"));
        assertTrue(AuditActionEnum.ADMIN_CREATE.getCode().startsWith("ADMIN_"));
        
        // 测试用户相关操作
        assertTrue(AuditActionEnum.USER_BAN.getCode().startsWith("USER_"));
        assertTrue(AuditActionEnum.USER_MUTE.getCode().startsWith("USER_"));
        assertTrue(AuditActionEnum.USER_DELETE.getCode().startsWith("USER_"));
        
        // 测试商品相关操作
        assertTrue(AuditActionEnum.PRODUCT_APPROVE.getCode().startsWith("PRODUCT_"));
        assertTrue(AuditActionEnum.PRODUCT_REJECT.getCode().startsWith("PRODUCT_"));
        assertTrue(AuditActionEnum.PRODUCT_DELETE.getCode().startsWith("PRODUCT_"));
        
        // 测试系统相关操作
        assertTrue(AuditActionEnum.SYSTEM_CONFIG_UPDATE.getCode().startsWith("SYSTEM_"));
        assertTrue(AuditActionEnum.SYSTEM_MAINTENANCE.getCode().startsWith("SYSTEM_"));
        
        System.out.println("✓ 操作分类测试通过");
    }
}
