package com.shiwu.message.service;

import com.shiwu.common.result.Result;
import com.shiwu.message.dao.ConversationDao;
import com.shiwu.message.dao.MessageDao;
import com.shiwu.message.dto.MessagePollDTO;
import com.shiwu.message.model.Message;
import com.shiwu.message.service.impl.RealtimeMessageServiceImpl;
import com.shiwu.message.vo.MessagePollVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 实时消息服务测试类
 * 
 * 测试实时消息服务的所有功能
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
@DisplayName("实时消息服务测试")
class RealtimeMessageServiceTest {
    
    @Mock
    private MessageDao messageDao;
    
    @Mock
    private ConversationDao conversationDao;
    
    @Mock
    private MessageService messageService;
    
    private RealtimeMessageService realtimeMessageService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        realtimeMessageService = new RealtimeMessageServiceImpl(messageDao, conversationDao, messageService);
    }
    
    @Test
    @DisplayName("轮询新消息 - 有新消息")
    void testPollNewMessagesWithNewMessages() {
        // 准备测试数据
        Long userId = 1L;
        MessagePollDTO pollDTO = new MessagePollDTO();
        pollDTO.setLastMessageTime(System.currentTimeMillis() - 60000); // 1分钟前
        
        Message newMessage = new Message();
        newMessage.setId(1L);
        newMessage.setReceiverId(userId);
        newMessage.setContent("新消息");
        newMessage.setCreateTime(LocalDateTime.now());
        
        List<Message> newMessages = Arrays.asList(newMessage);
        
        // Mock行为
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(newMessages);
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(3);
        
        // 执行测试
        Result<MessagePollVO> result = realtimeMessageService.pollNewMessages(userId, pollDTO);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getHasNewMessages());
        assertEquals(1, result.getData().getNewMessages().size());
        assertEquals(3, result.getData().getTotalUnreadCount());
        assertEquals("新消息", result.getData().getNewMessages().get(0).getContent());
    }
    
    @Test
    @DisplayName("轮询新消息 - 无新消息")
    void testPollNewMessagesWithoutNewMessages() {
        // 准备测试数据
        Long userId = 1L;
        MessagePollDTO pollDTO = new MessagePollDTO();
        pollDTO.setLastMessageTime(System.currentTimeMillis() - 60000);
        
        // Mock行为 - 无新消息
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(0);
        
        // 执行测试
        Result<MessagePollVO> result = realtimeMessageService.pollNewMessages(userId, pollDTO);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().getHasNewMessages());
        assertEquals(0, result.getData().getNewMessages().size());
        assertEquals(0, result.getData().getTotalUnreadCount());
    }
    
    @Test
    @DisplayName("轮询新消息 - 参数为空")
    void testPollNewMessagesWithNullUserId() {
        // 执行测试
        Result<MessagePollVO> result = realtimeMessageService.pollNewMessages(null, new MessagePollDTO());
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals("用户ID不能为空", result.getMessage());
    }
    
    @Test
    @DisplayName("轮询新消息 - 默认参数")
    void testPollNewMessagesWithDefaultParams() {
        // 准备测试数据
        Long userId = 1L;
        
        // Mock行为
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(0);
        
        // 执行测试 - 传入null参数
        Result<MessagePollVO> result = realtimeMessageService.pollNewMessages(userId, null);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        // 验证使用了默认的时间参数（最近1分钟）
        verify(messageDao).findNewMessagesByUserId(eq(userId), any(LocalDateTime.class));
    }
    
    @Test
    @DisplayName("长轮询新消息 - 立即有新消息")
    void testLongPollNewMessagesImmediateResponse() {
        // 准备测试数据
        Long userId = 1L;
        MessagePollDTO pollDTO = new MessagePollDTO();
        pollDTO.setLastMessageTime(System.currentTimeMillis() - 60000);
        
        Message newMessage = new Message();
        newMessage.setId(1L);
        newMessage.setReceiverId(userId);
        newMessage.setContent("新消息");
        newMessage.setCreateTime(LocalDateTime.now());
        
        List<Message> newMessages = Arrays.asList(newMessage);
        
        // Mock行为 - 立即有新消息
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(newMessages);
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(1);
        
        // 执行测试
        long startTime = System.currentTimeMillis();
        Result<MessagePollVO> result = realtimeMessageService.longPollNewMessages(userId, pollDTO, 30);
        long endTime = System.currentTimeMillis();
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().getHasNewMessages());
        
        // 验证响应时间很快（因为立即有新消息）
        assertTrue(endTime - startTime < 5000); // 应该在5秒内响应
    }
    
    @Test
    @DisplayName("长轮询新消息 - 参数验证")
    void testLongPollNewMessagesParameterValidation() {
        // 测试用户ID为空
        Result<MessagePollVO> result1 = realtimeMessageService.longPollNewMessages(null, new MessagePollDTO(), 30);
        assertFalse(result1.isSuccess());
        assertEquals("用户ID不能为空", result1.getMessage());
        
        // 测试超时时间验证
        Long userId = 1L;
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(0);
        
        // 测试负数超时时间
        Result<MessagePollVO> result2 = realtimeMessageService.longPollNewMessages(userId, new MessagePollDTO(), -1);
        assertTrue(result2.isSuccess()); // 应该使用默认超时时间
        
        // 测试过大的超时时间
        Result<MessagePollVO> result3 = realtimeMessageService.longPollNewMessages(userId, new MessagePollDTO(), 100);
        assertTrue(result3.isSuccess()); // 应该使用默认超时时间
    }
    
    @Test
    @DisplayName("获取用户实时状态 - 成功")
    void testGetUserRealtimeStatusSuccess() {
        // 准备测试数据
        Long userId = 1L;
        int expectedUnreadCount = 5;
        
        // Mock行为
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(expectedUnreadCount);
        
        // 执行测试
        Result<MessagePollVO> result = realtimeMessageService.getUserRealtimeStatus(userId);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(expectedUnreadCount, result.getData().getTotalUnreadCount());
        assertFalse(result.getData().getHasNewMessages());
        assertEquals(0, result.getData().getNewMessages().size());
        assertNotNull(result.getData().getCurrentTime());
    }
    
    @Test
    @DisplayName("获取用户实时状态 - 用户ID为空")
    void testGetUserRealtimeStatusWithNullUserId() {
        // 执行测试
        Result<MessagePollVO> result = realtimeMessageService.getUserRealtimeStatus(null);
        
        // 验证结果
        assertFalse(result.isSuccess());
        assertEquals("用户ID不能为空", result.getMessage());
    }
    
    @Test
    @DisplayName("检查是否有新消息 - 有新消息")
    void testHasNewMessagesTrue() {
        // 准备测试数据
        Long userId = 1L;
        Long lastCheckTime = System.currentTimeMillis() - 60000; // 1分钟前
        
        Message newMessage = new Message();
        newMessage.setId(1L);
        newMessage.setReceiverId(userId);
        newMessage.setCreateTime(LocalDateTime.now());
        
        List<Message> newMessages = Arrays.asList(newMessage);
        
        // Mock行为
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(newMessages);
        
        // 执行测试
        Result<Boolean> result = realtimeMessageService.hasNewMessages(userId, lastCheckTime);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }
    
    @Test
    @DisplayName("检查是否有新消息 - 无新消息")
    void testHasNewMessagesFalse() {
        // 准备测试数据
        Long userId = 1L;
        Long lastCheckTime = System.currentTimeMillis() - 60000;
        
        // Mock行为 - 无新消息
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        
        // 执行测试
        Result<Boolean> result = realtimeMessageService.hasNewMessages(userId, lastCheckTime);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertFalse(result.getData());
    }
    
    @Test
    @DisplayName("检查是否有新消息 - 默认时间参数")
    void testHasNewMessagesWithDefaultTime() {
        // 准备测试数据
        Long userId = 1L;
        
        // Mock行为
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        
        // 执行测试 - 传入null时间
        Result<Boolean> result = realtimeMessageService.hasNewMessages(userId, null);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertFalse(result.getData());
        
        // 验证使用了默认时间（最近1分钟）
        verify(messageDao).findNewMessagesByUserId(eq(userId), any(LocalDateTime.class));
    }
    
    @Test
    @DisplayName("通知新消息")
    void testNotifyNewMessage() {
        // 准备测试数据
        Long userId = 1L;
        Long messageId = 100L;
        
        // 执行测试 - 这个方法目前只是记录日志，不抛异常即为成功
        assertDoesNotThrow(() -> {
            realtimeMessageService.notifyNewMessage(userId, messageId);
        });
        
        // 测试参数为空的情况
        assertDoesNotThrow(() -> {
            realtimeMessageService.notifyNewMessage(null, null);
        });
    }
    
    @Test
    @DisplayName("获取在线用户数量")
    void testGetOnlineUserCount() {
        // 执行测试
        int onlineCount = realtimeMessageService.getOnlineUserCount();
        
        // 验证结果 - 初始状态应该是0或正数
        assertTrue(onlineCount >= 0);
    }
    
    @Test
    @DisplayName("用户活跃时间更新")
    void testUserActiveTimeUpdate() {
        // 通过调用轮询方法来触发用户活跃时间更新
        Long userId = 1L;
        
        // Mock行为
        when(messageDao.findNewMessagesByUserId(eq(userId), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(conversationDao.getTotalUnreadCount(userId)).thenReturn(0);
        
        // 第一次调用
        int initialCount = realtimeMessageService.getOnlineUserCount();
        realtimeMessageService.pollNewMessages(userId, new MessagePollDTO());
        int afterFirstCall = realtimeMessageService.getOnlineUserCount();
        
        // 第二次调用（短时间内）
        realtimeMessageService.pollNewMessages(userId, new MessagePollDTO());
        int afterSecondCall = realtimeMessageService.getOnlineUserCount();
        
        // 验证在线用户数的变化
        assertTrue(afterFirstCall >= initialCount);
        assertEquals(afterFirstCall, afterSecondCall); // 短时间内重复调用不应该增加计数
    }
}
