package com.shiwu.message.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Conversation模型单元测试
 * 
 * 测试会话实体类的所有功能
 * 严格遵循项目测试规范
 * 
 * @author Shiwu Team
 * @version 1.0
 */
@DisplayName("会话模型测试")
class ConversationTest {
    
    private Conversation conversation;
    private LocalDateTime testTime;
    
    @BeforeEach
    void setUp() {
        conversation = new Conversation();
        testTime = LocalDateTime.now();
    }
    
    @Test
    @DisplayName("默认构造函数")
    void testDefaultConstructor() {
        Conversation newConversation = new Conversation();
        
        assertNotNull(newConversation, "会话对象不应为空");
        assertNull(newConversation.getId(), "ID应为空");
        assertNull(newConversation.getConversationId(), "会话ID应为空");
        assertNull(newConversation.getParticipant1Id(), "参与者1 ID应为空");
        assertNull(newConversation.getParticipant2Id(), "参与者2 ID应为空");
        assertNull(newConversation.getProductId(), "商品ID应为空");
        assertNull(newConversation.getLastMessage(), "最后消息应为空");
        assertNull(newConversation.getLastMessageTime(), "最后消息时间应为空");
        assertNull(newConversation.getUnreadCount1(), "未读数量1应为空");
        assertNull(newConversation.getUnreadCount2(), "未读数量2应为空");
        assertNull(newConversation.getStatus(), "状态应为空");
        assertNull(newConversation.getDeleted(), "删除状态应为空");
        assertNull(newConversation.getCreateTime(), "创建时间应为空");
        assertNull(newConversation.getUpdateTime(), "更新时间应为空");
    }
    
    @Test
    @DisplayName("带参数构造函数")
    void testParameterizedConstructor() {
        String conversationId = "1_2_100";
        Long participant1Id = 1L;
        Long participant2Id = 2L;
        Long productId = 100L;
        
        Conversation newConversation = new Conversation(conversationId, participant1Id, participant2Id, productId);
        
        assertNotNull(newConversation, "会话对象不应为空");
        assertEquals(conversationId, newConversation.getConversationId(), "会话ID应正确设置");
        assertEquals(participant1Id, newConversation.getParticipant1Id(), "参与者1 ID应正确设置");
        assertEquals(participant2Id, newConversation.getParticipant2Id(), "参与者2 ID应正确设置");
        assertEquals(productId, newConversation.getProductId(), "商品ID应正确设置");
        assertEquals(0, newConversation.getUnreadCount1(), "未读数量1应默认为0");
        assertEquals(0, newConversation.getUnreadCount2(), "未读数量2应默认为0");
        assertEquals("ACTIVE", newConversation.getStatus(), "状态应默认为ACTIVE");
        assertFalse(newConversation.getDeleted(), "删除状态应默认为false");
    }
    
    @Test
    @DisplayName("ID属性测试")
    void testIdProperty() {
        Long testId = 12345L;
        
        conversation.setId(testId);
        assertEquals(testId, conversation.getId(), "ID应正确设置和获取");
        
        conversation.setId(null);
        assertNull(conversation.getId(), "ID应能设置为null");
    }
    
    @Test
    @DisplayName("会话ID属性测试")
    void testConversationIdProperty() {
        String testConversationId = "1_2_100";
        
        conversation.setConversationId(testConversationId);
        assertEquals(testConversationId, conversation.getConversationId(), "会话ID应正确设置和获取");
        
        conversation.setConversationId(null);
        assertNull(conversation.getConversationId(), "会话ID应能设置为null");
        
        conversation.setConversationId("");
        assertEquals("", conversation.getConversationId(), "会话ID应能设置为空字符串");
        
        // 测试不同格式的会话ID
        conversation.setConversationId("3_5_200");
        assertEquals("3_5_200", conversation.getConversationId(), "应支持不同的会话ID格式");
    }
    
    @Test
    @DisplayName("参与者ID属性测试")
    void testParticipantIdProperties() {
        Long participant1Id = 1L;
        Long participant2Id = 2L;
        
        conversation.setParticipant1Id(participant1Id);
        conversation.setParticipant2Id(participant2Id);
        
        assertEquals(participant1Id, conversation.getParticipant1Id(), "参与者1 ID应正确设置和获取");
        assertEquals(participant2Id, conversation.getParticipant2Id(), "参与者2 ID应正确设置和获取");
        
        conversation.setParticipant1Id(null);
        conversation.setParticipant2Id(null);
        
        assertNull(conversation.getParticipant1Id(), "参与者1 ID应能设置为null");
        assertNull(conversation.getParticipant2Id(), "参与者2 ID应能设置为null");
    }
    
    @Test
    @DisplayName("商品ID属性测试")
    void testProductIdProperty() {
        Long testProductId = 100L;
        
        conversation.setProductId(testProductId);
        assertEquals(testProductId, conversation.getProductId(), "商品ID应正确设置和获取");
        
        conversation.setProductId(null);
        assertNull(conversation.getProductId(), "商品ID应能设置为null");
    }
    
