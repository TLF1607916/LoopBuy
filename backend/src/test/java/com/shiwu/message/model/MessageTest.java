package com.shiwu.message.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Message模型单元测试
 * 
 * 测试消息实体类的所有功能
 * 严格遵循项目测试规范
 * 
 * @author Shiwu Team
 * @version 1.0
 */
@DisplayName("消息模型测试")
class MessageTest {
    
    private Message message;
    private LocalDateTime testTime;
    
    @BeforeEach
    void setUp() {
        message = new Message();
        testTime = LocalDateTime.now();
    }
    
    @Test
    @DisplayName("默认构造函数")
    void testDefaultConstructor() {
        Message newMessage = new Message();
        
        assertNotNull(newMessage, "消息对象不应为空");
        assertNull(newMessage.getId(), "ID应为空");
        assertNull(newMessage.getConversationId(), "会话ID应为空");
        assertNull(newMessage.getSenderId(), "发送者ID应为空");
        assertNull(newMessage.getReceiverId(), "接收者ID应为空");
        assertNull(newMessage.getProductId(), "商品ID应为空");
        assertNull(newMessage.getContent(), "内容应为空");
        assertNull(newMessage.getMessageType(), "消息类型应为空");
        assertNull(newMessage.getRead(), "已读状态应为空");
        assertNull(newMessage.getDeleted(), "删除状态应为空");
        assertNull(newMessage.getCreateTime(), "创建时间应为空");
        assertNull(newMessage.getUpdateTime(), "更新时间应为空");
    }
    
    @Test
    @DisplayName("带参数构造函数")
    void testParameterizedConstructor() {
        String conversationId = "1_2_100";
        Long senderId = 1L;
        Long receiverId = 2L;
        Long productId = 100L;
        String content = "测试消息内容";
        String messageType = "TEXT";
        
        Message newMessage = new Message(conversationId, senderId, receiverId, productId, content, messageType);
        
        assertNotNull(newMessage, "消息对象不应为空");
        assertEquals(conversationId, newMessage.getConversationId(), "会话ID应正确设置");
        assertEquals(senderId, newMessage.getSenderId(), "发送者ID应正确设置");
        assertEquals(receiverId, newMessage.getReceiverId(), "接收者ID应正确设置");
        assertEquals(productId, newMessage.getProductId(), "商品ID应正确设置");
        assertEquals(content, newMessage.getContent(), "内容应正确设置");
        assertEquals(messageType, newMessage.getMessageType(), "消息类型应正确设置");
        assertFalse(newMessage.getRead(), "已读状态应默认为false");
        assertFalse(newMessage.getDeleted(), "删除状态应默认为false");
    }
    
    @Test
    @DisplayName("ID属性测试")
    void testIdProperty() {
        Long testId = 12345L;
        
        message.setId(testId);
        assertEquals(testId, message.getId(), "ID应正确设置和获取");
        
        message.setId(null);
        assertNull(message.getId(), "ID应能设置为null");
    }
    
    @Test
    @DisplayName("会话ID属性测试")
    void testConversationIdProperty() {
        String testConversationId = "1_2_100";
        
        message.setConversationId(testConversationId);
        assertEquals(testConversationId, message.getConversationId(), "会话ID应正确设置和获取");
        
        message.setConversationId(null);
        assertNull(message.getConversationId(), "会话ID应能设置为null");
        
        message.setConversationId("");
        assertEquals("", message.getConversationId(), "会话ID应能设置为空字符串");
    }
    
    @Test
    @DisplayName("发送者ID属性测试")
    void testSenderIdProperty() {
        Long testSenderId = 1L;
        
        message.setSenderId(testSenderId);
        assertEquals(testSenderId, message.getSenderId(), "发送者ID应正确设置和获取");
        
        message.setSenderId(null);
        assertNull(message.getSenderId(), "发送者ID应能设置为null");
    }
    
    @Test
    @DisplayName("接收者ID属性测试")
    void testReceiverIdProperty() {
        Long testReceiverId = 2L;
        
        message.setReceiverId(testReceiverId);
        assertEquals(testReceiverId, message.getReceiverId(), "接收者ID应正确设置和获取");
        
        message.setReceiverId(null);
        assertNull(message.getReceiverId(), "接收者ID应能设置为null");
    }
    
    @Test
    @DisplayName("商品ID属性测试")
    void testProductIdProperty() {
        Long testProductId = 100L;
        
        message.setProductId(testProductId);
        assertEquals(testProductId, message.getProductId(), "商品ID应正确设置和获取");
        
        message.setProductId(null);
        assertNull(message.getProductId(), "商品ID应能设置为null");
    }
    
