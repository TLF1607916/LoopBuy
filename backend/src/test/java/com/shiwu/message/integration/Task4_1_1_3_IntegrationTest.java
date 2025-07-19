package com.shiwu.message.integration;

import com.shiwu.common.result.Result;
import com.shiwu.message.dao.ConversationDao;
import com.shiwu.message.dao.MessageDao;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.model.Conversation;
import com.shiwu.message.model.Message;
import com.shiwu.message.service.MessageService;
import com.shiwu.message.service.impl.MessageServiceImpl;
import com.shiwu.message.vo.ConversationVO;
import com.shiwu.message.vo.MessageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Task4_1_1_3集成测试
 * 
 * 专门测试Task4_1_1_3的核心需求：
 * 1. 发送消息的API功能
 * 2. 会话与商品ID、买卖双方ID的绑定机制
 * 3. 不跨模块修改，不修改数据表
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
@DisplayName("Task4_1_1_3 - 发送消息API和会话绑定集成测试")
class Task4_1_1_3_IntegrationTest {
    
    @Mock
    private MessageDao messageDao;
    
    @Mock
    private ConversationDao conversationDao;
    
    private MessageService messageService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageServiceImpl(messageDao, conversationDao);
    }
    
    @Test
    @DisplayName("核心功能1: 发送消息API - 创建新会话并绑定商品ID和买卖双方ID")
    void testSendMessageAPI_CreateConversationWithProductBinding() {
        // 准备测试数据 - 模拟买家向卖家咨询商品
        Long buyerId = 1L;      // 买家ID
        Long sellerId = 2L;     // 卖家ID  
        Long productId = 100L;  // 商品ID
        
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(sellerId);
        dto.setContent("你好，请问这个商品还有库存吗？价格可以商量吗？");
        dto.setMessageType("TEXT");
        dto.setProductId(productId);
        
        // Mock数据库操作 - 会话不存在，需要创建新会话
        String expectedConversationId = "1_2_100"; // 买家ID_卖家ID_商品ID
        when(conversationDao.findConversationById(expectedConversationId)).thenReturn(null);
        when(conversationDao.insertConversation(any(Conversation.class))).thenReturn(1L);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(1L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.incrementUnreadCount(anyString(), anyLong())).thenReturn(true);
        
        // 执行测试
        Result<MessageVO> result = messageService.sendMessage(buyerId, dto);
        
        // 验证结果
        assertTrue(result.isSuccess(), "发送消息应该成功");
        assertNotNull(result.getData(), "返回的消息数据不应为空");
        
        MessageVO messageVO = result.getData();
        assertEquals(expectedConversationId, messageVO.getConversationId(), "会话ID应该正确绑定买卖双方和商品");
        assertEquals(buyerId, messageVO.getSenderId(), "发送者应该是买家");
        assertEquals(sellerId, messageVO.getReceiverId(), "接收者应该是卖家");
        assertEquals(productId, messageVO.getProductId(), "应该正确绑定商品ID");
        assertEquals("TEXT", messageVO.getMessageType(), "消息类型应该正确");
        assertEquals("你好，请问这个商品还有库存吗？价格可以商量吗？", messageVO.getContent(), "消息内容应该正确");
        
        // 验证会话创建
        verify(conversationDao).insertConversation(argThat(conversation -> 
            conversation.getConversationId().equals(expectedConversationId) &&
            conversation.getParticipant1Id().equals(buyerId) &&
            conversation.getParticipant2Id().equals(sellerId) &&
            conversation.getProductId().equals(productId)
        ));
        
        // 验证消息插入
        verify(messageDao).insertMessage(argThat(message ->
            message.getConversationId().equals(expectedConversationId) &&
            message.getSenderId().equals(buyerId) &&
            message.getReceiverId().equals(sellerId) &&
            message.getProductId().equals(productId) &&
            message.getContent().equals("你好，请问这个商品还有库存吗？价格可以商量吗？")
        ));
    }
    
    @Test
    @DisplayName("核心功能2: 会话ID生成算法 - 确保买卖双方ID顺序一致性")
    void testConversationIdGeneration_BuyerSellerOrderConsistency() {
        // 测试场景1: 买家ID < 卖家ID
        Long buyerId1 = 1L;
        Long sellerId1 = 5L;
        Long productId1 = 200L;
        
        MessageSendDTO dto1 = new MessageSendDTO(sellerId1, "消息内容1", productId1);
        
        when(conversationDao.findConversationById("1_5_200")).thenReturn(null);
        when(conversationDao.insertConversation(any(Conversation.class))).thenReturn(1L);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(1L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.incrementUnreadCount(anyString(), anyLong())).thenReturn(true);
        
        Result<MessageVO> result1 = messageService.sendMessage(buyerId1, dto1);
        assertTrue(result1.isSuccess());
        assertEquals("1_5_200", result1.getData().getConversationId());
        
        // 测试场景2: 卖家ID < 买家ID (反向发送消息)
        Long buyerId2 = 8L;
        Long sellerId2 = 3L;
        Long productId2 = 200L;
        
        MessageSendDTO dto2 = new MessageSendDTO(buyerId2, "消息内容2", productId2);
        
        when(conversationDao.findConversationById("3_8_200")).thenReturn(null);
        
        Result<MessageVO> result2 = messageService.sendMessage(sellerId2, dto2);
        assertTrue(result2.isSuccess());
        assertEquals("3_8_200", result2.getData().getConversationId());
        
        // 验证：无论谁先发消息，相同的买卖双方和商品组合应该生成相同的会话ID格式
        // 即：较小的用户ID_较大的用户ID_商品ID
    }
    
    @Test
    @DisplayName("核心功能3: 多商品会话隔离 - 同一买卖双方不同商品应创建不同会话")
    void testMultiProductConversationIsolation() {
        Long buyerId = 1L;
        Long sellerId = 2L;
        Long productId1 = 100L;
        Long productId2 = 200L;
        
        // 发送关于商品1的消息
        MessageSendDTO dto1 = new MessageSendDTO(sellerId, "询问商品1", productId1);
        when(conversationDao.findConversationById("1_2_100")).thenReturn(null);
        when(conversationDao.insertConversation(any(Conversation.class))).thenReturn(1L);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(1L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.incrementUnreadCount(anyString(), anyLong())).thenReturn(true);
        
        Result<MessageVO> result1 = messageService.sendMessage(buyerId, dto1);
        assertTrue(result1.isSuccess());
        assertEquals("1_2_100", result1.getData().getConversationId());
        
        // 发送关于商品2的消息
        MessageSendDTO dto2 = new MessageSendDTO(sellerId, "询问商品2", productId2);
        when(conversationDao.findConversationById("1_2_200")).thenReturn(null);
        
        Result<MessageVO> result2 = messageService.sendMessage(buyerId, dto2);
        assertTrue(result2.isSuccess());
        assertEquals("1_2_200", result2.getData().getConversationId());
        
        // 验证：同一买卖双方的不同商品会话应该是独立的
        assertNotEquals(result1.getData().getConversationId(), result2.getData().getConversationId());
        
        // 验证创建了两个不同的会话
        verify(conversationDao, times(2)).insertConversation(any(Conversation.class));
    }
    
    @Test
    @DisplayName("核心功能4: 现有会话消息追加 - 相同买卖双方和商品的后续消息应加入现有会话")
    void testExistingConversationMessageAppend() {
        Long buyerId = 1L;
        Long sellerId = 2L;
        Long productId = 100L;
        String conversationId = "1_2_100";
        
        // 模拟已存在的会话
        Conversation existingConversation = new Conversation();
        existingConversation.setId(1L);
        existingConversation.setConversationId(conversationId);
        existingConversation.setParticipant1Id(buyerId);
        existingConversation.setParticipant2Id(sellerId);
        existingConversation.setProductId(productId);
        existingConversation.setUnreadCount1(0);
        existingConversation.setUnreadCount2(1);
        existingConversation.setStatus("ACTIVE");
        
        when(conversationDao.findConversationById(conversationId)).thenReturn(existingConversation);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(2L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.incrementUnreadCount(anyString(), anyLong())).thenReturn(true);
        
        // 发送后续消息
        MessageSendDTO dto = new MessageSendDTO(sellerId, "这是后续消息", productId);
        Result<MessageVO> result = messageService.sendMessage(buyerId, dto);
        
        // 验证结果
        assertTrue(result.isSuccess());
        assertEquals(conversationId, result.getData().getConversationId());
        
        // 验证：不应该创建新会话，只插入新消息
        verify(conversationDao, never()).insertConversation(any(Conversation.class));
        verify(messageDao).insertMessage(argThat(message ->
            message.getConversationId().equals(conversationId) &&
            message.getContent().equals("这是后续消息")
        ));
    }
    
    @Test
    @DisplayName("核心功能5: 无商品关联的普通会话")
    void testNormalConversationWithoutProduct() {
        Long user1Id = 1L;
        Long user2Id = 3L;
        
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(user2Id);
        dto.setContent("普通聊天消息，不关联商品");
        dto.setMessageType("TEXT");
        dto.setProductId(null); // 无商品关联
        
        String expectedConversationId = "1_3"; // 无商品ID的会话格式
        when(conversationDao.findConversationById(expectedConversationId)).thenReturn(null);
        when(conversationDao.insertConversation(any(Conversation.class))).thenReturn(1L);
        when(messageDao.insertMessage(any(Message.class))).thenReturn(1L);
        when(conversationDao.updateLastMessage(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(conversationDao.incrementUnreadCount(anyString(), anyLong())).thenReturn(true);
        
        Result<MessageVO> result = messageService.sendMessage(user1Id, dto);
        
        assertTrue(result.isSuccess());
        assertEquals(expectedConversationId, result.getData().getConversationId());
        assertNull(result.getData().getProductId());
        
        // 验证会话创建时商品ID为null
        verify(conversationDao).insertConversation(argThat(conversation ->
            conversation.getConversationId().equals(expectedConversationId) &&
            conversation.getProductId() == null
        ));
    }
    
    @Test
    @DisplayName("边界测试: 参数验证和错误处理")
    void testParameterValidationAndErrorHandling() {
        Long senderId = 1L;
        
        // 测试1: 接收者ID为空
        MessageSendDTO dto1 = new MessageSendDTO();
        dto1.setReceiverId(null);
        dto1.setContent("测试消息");
        
        Result<MessageVO> result1 = messageService.sendMessage(senderId, dto1);
        assertFalse(result1.isSuccess());
        assertTrue(result1.getMessage().contains("接收者ID和消息内容不能为空"));

        // 测试2: 消息内容为空
        MessageSendDTO dto2 = new MessageSendDTO();
        dto2.setReceiverId(2L);
        dto2.setContent("");

        Result<MessageVO> result2 = messageService.sendMessage(senderId, dto2);
        assertFalse(result2.isSuccess());
        assertTrue(result2.getMessage().contains("接收者ID和消息内容不能为空"));
        
        // 测试3: 发送者和接收者相同
        MessageSendDTO dto3 = new MessageSendDTO();
        dto3.setReceiverId(senderId);
        dto3.setContent("自己给自己发消息");
        
        Result<MessageVO> result3 = messageService.sendMessage(senderId, dto3);
        assertFalse(result3.isSuccess());
        assertTrue(result3.getMessage().contains("不能给自己发送消息"));
    }
}