    @Test
    @DisplayName("最后消息属性测试")
    void testLastMessageProperty() {
        String testLastMessage = "这是最后一条消息";
        
        conversation.setLastMessage(testLastMessage);
        assertEquals(testLastMessage, conversation.getLastMessage(), "最后消息应正确设置和获取");
        
        conversation.setLastMessage(null);
        assertNull(conversation.getLastMessage(), "最后消息应能设置为null");
        
        conversation.setLastMessage("");
        assertEquals("", conversation.getLastMessage(), "最后消息应能设置为空字符串");
        
        // 测试长消息
        StringBuilder longMessageBuilder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longMessageBuilder.append("这是一条很长的最后消息");
        }
        String longMessage = longMessageBuilder.toString();
        conversation.setLastMessage(longMessage);
        assertEquals(longMessage, conversation.getLastMessage(), "应能处理长消息");
    }
    
    @Test
    @DisplayName("最后消息时间属性测试")
    void testLastMessageTimeProperty() {
        conversation.setLastMessageTime(testTime);
        assertEquals(testTime, conversation.getLastMessageTime(), "最后消息时间应正确设置和获取");
        
        conversation.setLastMessageTime(null);
        assertNull(conversation.getLastMessageTime(), "最后消息时间应能设置为null");
        
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        conversation.setLastMessageTime(futureTime);
        assertEquals(futureTime, conversation.getLastMessageTime(), "最后消息时间应能设置为未来时间");
        
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        conversation.setLastMessageTime(pastTime);
        assertEquals(pastTime, conversation.getLastMessageTime(), "最后消息时间应能设置为过去时间");
    }
    
    @Test
    @DisplayName("未读消息数量属性测试")
    void testUnreadCountProperties() {
        Integer unreadCount1 = 5;
        Integer unreadCount2 = 3;
        
        conversation.setUnreadCount1(unreadCount1);
        conversation.setUnreadCount2(unreadCount2);
        
        assertEquals(unreadCount1, conversation.getUnreadCount1(), "未读数量1应正确设置和获取");
        assertEquals(unreadCount2, conversation.getUnreadCount2(), "未读数量2应正确设置和获取");
        
        conversation.setUnreadCount1(0);
        conversation.setUnreadCount2(0);
        
        assertEquals(0, conversation.getUnreadCount1(), "未读数量1应能设置为0");
        assertEquals(0, conversation.getUnreadCount2(), "未读数量2应能设置为0");
        
        conversation.setUnreadCount1(null);
        conversation.setUnreadCount2(null);
        
        assertNull(conversation.getUnreadCount1(), "未读数量1应能设置为null");
        assertNull(conversation.getUnreadCount2(), "未读数量2应能设置为null");
        
        // 测试大数值
        conversation.setUnreadCount1(999);
        conversation.setUnreadCount2(1000);
        
        assertEquals(999, conversation.getUnreadCount1(), "应能处理大的未读数量");
        assertEquals(1000, conversation.getUnreadCount2(), "应能处理大的未读数量");
    }
    
    @Test
    @DisplayName("状态属性测试")
    void testStatusProperty() {
        conversation.setStatus("ACTIVE");
        assertEquals("ACTIVE", conversation.getStatus(), "状态应正确设置为ACTIVE");
        
        conversation.setStatus("ARCHIVED");
        assertEquals("ARCHIVED", conversation.getStatus(), "状态应正确设置为ARCHIVED");
        
        conversation.setStatus("BLOCKED");
        assertEquals("BLOCKED", conversation.getStatus(), "状态应正确设置为BLOCKED");
        
        conversation.setStatus(null);
        assertNull(conversation.getStatus(), "状态应能设置为null");
        
        conversation.setStatus("");
        assertEquals("", conversation.getStatus(), "状态应能设置为空字符串");
    }
    
    @Test
    @DisplayName("删除状态属性测试")
    void testDeletedProperty() {
        conversation.setDeleted(true);
        assertTrue(conversation.getDeleted(), "删除状态应正确设置为true");
        
        conversation.setDeleted(false);
        assertFalse(conversation.getDeleted(), "删除状态应正确设置为false");
        
        conversation.setDeleted(null);
        assertNull(conversation.getDeleted(), "删除状态应能设置为null");
    }
    
    @Test
    @DisplayName("时间属性测试")
    void testTimeProperties() {
        LocalDateTime createTime = LocalDateTime.now().minusHours(1);
        LocalDateTime updateTime = LocalDateTime.now();
        
        conversation.setCreateTime(createTime);
        conversation.setUpdateTime(updateTime);
        
        assertEquals(createTime, conversation.getCreateTime(), "创建时间应正确设置和获取");
        assertEquals(updateTime, conversation.getUpdateTime(), "更新时间应正确设置和获取");
        
        conversation.setCreateTime(null);
        conversation.setUpdateTime(null);
        
        assertNull(conversation.getCreateTime(), "创建时间应能设置为null");
        assertNull(conversation.getUpdateTime(), "更新时间应能设置为null");
    }
    
    @Test
    @DisplayName("toString方法测试")
    void testToStringMethod() {
        // 设置所有属性
        conversation.setId(1L);
        conversation.setConversationId("1_2_100");
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        conversation.setProductId(100L);
        conversation.setLastMessage("测试最后消息");
        conversation.setLastMessageTime(testTime);
        conversation.setUnreadCount1(2);
        conversation.setUnreadCount2(3);
        conversation.setStatus("ACTIVE");
        conversation.setDeleted(false);
        conversation.setCreateTime(testTime);
        conversation.setUpdateTime(testTime);
        
        String toStringResult = conversation.toString();
        
        assertNotNull(toStringResult, "toString结果不应为空");
        assertTrue(toStringResult.contains("Conversation{"), "toString应包含类名");
        assertTrue(toStringResult.contains("id=1"), "toString应包含ID");
        assertTrue(toStringResult.contains("conversationId='1_2_100'"), "toString应包含会话ID");
        assertTrue(toStringResult.contains("participant1Id=1"), "toString应包含参与者1 ID");
        assertTrue(toStringResult.contains("participant2Id=2"), "toString应包含参与者2 ID");
        assertTrue(toStringResult.contains("productId=100"), "toString应包含商品ID");
        assertTrue(toStringResult.contains("lastMessage='测试最后消息'"), "toString应包含最后消息");
        assertTrue(toStringResult.contains("unreadCount1=2"), "toString应包含未读数量1");
        assertTrue(toStringResult.contains("unreadCount2=3"), "toString应包含未读数量2");
        assertTrue(toStringResult.contains("status='ACTIVE'"), "toString应包含状态");
        assertTrue(toStringResult.contains("deleted=false"), "toString应包含删除状态");
    }
    
    @Test
    @DisplayName("toString方法 - 空值测试")
    void testToStringMethodWithNullValues() {
        String toStringResult = conversation.toString();
        
        assertNotNull(toStringResult, "toString结果不应为空");
        assertTrue(toStringResult.contains("Conversation{"), "toString应包含类名");
        assertTrue(toStringResult.contains("id=null"), "toString应正确处理null值");
    }
    
    @Test
    @DisplayName("对象完整性测试")
    void testObjectIntegrity() {
        // 设置完整的会话对象
        conversation.setId(1L);
        conversation.setConversationId("1_2_100");
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        conversation.setProductId(100L);
        conversation.setLastMessage("完整性测试消息");
        conversation.setLastMessageTime(testTime);
        conversation.setUnreadCount1(5);
        conversation.setUnreadCount2(3);
        conversation.setStatus("ACTIVE");
        conversation.setDeleted(false);
        conversation.setCreateTime(testTime);
        conversation.setUpdateTime(testTime);
        
        // 验证所有属性都正确设置
        assertEquals(1L, conversation.getId());
        assertEquals("1_2_100", conversation.getConversationId());
        assertEquals(1L, conversation.getParticipant1Id());
        assertEquals(2L, conversation.getParticipant2Id());
        assertEquals(100L, conversation.getProductId());
        assertEquals("完整性测试消息", conversation.getLastMessage());
        assertEquals(testTime, conversation.getLastMessageTime());
        assertEquals(5, conversation.getUnreadCount1());
        assertEquals(3, conversation.getUnreadCount2());
        assertEquals("ACTIVE", conversation.getStatus());
        assertFalse(conversation.getDeleted());
        assertEquals(testTime, conversation.getCreateTime());
        assertEquals(testTime, conversation.getUpdateTime());
    }
    
    @Test
    @DisplayName("边界值测试")
    void testBoundaryValues() {
        // 测试极大值
        conversation.setId(Long.MAX_VALUE);
        conversation.setParticipant1Id(Long.MAX_VALUE);
        conversation.setParticipant2Id(Long.MAX_VALUE);
        conversation.setProductId(Long.MAX_VALUE);
        conversation.setUnreadCount1(Integer.MAX_VALUE);
        conversation.setUnreadCount2(Integer.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, conversation.getId());
        assertEquals(Long.MAX_VALUE, conversation.getParticipant1Id());
        assertEquals(Long.MAX_VALUE, conversation.getParticipant2Id());
        assertEquals(Long.MAX_VALUE, conversation.getProductId());
        assertEquals(Integer.MAX_VALUE, conversation.getUnreadCount1());
        assertEquals(Integer.MAX_VALUE, conversation.getUnreadCount2());
        
        // 测试最小值
        conversation.setId(1L);
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(1L);
        conversation.setProductId(1L);
        conversation.setUnreadCount1(0);
        conversation.setUnreadCount2(0);
        
        assertEquals(1L, conversation.getId());
        assertEquals(1L, conversation.getParticipant1Id());
        assertEquals(1L, conversation.getParticipant2Id());
        assertEquals(1L, conversation.getProductId());
        assertEquals(0, conversation.getUnreadCount1());
        assertEquals(0, conversation.getUnreadCount2());
    }
}
