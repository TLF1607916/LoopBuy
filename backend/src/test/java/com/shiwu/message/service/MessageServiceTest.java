package com.shiwu.message.service;

import com.shiwu.common.result.Result;
import com.shiwu.message.dao.ConversationDao;
import com.shiwu.message.dao.MessageDao;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.model.Conversation;
import com.shiwu.message.model.Message;
import com.shiwu.message.service.impl.MessageServiceImpl;
import com.shiwu.message.vo.ConversationVO;
import com.shiwu.message.vo.MessageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 消息服务测试类
 * 
 * 测试消息服务的所有功能
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
@DisplayName("消息服务测试")
class MessageServiceTest {
    
    @Mock
    private MessageDao messageDao;
    
    @Mock
    private ConversationDao conversationDao;
    
    private MessageService messageService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageServiceImpl(messageDao, conversationDao, null, null);
    }
    
    @Test
    @DisplayName("发送消息 - 成功")
    void testSendMessageSuccess() {
        // 准备测试数据
        Long senderId = 1L;
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(2L);
        dto.setContent("测试消息");
        dto.setMessageType("TEXT");
        dto.setProductId(100L);
        
        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setConversationId("1_2_100");
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        conversation.setUnreadCount1(0);
        conversation.setUnreadCount2(0);
        
        // Mock行为
        when(conversationDao.findConversationById("1_2_100")).thenReturn(conversation);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(1L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.updateUnreadCount(anyString(), anyLong(), anyInt())).thenReturn(true);
        
        // 执行测试
        Result<MessageVO> result = messageService.sendMessage(senderId, dto);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("测试消息", result.getData().getContent());
        assertEquals(senderId, result.getData().getSenderId());
        assertEquals(dto.getReceiverId(), result.getData().getReceiverId());
        
        // 验证方法调用
        verify(messageDao).insertMessage(any(Message.class));
        verify(conversationDao).updateLastMessage(anyString(), anyString(), any(LocalDateTime.class));
    }
    
    @Test
    @DisplayName("发送消息 - 参数为空")
    void testSendMessageWithNullParams() {
        // 测试senderId为空
        Result<MessageVO> result1 = messageService.sendMessage(null, new MessageSendDTO());
        assertFalse(result1.isSuccess());
        assertEquals("参数不能为空", result1.getMessage());
        
        // 测试dto为空
        Result<MessageVO> result2 = messageService.sendMessage(1L, null);
        assertFalse(result2.isSuccess());
        assertEquals("参数不能为空", result2.getMessage());
    }
    
    @Test
    @DisplayName("发送消息 - 给自己发送")
    void testSendMessageToSelf() {
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(1L);
        dto.setContent("测试消息");
        
        Result<MessageVO> result = messageService.sendMessage(1L, dto);
        
        assertFalse(result.isSuccess());
        assertEquals("不能给自己发送消息", result.getMessage());
    }
    
    @Test
    @DisplayName("发送消息 - 创建新会话")
    void testSendMessageCreateNewConversation() {
        // 准备测试数据
        Long senderId = 1L;
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(2L);
        dto.setContent("测试消息");
        dto.setProductId(100L);
        
        // Mock行为 - 会话不存在
        when(conversationDao.findConversationById("1_2_100")).thenReturn(null);
        when(conversationDao.insertConversation(any(Conversation.class))).thenReturn(1L);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(1L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.updateUnreadCount(anyString(), anyLong(), anyInt())).thenReturn(true);
        
        // 执行测试
        Result<MessageVO> result = messageService.sendMessage(senderId, dto);
        
        // 验证结果
        assertTrue(result.isSuccess());
        
        // 验证创建了新会话
        verify(conversationDao).insertConversation(any(Conversation.class));
    }
    
    @Test
    @DisplayName("获取会话列表 - 成功")
    void testGetConversationsSuccess() {
        // 准备测试数据
        Long userId = 1L;
        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setConversationId("1_2_100");
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        conversation.setLastMessage("最后消息");
        conversation.setUnreadCount1(2);
        conversation.setUnreadCount2(0);
        
        List<Conversation> conversations = Arrays.asList(conversation);
        
        // Mock行为
        when(conversationDao.findConversationsByUserId(userId, null, null, 0, 20)).thenReturn(conversations);
        
        // 执行测试
        Result<List<ConversationVO>> result = messageService.getConversations(userId, 1, 20);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConversationVO vo = result.getData().get(0);
        assertEquals("1_2_100", vo.getConversationId());
        assertEquals("最后消息", vo.getLastMessage());
        assertEquals(2, vo.getUnreadCount()); // 用户1的未读数量
    }
    
    @Test
    @DisplayName("获取消息历史 - 成功")
    void testGetMessageHistorySuccess() {
        // 准备测试数据
        Long userId = 1L;
        String conversationId = "1_2_100";
        
        Conversation conversation = new Conversation();
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        
        Message message = new Message();
        message.setId(1L);
        message.setConversationId(conversationId);
        message.setSenderId(2L);
        message.setReceiverId(1L);
        message.setContent("测试消息");
        message.setCreateTime(LocalDateTime.now());
        
        List<Message> messages = Arrays.asList(message);
        
        // Mock行为
        when(conversationDao.findConversationById(conversationId)).thenReturn(conversation);
        when(messageDao.findMessagesByConversationId(conversationId, 0, 50)).thenReturn(messages);
        
        // 执行测试
        Result<List<MessageVO>> result = messageService.getMessageHistory(userId, conversationId, 1, 50);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        MessageVO vo = result.getData().get(0);
        assertEquals(conversationId, vo.getConversationId());
        assertEquals("测试消息", vo.getContent());
    }
    
    @Test
    @DisplayName("获取消息历史 - 无权限")
    void testGetMessageHistoryNoPermission() {
        // 准备测试数据
        Long userId = 3L; // 不是会话参与者
        String conversationId = "1_2_100";
        
        Conversation conversation = new Conversation();
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        
        // Mock行为
        when(conversationDao.findConversationById(conversationId)).thenReturn(conversation);
        
        // 执行测试
        Result<List<MessageVO>> result = messageService.getMessageHistory(userId, conversationId, 1, 50);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals("无权限访问该会话", result.getMessage());
    }
    
    @Test
    @DisplayName("标记消息已读 - 成功")
    void testMarkMessagesAsReadSuccess() {
        // 准备测试数据
        Long userId = 1L;
        String conversationId = "1_2_100";
        
        Conversation conversation = new Conversation();
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        
        // Mock行为
        when(conversationDao.findConversationById(conversationId)).thenReturn(conversation);
        when(messageDao.markMessagesAsRead(conversationId, userId)).thenReturn(true);
        when(conversationDao.updateUnreadCount(conversationId, userId, 0)).thenReturn(true);
        
        // 执行测试
        Result<Void> result = messageService.markMessagesAsRead(userId, conversationId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        
        // 验证方法调用
        verify(messageDao).markMessagesAsRead(conversationId, userId);
        verify(conversationDao).updateUnreadCount(conversationId, userId, 0);
    }
    
    @Test
    @DisplayName("获取新消息 - 成功")
    void testGetNewMessagesSuccess() {
        // 准备测试数据
        Long userId = 1L;
        Long lastMessageTime = System.currentTimeMillis() - 60000; // 1分钟前
        
        Message message = new Message();
        message.setId(1L);
        message.setReceiverId(userId);
        message.setContent("新消息");
        message.setCreateTime(LocalDateTime.now());
        
        List<Message> newMessages = Arrays.asList(message);
        
        // Mock行为
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(newMessages);
        
        // 执行测试
        Result<List<MessageVO>> result = messageService.getNewMessages(userId, lastMessageTime);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("新消息", result.getData().get(0).getContent());
    }
    
    @Test
    @DisplayName("获取未读消息数量 - 成功")
    void testGetUnreadMessageCountSuccess() {
        // 准备测试数据
        Long userId = 1L;
        int expectedCount = 5;
        
        // Mock行为
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(expectedCount);
        
        // 执行测试
        Result<Integer> result = messageService.getUnreadMessageCount(userId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals(expectedCount, result.getData());
    }
    
    @Test
    @DisplayName("创建或获取会话 - 成功")
    void testGetOrCreateConversationSuccess() {
        // 准备测试数据
        Long participant1Id = 1L;
        Long participant2Id = 2L;
        Long productId = 100L;
        
        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setConversationId("1_2_100");
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        conversation.setProductId(100L);
        
        // Mock行为
        when(conversationDao.findConversationById("1_2_100")).thenReturn(conversation);
        
        // 执行测试
        Result<ConversationVO> result = messageService.getOrCreateConversation(participant1Id, participant2Id, productId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("1_2_100", result.getData().getConversationId());
    }
    
    @Test
    @DisplayName("检查会话权限 - 有权限")
    void testHasConversationPermissionTrue() {
        // 准备测试数据
        Long userId = 1L;
        String conversationId = "1_2_100";
        
        Conversation conversation = new Conversation();
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        
        // Mock行为
        when(conversationDao.findConversationById(conversationId)).thenReturn(conversation);
        
        // 执行测试
        boolean hasPermission = messageService.hasConversationPermission(userId, conversationId);
        
        // 验证结果
        assertTrue(hasPermission);
    }
    
    @Test
    @DisplayName("检查会话权限 - 无权限")
    void testHasConversationPermissionFalse() {
        // 准备测试数据
        Long userId = 3L; // 不是会话参与者
        String conversationId = "1_2_100";
        
        Conversation conversation = new Conversation();
        conversation.setParticipant1Id(1L);
        conversation.setParticipant2Id(2L);
        
        // Mock行为
        when(conversationDao.findConversationById(conversationId)).thenReturn(conversation);
        
        // 执行测试
        boolean hasPermission = messageService.hasConversationPermission(userId, conversationId);
        
        // 验证结果
        assertFalse(hasPermission);
    }
}
