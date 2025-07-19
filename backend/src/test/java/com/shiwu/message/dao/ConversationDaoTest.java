package com.shiwu.message.dao;

import com.shiwu.message.model.Conversation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConversationDao单元测试
 * 
 * 测试会话数据访问层的所有功能
 * 严格遵循项目测试规范
 * 
 * @author Shiwu Team
 * @version 1.0
 */
@DisplayName("会话DAO测试")
class ConversationDaoTest {
    
    private ConversationDao conversationDao;
    private Conversation testConversation;
    
    @BeforeEach
    void setUp() {
        conversationDao = new ConversationDao();
        
        // 创建测试会话对象
        testConversation = new Conversation();
        testConversation.setConversationId("test_" + System.currentTimeMillis());
        testConversation.setParticipant1Id(1L);
        testConversation.setParticipant2Id(2L);
        testConversation.setProductId(100L);
        testConversation.setLastMessage("测试最后消息");
        testConversation.setLastMessageTime(LocalDateTime.now());
        testConversation.setUnreadCount1(0);
        testConversation.setUnreadCount2(1);
        testConversation.setStatus("ACTIVE");
        testConversation.setDeleted(false);
    }
    
    @Test
    @DisplayName("插入会话 - 成功")
    void testInsertConversation_Success() {
        // 执行插入
        Long conversationDbId = conversationDao.insertConversation(testConversation);
        
        // 验证结果
        assertNotNull(conversationDbId, "会话数据库ID不应为空");
        assertTrue(conversationDbId > 0, "会话数据库ID应大于0");
        
        // 验证插入的数据
        Conversation insertedConversation = conversationDao.findConversationById(testConversation.getConversationId());
        assertNotNull(insertedConversation, "插入的会话应能被查询到");
        assertEquals(testConversation.getConversationId(), insertedConversation.getConversationId());
        assertEquals(testConversation.getParticipant1Id(), insertedConversation.getParticipant1Id());
        assertEquals(testConversation.getParticipant2Id(), insertedConversation.getParticipant2Id());
        assertEquals(testConversation.getProductId(), insertedConversation.getProductId());
        assertEquals(testConversation.getLastMessage(), insertedConversation.getLastMessage());
        assertEquals(testConversation.getUnreadCount1(), insertedConversation.getUnreadCount1());
        assertEquals(testConversation.getUnreadCount2(), insertedConversation.getUnreadCount2());
        assertEquals(testConversation.getStatus(), insertedConversation.getStatus());
        assertFalse(insertedConversation.getDeleted(), "新会话应为未删除状态");
        assertNotNull(insertedConversation.getCreateTime(), "创建时间不应为空");
        assertNotNull(insertedConversation.getUpdateTime(), "更新时间不应为空");
    }
    
    @Test
    @DisplayName("插入会话 - 无商品ID")
    void testInsertConversation_WithoutProductId() {
        // 设置无商品ID的会话
        testConversation.setProductId(null);
        
        // 执行插入
        Long conversationDbId = conversationDao.insertConversation(testConversation);
        
        // 验证结果
        assertNotNull(conversationDbId, "会话数据库ID不应为空");
        
        Conversation insertedConversation = conversationDao.findConversationById(testConversation.getConversationId());
        assertNotNull(insertedConversation, "插入的会话应能被查询到");
        assertNull(insertedConversation.getProductId(), "商品ID应为空");
    }
    
    @Test
    @DisplayName("根据会话ID查询会话 - 成功")
    void testFindConversationById_Success() {
        // 先插入一个会话
        conversationDao.insertConversation(testConversation);
        
        // 查询会话
        Conversation foundConversation = conversationDao.findConversationById(testConversation.getConversationId());
        
        // 验证结果
        assertNotNull(foundConversation, "应能查询到会话");
        assertEquals(testConversation.getConversationId(), foundConversation.getConversationId());
        assertEquals(testConversation.getParticipant1Id(), foundConversation.getParticipant1Id());
        assertEquals(testConversation.getParticipant2Id(), foundConversation.getParticipant2Id());
    }
    
    @Test
    @DisplayName("根据会话ID查询会话 - 不存在")
    void testFindConversationById_NotFound() {
        // 查询不存在的会话
        Conversation foundConversation = conversationDao.findConversationById("non_existent_conversation");
        
        // 验证结果
        assertNull(foundConversation, "不存在的会话应返回null");
    }
    