    @Test
    @DisplayName("消息内容属性测试")
    void testContentProperty() {
        String testContent = "这是一条测试消息";
        
        message.setContent(testContent);
        assertEquals(testContent, message.getContent(), "消息内容应正确设置和获取");
        
        message.setContent(null);
        assertNull(message.getContent(), "消息内容应能设置为null");
        
        message.setContent("");
        assertEquals("", message.getContent(), "消息内容应能设置为空字符串");
        
        // 测试长内容
        StringBuilder longContentBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longContentBuilder.append("这是一条很长的测试消息");
        }
        String longContent = longContentBuilder.toString();
        message.setContent(longContent);
        assertEquals(longContent, message.getContent(), "应能处理长内容");
    }
    
    @Test
    @DisplayName("消息类型属性测试")
    void testMessageTypeProperty() {
        String testMessageType = "TEXT";
        
        message.setMessageType(testMessageType);
        assertEquals(testMessageType, message.getMessageType(), "消息类型应正确设置和获取");
        
        message.setMessageType("IMAGE");
        assertEquals("IMAGE", message.getMessageType(), "消息类型应能更新");
        
        message.setMessageType("SYSTEM");
        assertEquals("SYSTEM", message.getMessageType(), "消息类型应支持系统消息");
        
        message.setMessageType(null);
        assertNull(message.getMessageType(), "消息类型应能设置为null");
    }
    
    @Test
    @DisplayName("已读状态属性测试")
    void testReadProperty() {
        message.setRead(true);
        assertTrue(message.getRead(), "已读状态应正确设置为true");
        
        message.setRead(false);
        assertFalse(message.getRead(), "已读状态应正确设置为false");
        
        message.setRead(null);
        assertNull(message.getRead(), "已读状态应能设置为null");
    }
    
    @Test
    @DisplayName("删除状态属性测试")
    void testDeletedProperty() {
        message.setDeleted(true);
        assertTrue(message.getDeleted(), "删除状态应正确设置为true");
        
        message.setDeleted(false);
        assertFalse(message.getDeleted(), "删除状态应正确设置为false");
        
        message.setDeleted(null);
        assertNull(message.getDeleted(), "删除状态应能设置为null");
    }
    
    @Test
    @DisplayName("创建时间属性测试")
    void testCreateTimeProperty() {
        message.setCreateTime(testTime);
        assertEquals(testTime, message.getCreateTime(), "创建时间应正确设置和获取");
        
        message.setCreateTime(null);
        assertNull(message.getCreateTime(), "创建时间应能设置为null");
        
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        message.setCreateTime(futureTime);
        assertEquals(futureTime, message.getCreateTime(), "创建时间应能设置为未来时间");
        
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        message.setCreateTime(pastTime);
        assertEquals(pastTime, message.getCreateTime(), "创建时间应能设置为过去时间");
    }
    
    @Test
    @DisplayName("更新时间属性测试")
    void testUpdateTimeProperty() {
        message.setUpdateTime(testTime);
        assertEquals(testTime, message.getUpdateTime(), "更新时间应正确设置和获取");
        
        message.setUpdateTime(null);
        assertNull(message.getUpdateTime(), "更新时间应能设置为null");
        
        LocalDateTime newTime = LocalDateTime.now().plusMinutes(5);
        message.setUpdateTime(newTime);
        assertEquals(newTime, message.getUpdateTime(), "更新时间应能更新");
    }
    
    @Test
    @DisplayName("toString方法测试")
    void testToStringMethod() {
        // 设置所有属性
        message.setId(1L);
        message.setConversationId("1_2_100");
        message.setSenderId(1L);
        message.setReceiverId(2L);
        message.setProductId(100L);
        message.setContent("测试消息");
        message.setMessageType("TEXT");
        message.setRead(false);
        message.setDeleted(false);
        message.setCreateTime(testTime);
        message.setUpdateTime(testTime);
        
        String toStringResult = message.toString();
        
        assertNotNull(toStringResult, "toString结果不应为空");
        assertTrue(toStringResult.contains("Message{"), "toString应包含类名");
        assertTrue(toStringResult.contains("id=1"), "toString应包含ID");
        assertTrue(toStringResult.contains("conversationId='1_2_100'"), "toString应包含会话ID");
        assertTrue(toStringResult.contains("senderId=1"), "toString应包含发送者ID");
        assertTrue(toStringResult.contains("receiverId=2"), "toString应包含接收者ID");
        assertTrue(toStringResult.contains("productId=100"), "toString应包含商品ID");
        assertTrue(toStringResult.contains("content='测试消息'"), "toString应包含内容");
        assertTrue(toStringResult.contains("messageType='TEXT'"), "toString应包含消息类型");
        assertTrue(toStringResult.contains("read=false"), "toString应包含已读状态");
        assertTrue(toStringResult.contains("deleted=false"), "toString应包含删除状态");
    }
    
    @Test
    @DisplayName("toString方法 - 空值测试")
    void testToStringMethodWithNullValues() {
        String toStringResult = message.toString();
        
        assertNotNull(toStringResult, "toString结果不应为空");
        assertTrue(toStringResult.contains("Message{"), "toString应包含类名");
        assertTrue(toStringResult.contains("id=null"), "toString应正确处理null值");
    }
    
    @Test
    @DisplayName("对象完整性测试")
    void testObjectIntegrity() {
        // 设置完整的消息对象
        message.setId(1L);
        message.setConversationId("1_2_100");
        message.setSenderId(1L);
        message.setReceiverId(2L);
        message.setProductId(100L);
        message.setContent("完整性测试消息");
        message.setMessageType("TEXT");
        message.setRead(false);
        message.setDeleted(false);
        message.setCreateTime(testTime);
        message.setUpdateTime(testTime);
        
        // 验证所有属性都正确设置
        assertEquals(1L, message.getId());
        assertEquals("1_2_100", message.getConversationId());
        assertEquals(1L, message.getSenderId());
        assertEquals(2L, message.getReceiverId());
        assertEquals(100L, message.getProductId());
        assertEquals("完整性测试消息", message.getContent());
        assertEquals("TEXT", message.getMessageType());
        assertFalse(message.getRead());
        assertFalse(message.getDeleted());
        assertEquals(testTime, message.getCreateTime());
        assertEquals(testTime, message.getUpdateTime());
    }
}
