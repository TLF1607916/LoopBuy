package com.shiwu.message.service;

import com.shiwu.common.result.Result;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.vo.MessageVO;
import com.shiwu.message.vo.ConversationVO;

import java.util.List;

/**
 * 消息服务接口
 * 
 * 提供实时消息收发、会话管理等功能
 * 支持基于轮询的实时消息推送
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public interface MessageService {
    
    /**
     * 发送消息
     * 
     * @param senderId 发送者ID
     * @param dto 消息发送DTO
     * @return 发送结果，包含消息信息
     */
    Result<MessageVO> sendMessage(Long senderId, MessageSendDTO dto);
    
    /**
     * 获取用户的会话列表
     * 
     * @param userId 用户ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 会话列表
     */
    Result<List<ConversationVO>> getConversations(Long userId, int page, int size);
    
    /**
     * 获取会话的消息历史
     * 
     * @param userId 用户ID（用于权限验证）
     * @param conversationId 会话ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 消息列表
     */
    Result<List<MessageVO>> getMessageHistory(Long userId, String conversationId, int page, int size);
    
    /**
     * 标记消息为已读
     * 
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 操作结果
     */
    Result<Void> markMessagesAsRead(Long userId, String conversationId);
    
    /**
     * 获取用户的新消息（用于轮询）
     * 
     * @param userId 用户ID
     * @param lastMessageTime 上次获取消息的时间戳（毫秒）
     * @return 新消息列表
     */
    Result<List<MessageVO>> getNewMessages(Long userId, Long lastMessageTime);
    
    /**
     * 获取用户的未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Result<Integer> getUnreadMessageCount(Long userId);
    
    /**
     * 创建或获取会话
     * 
     * @param participant1Id 参与者1 ID
     * @param participant2Id 参与者2 ID
     * @param productId 商品ID（可选）
     * @return 会话信息
     */
    Result<ConversationVO> getOrCreateConversation(Long participant1Id, Long participant2Id, Long productId);
    
    /**
     * 检查用户是否有权限访问会话
     * 
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 是否有权限
     */
    boolean hasConversationPermission(Long userId, String conversationId);
    
    /**
     * 获取会话详情
     * 
     * @param userId 用户ID（用于权限验证）
     * @param conversationId 会话ID
     * @return 会话详情
     */
    Result<ConversationVO> getConversationDetail(Long userId, String conversationId);
    
    /**
     * 更新会话状态
     * 
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @param status 新状态
     * @return 操作结果
     */
    Result<Void> updateConversationStatus(Long userId, String conversationId, String status);
}