    @Test
    @DisplayName("查询用户会话列表 - 成功")
    void testFindConversationsByUserId_Success() {
        Long userId = 1L;
        
        // 插入多个会话
        for (int i = 1; i <= 3; i++) {
            Conversation conversation = new Conversation();
            conversation.setConversationId("user_conv_" + System.currentTimeMillis() + "_" + i);
            conversation.setParticipant1Id(userId);
            conversation.setParticipant2Id((long) (i + 10));
            conversation.setLastMessage("用户会话测试 " + i);
            conversation.setLastMessageTime(LocalDateTime.now().minusMinutes(i));
            conversation.setUnreadCount1(0);
            conversation.setUnreadCount2(i);
            conversation.setStatus("ACTIVE");
            conversation.setDeleted(false);
            
            conversationDao.insertConversation(conversation);
        }
        
        // 查询用户会话列表
        List<Conversation> conversations = conversationDao.findConversationsByUserId(userId, null, null, 0, 10);
        
        // 验证结果
        assertNotNull(conversations, "会话列表不应为空");
        assertTrue(conversations.size() >= 3, "应查询到至少3个会话");
        
        // 验证会话按最后消息时间降序排列
        for (int i = 0; i < conversations.size() - 1; i++) {
            LocalDateTime current = conversations.get(i).getLastMessageTime();
            LocalDateTime next = conversations.get(i + 1).getLastMessageTime();
            if (current != null && next != null) {
                assertTrue(current.isAfter(next) || current.isEqual(next),
                          "会话应按最后消息时间降序排列");
            }
        }
    }
    
    @Test
    @DisplayName("查询用户会话列表 - 按状态过滤")
    void testFindConversationsByUserId_FilterByStatus() {
        Long userId = 1L;
        String uniquePrefix = "status_filter_" + System.currentTimeMillis();
        
        // 插入不同状态的会话
        Conversation activeConv = new Conversation();
        activeConv.setConversationId(uniquePrefix + "_active");
        activeConv.setParticipant1Id(userId);
        activeConv.setParticipant2Id(20L);
        activeConv.setStatus("ACTIVE");
        activeConv.setDeleted(false);
        conversationDao.insertConversation(activeConv);
        
        Conversation archivedConv = new Conversation();
        archivedConv.setConversationId(uniquePrefix + "_archived");
        archivedConv.setParticipant1Id(userId);
        archivedConv.setParticipant2Id(21L);
        archivedConv.setStatus("ARCHIVED");
        archivedConv.setDeleted(false);
        conversationDao.insertConversation(archivedConv);
        
        // 查询活跃状态的会话
        List<Conversation> activeConversations = conversationDao.findConversationsByUserId(userId, "ACTIVE", null, 0, 10);
        
        // 验证结果
        assertNotNull(activeConversations, "活跃会话列表不应为空");
        for (Conversation conv : activeConversations) {
            assertEquals("ACTIVE", conv.getStatus(), "所有会话都应为活跃状态");
        }
    }
    
    @Test
    @DisplayName("查询用户会话列表 - 只显示有未读消息的会话")
    void testFindConversationsByUserId_OnlyUnread() {
        Long userId = 1L;
        String uniquePrefix = "unread_filter_" + System.currentTimeMillis();
        
        // 插入有未读消息的会话
        Conversation unreadConv = new Conversation();
        unreadConv.setConversationId(uniquePrefix + "_unread");
        unreadConv.setParticipant1Id(userId);
        unreadConv.setParticipant2Id(30L);
        unreadConv.setUnreadCount1(3);
        unreadConv.setUnreadCount2(0);
        unreadConv.setStatus("ACTIVE");
        unreadConv.setDeleted(false);
        conversationDao.insertConversation(unreadConv);
        
        // 插入无未读消息的会话
        Conversation readConv = new Conversation();
        readConv.setConversationId(uniquePrefix + "_read");
        readConv.setParticipant1Id(userId);
        readConv.setParticipant2Id(31L);
        readConv.setUnreadCount1(0);
        readConv.setUnreadCount2(0);
        readConv.setStatus("ACTIVE");
        readConv.setDeleted(false);
        conversationDao.insertConversation(readConv);
        
        // 查询只有未读消息的会话
        List<Conversation> unreadConversations = conversationDao.findConversationsByUserId(userId, null, true, 0, 10);
        
        // 验证结果
        assertNotNull(unreadConversations, "未读会话列表不应为空");
        for (Conversation conv : unreadConversations) {
            boolean hasUnread = (conv.getParticipant1Id().equals(userId) && conv.getUnreadCount1() > 0) ||
                               (conv.getParticipant2Id().equals(userId) && conv.getUnreadCount2() > 0);
            assertTrue(hasUnread, "所有会话都应有未读消息");
        }
    }
    
