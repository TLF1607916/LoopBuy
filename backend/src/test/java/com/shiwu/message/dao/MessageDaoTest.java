package com.shiwu.message.dao;

import com.shiwu.message.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageDao单元测试
 * 
 * 测试消息数据访问层的所有功能
 * 严格遵循项目测试规范
 * 
 * @author Shiwu Team
 * @version 1.0
 */
@DisplayName("消息DAO测试")
class MessageDaoTest {
    
    private MessageDao messageDao;
    private Message testMessage;
    
    @BeforeEach
    void setUp() {
        messageDao = new MessageDao();
        
        // 创建测试消息对象
        testMessage = new Message();
        testMessage.setConversationId("1_2_100");
        testMessage.setSenderId(1L);
        testMessage.setReceiverId(2L);
        testMessage.setProductId(100L);
        testMessage.setContent("测试消息内容");
        testMessage.setMessageType("TEXT");
        testMessage.setRead(false);
        testMessage.setDeleted(false);
    }
    
    @Test
    @DisplayName("插入消息 - 成功")
    void testInsertMessage_Success() {
        // 执行插入
        Long messageId = messageDao.insertMessage(testMessage);
        
        // 验证结果
        assertNotNull(messageId, "消息ID不应为空");
        assertTrue(messageId > 0, "消息ID应大于0");
        
        // 验证插入的数据
        Message insertedMessage = messageDao.findMessageById(messageId);
        assertNotNull(insertedMessage, "插入的消息应能被查询到");
        assertEquals(testMessage.getConversationId(), insertedMessage.getConversationId());
        assertEquals(testMessage.getSenderId(), insertedMessage.getSenderId());
        assertEquals(testMessage.getReceiverId(), insertedMessage.getReceiverId());
        assertEquals(testMessage.getContent(), insertedMessage.getContent());
        assertEquals(testMessage.getMessageType(), insertedMessage.getMessageType());
        assertFalse(insertedMessage.getRead(), "新消息应为未读状态");
        assertFalse(insertedMessage.getDeleted(), "新消息应为未删除状态");
        assertNotNull(insertedMessage.getCreateTime(), "创建时间不应为空");
        assertNotNull(insertedMessage.getUpdateTime(), "更新时间不应为空");
    }
    
    @Test
    @DisplayName("插入消息 - 无商品ID")
    void testInsertMessage_WithoutProductId() {
        // 设置无商品ID的消息
        testMessage.setProductId(null);
        
        // 执行插入
        Long messageId = messageDao.insertMessage(testMessage);
        
        // 验证结果
        assertNotNull(messageId, "消息ID不应为空");
        
        Message insertedMessage = messageDao.findMessageById(messageId);
        assertNotNull(insertedMessage, "插入的消息应能被查询到");
        assertNull(insertedMessage.getProductId(), "商品ID应为空");
    }
    
    @Test
    @DisplayName("根据ID查询消息 - 成功")
    void testFindMessageById_Success() {
        // 先插入一条消息
        Long messageId = messageDao.insertMessage(testMessage);
        
        // 查询消息
        Message foundMessage = messageDao.findMessageById(messageId);
        
        // 验证结果
        assertNotNull(foundMessage, "应能查询到消息");
        assertEquals(messageId, foundMessage.getId());
        assertEquals(testMessage.getConversationId(), foundMessage.getConversationId());
        assertEquals(testMessage.getContent(), foundMessage.getContent());
    }
    
    @Test
    @DisplayName("根据ID查询消息 - 不存在")
    void testFindMessageById_NotFound() {
        // 查询不存在的消息
        Message foundMessage = messageDao.findMessageById(99999L);
        
        // 验证结果
        assertNull(foundMessage, "不存在的消息应返回null");
    }
    
    @Test
    @DisplayName("根据会话ID查询消息列表 - 成功")
    void testFindMessagesByConversationId_Success() {
        String conversationId = "test_conversation_" + System.currentTimeMillis();
        
        // 插入多条消息
        for (int i = 1; i <= 3; i++) {
            Message message = new Message();
            message.setConversationId(conversationId);
            message.setSenderId(1L);
            message.setReceiverId(2L);
            message.setContent("测试消息 " + i);
            message.setMessageType("TEXT");
            message.setRead(false);
            message.setDeleted(false);
            
            messageDao.insertMessage(message);
        }
        
        // 查询消息列表
        List<Message> messages = messageDao.findMessagesByConversationId(conversationId, 0, 10);
        
        // 验证结果
        assertNotNull(messages, "消息列表不应为空");
        assertEquals(3, messages.size(), "应查询到3条消息");
        
        // 验证消息按时间升序排列
        for (int i = 0; i < messages.size() - 1; i++) {
            assertTrue(messages.get(i).getCreateTime().isBefore(messages.get(i + 1).getCreateTime()) ||
                      messages.get(i).getCreateTime().isEqual(messages.get(i + 1).getCreateTime()),
                      "消息应按创建时间升序排列");
        }
    }
    
