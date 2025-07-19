package com.shiwu.message.service;

import com.shiwu.common.result.Result;
import com.shiwu.message.dto.MessagePollDTO;
import com.shiwu.message.vo.MessagePollVO;

/**
 * 实时消息服务接口
 * 
 * 提供基于轮询的实时消息推送功能
 * 支持长轮询和短轮询两种模式
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public interface RealtimeMessageService {
    
    /**
     * 轮询获取新消息
     * 
     * @param userId 用户ID
     * @param pollDTO 轮询参数
     * @return 轮询结果，包含新消息和状态信息
     */
    Result<MessagePollVO> pollNewMessages(Long userId, MessagePollDTO pollDTO);
    
    /**
     * 长轮询获取新消息
     * 支持在指定时间内等待新消息
     * 
     * @param userId 用户ID
     * @param pollDTO 轮询参数
     * @param timeoutSeconds 超时时间（秒）
     * @return 轮询结果
     */
    Result<MessagePollVO> longPollNewMessages(Long userId, MessagePollDTO pollDTO, int timeoutSeconds);
    
    /**
     * 获取用户的实时状态信息
     * 
     * @param userId 用户ID
     * @return 状态信息，包含未读消息数量等
     */
    Result<MessagePollVO> getUserRealtimeStatus(Long userId);
    
    /**
     * 检查用户是否有新消息
     *
     * @param userId 用户ID
     * @param lastCheckTime 上次检查时间戳（毫秒）
     * @return 是否有新消息
     */
    Result<Boolean> hasNewMessages(Long userId, Long lastCheckTime);

    /**
     * 通知用户有新消息（用于消息发送后的实时推送）
     *
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void notifyNewMessage(Long userId, Long messageId);

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    int getOnlineUserCount();
}