    @Test
    @DisplayName("更新最后消息信息 - 成功")
    void testUpdateLastMessage_Success() {
        // 插入会话
        conversationDao.insertConversation(testConversation);
        
        // 更新最后消息
        String newLastMessage = "更新后的最后消息";
        LocalDateTime newLastMessageTime = LocalDateTime.now();
        boolean result = conversationDao.updateLastMessage(testConversation.getConversationId(), 
                                                          newLastMessage, newLastMessageTime);
        
        // 验证结果
        assertTrue(result, "更新最后消息应成功");
        
        Conversation updatedConversation = conversationDao.findConversationById(testConversation.getConversationId());
        assertEquals(newLastMessage, updatedConversation.getLastMessage(), "最后消息应已更新");
        assertNotNull(updatedConversation.getLastMessageTime(), "最后消息时间应已更新");
    }
    
    @Test
    @DisplayName("更新未读消息数量 - 成功")
    void testUpdateUnreadCount_Success() {
        // 插入会话
        conversationDao.insertConversation(testConversation);
        
        // 更新参与者1的未读数量
        boolean result = conversationDao.updateUnreadCount(testConversation.getConversationId(), 
                                                          testConversation.getParticipant1Id(), 5);
        
        // 验证结果
        assertTrue(result, "更新未读数量应成功");
        
        Conversation updatedConversation = conversationDao.findConversationById(testConversation.getConversationId());
        assertEquals(5, updatedConversation.getUnreadCount1(), "参与者1的未读数量应已更新");
        assertEquals(testConversation.getUnreadCount2(), updatedConversation.getUnreadCount2(), 
                    "参与者2的未读数量应保持不变");
    }
    
    @Test
    @DisplayName("增加未读消息数量 - 成功")
    void testIncrementUnreadCount_Success() {
        // 插入会话
        conversationDao.insertConversation(testConversation);
        
        // 获取初始未读数量
        Conversation initialConversation = conversationDao.findConversationById(testConversation.getConversationId());
        Integer initialUnreadCount2 = initialConversation.getUnreadCount2();
        
        // 增加参与者2的未读数量
        boolean result = conversationDao.incrementUnreadCount(testConversation.getConversationId(), 
                                                             testConversation.getParticipant2Id());
        
        // 验证结果
        assertTrue(result, "增加未读数量应成功");
        
        Conversation updatedConversation = conversationDao.findConversationById(testConversation.getConversationId());
        assertEquals(initialUnreadCount2 + 1, updatedConversation.getUnreadCount2(), 
                    "参与者2的未读数量应增加1");
        assertEquals(initialConversation.getUnreadCount1(), updatedConversation.getUnreadCount1(), 
                    "参与者1的未读数量应保持不变");
    }
    
    @Test
    @DisplayName("逻辑删除会话 - 成功")
    void testDeleteConversation_Success() {
        // 插入会话
        conversationDao.insertConversation(testConversation);
        
        // 逻辑删除
        boolean result = conversationDao.deleteConversation(testConversation.getConversationId());
        
        // 验证结果
        assertTrue(result, "删除应成功");
        
        // 验证会话无法通过正常查询找到（因为查询会过滤已删除的会话）
        Conversation deletedConversation = conversationDao.findConversationById(testConversation.getConversationId());
        assertNull(deletedConversation, "已删除的会话应无法查询到");
    }
    
    @Test
    @DisplayName("逻辑删除会话 - 会话不存在")
    void testDeleteConversation_NotFound() {
        // 删除不存在的会话
        boolean result = conversationDao.deleteConversation("non_existent_conversation");
        
        // 验证结果
        assertFalse(result, "删除不存在的会话应返回false");
    }
    
    @Test
    @DisplayName("查询用户会话列表 - 分页测试")
    void testFindConversationsByUserId_Pagination() {
        Long userId = 1L;
        String uniquePrefix = "pagination_test_" + System.currentTimeMillis();
        
        // 插入5个会话
        for (int i = 1; i <= 5; i++) {
            Conversation conversation = new Conversation();
            conversation.setConversationId(uniquePrefix + "_" + i);
            conversation.setParticipant1Id(userId);
            conversation.setParticipant2Id((long) (i + 40));
            conversation.setLastMessage("分页测试会话 " + i);
            conversation.setLastMessageTime(LocalDateTime.now().minusMinutes(i));
            conversation.setStatus("ACTIVE");
            conversation.setDeleted(false);
            
            conversationDao.insertConversation(conversation);
        }
        
        // 查询第一页（前2个）
        List<Conversation> firstPage = conversationDao.findConversationsByUserId(userId, null, null, 0, 2);
        assertEquals(2, firstPage.size(), "第一页应有2个会话");
        
        // 查询第二页（第3-4个）
        List<Conversation> secondPage = conversationDao.findConversationsByUserId(userId, null, null, 2, 2);
        assertEquals(2, secondPage.size(), "第二页应有2个会话");
        
        // 查询第三页（第5个）
        List<Conversation> thirdPage = conversationDao.findConversationsByUserId(userId, null, null, 4, 2);
        assertTrue(thirdPage.size() >= 1, "第三页应至少有1个会话");
    }
}