    @Test
    @DisplayName("根据会话ID查询消息列表 - 分页")
    void testFindMessagesByConversationId_Pagination() {
        String conversationId = "test_pagination_" + System.currentTimeMillis();
        
        // 插入5条消息
        for (int i = 1; i <= 5; i++) {
            Message message = new Message();
            message.setConversationId(conversationId);
            message.setSenderId(1L);
            message.setReceiverId(2L);
            message.setContent("分页测试消息 " + i);
            message.setMessageType("TEXT");
            message.setRead(false);
            message.setDeleted(false);
            
            messageDao.insertMessage(message);
        }
        
        // 查询第一页（前2条）
        List<Message> firstPage = messageDao.findMessagesByConversationId(conversationId, 0, 2);
        assertEquals(2, firstPage.size(), "第一页应有2条消息");
        
        // 查询第二页（第3-4条）
        List<Message> secondPage = messageDao.findMessagesByConversationId(conversationId, 2, 2);
        assertEquals(2, secondPage.size(), "第二页应有2条消息");
        
        // 查询第三页（第5条）
        List<Message> thirdPage = messageDao.findMessagesByConversationId(conversationId, 4, 2);
        assertEquals(1, thirdPage.size(), "第三页应有1条消息");
    }
    
    @Test
    @DisplayName("标记消息为已读 - 成功")
    void testMarkMessageAsRead_Success() {
        // 插入未读消息
        Long messageId = messageDao.insertMessage(testMessage);
        
        // 标记为已读
        boolean result = messageDao.markMessageAsRead(messageId);
        
        // 验证结果
        assertTrue(result, "标记已读应成功");
        
        Message updatedMessage = messageDao.findMessageById(messageId);
        assertTrue(updatedMessage.getRead(), "消息应为已读状态");
    }
    
    @Test
    @DisplayName("标记消息为已读 - 消息不存在")
    void testMarkMessageAsRead_NotFound() {
        // 标记不存在的消息为已读
        boolean result = messageDao.markMessageAsRead(99999L);
        
        // 验证结果
        assertFalse(result, "标记不存在的消息应返回false");
    }
    
    @Test
    @DisplayName("批量标记会话消息为已读 - 成功")
    void testMarkConversationMessagesAsRead_Success() {
        String conversationId = "test_batch_read_" + System.currentTimeMillis();
        Long receiverId = 2L;
        
        // 插入多条未读消息
        for (int i = 1; i <= 3; i++) {
            Message message = new Message();
            message.setConversationId(conversationId);
            message.setSenderId(1L);
            message.setReceiverId(receiverId);
            message.setContent("批量已读测试 " + i);
            message.setMessageType("TEXT");
            message.setRead(false);
            message.setDeleted(false);
            
            messageDao.insertMessage(message);
        }
        
        // 批量标记为已读
        int updatedCount = messageDao.markConversationMessagesAsRead(conversationId, receiverId);
        
        // 验证结果
        assertEquals(3, updatedCount, "应更新3条消息");
        
        // 验证所有消息都已标记为已读
        List<Message> messages = messageDao.findMessagesByConversationId(conversationId, 0, 10);
        for (Message message : messages) {
            assertTrue(message.getRead(), "所有消息都应为已读状态");
        }
    }
    
    @Test
    @DisplayName("统计未读消息数量 - 成功")
    void testCountUnreadMessages_Success() {
        String conversationId = "test_unread_count_" + System.currentTimeMillis();
        Long receiverId = 2L;
        
        // 插入2条未读消息和1条已读消息
        for (int i = 1; i <= 3; i++) {
            Message message = new Message();
            message.setConversationId(conversationId);
            message.setSenderId(1L);
            message.setReceiverId(receiverId);
            message.setContent("未读统计测试 " + i);
            message.setMessageType("TEXT");
            message.setRead(i == 3); // 第3条设为已读
            message.setDeleted(false);
            
            messageDao.insertMessage(message);
        }
        
        // 统计未读消息数量
        Integer unreadCount = messageDao.countUnreadMessages(conversationId, receiverId);
        
        // 验证结果
        assertEquals(2, unreadCount, "应有2条未读消息");
    }
    
    @Test
    @DisplayName("统计未读消息数量 - 无未读消息")
    void testCountUnreadMessages_NoUnread() {
        String conversationId = "test_no_unread_" + System.currentTimeMillis();
        
        // 统计不存在的会话的未读消息
        Integer unreadCount = messageDao.countUnreadMessages(conversationId, 2L);
        
        // 验证结果
        assertEquals(0, unreadCount, "不存在的会话应返回0条未读消息");
    }
    
    @Test
    @DisplayName("逻辑删除消息 - 成功")
    void testDeleteMessage_Success() {
        // 插入消息
        Long messageId = messageDao.insertMessage(testMessage);
        
        // 逻辑删除
        boolean result = messageDao.deleteMessage(messageId);
        
        // 验证结果
        assertTrue(result, "删除应成功");
        
        // 验证消息无法通过正常查询找到（因为查询会过滤已删除的消息）
        Message deletedMessage = messageDao.findMessageById(messageId);
        assertNull(deletedMessage, "已删除的消息应无法查询到");
    }
    
    @Test
    @DisplayName("逻辑删除消息 - 消息不存在")
    void testDeleteMessage_NotFound() {
        // 删除不存在的消息
        boolean result = messageDao.deleteMessage(99999L);
        
        // 验证结果
        assertFalse(result, "删除不存在的消息应返回false");
    }
}
