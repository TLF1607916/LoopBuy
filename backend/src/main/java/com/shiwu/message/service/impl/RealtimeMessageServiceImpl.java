package com.shiwu.message.service.impl;

import com.shiwu.common.result.Result;
import com.shiwu.message.dao.ConversationDao;
import com.shiwu.message.dao.MessageDao;
import com.shiwu.message.dto.MessagePollDTO;
import com.shiwu.message.model.Message;
import com.shiwu.message.service.MessageService;
import com.shiwu.message.service.RealtimeMessageService;
import com.shiwu.message.vo.MessagePollVO;
import com.shiwu.message.vo.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实时消息服务实现类
 * 
 * 基于轮询机制实现实时消息推送
 * 支持短轮询和长轮询两种模式
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
public class RealtimeMessageServiceImpl implements RealtimeMessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessageServiceImpl.class);
    
    // 长轮询的默认超时时间（秒）
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    
    // 轮询间隔（毫秒）
    private static final long POLL_INTERVAL_MS = 1000;
    
    // 在线用户计数器
    private static final AtomicInteger onlineUserCount = new AtomicInteger(0);
    
    // 用户最后活跃时间记录
    private static final ConcurrentHashMap<Long, Long> userLastActiveTime = new ConcurrentHashMap<>();
    
    private final MessageDao messageDao;
    private final ConversationDao conversationDao;
    private final MessageService messageService;
    
    public RealtimeMessageServiceImpl() {
        this.messageDao = new MessageDao();
        this.conversationDao = new ConversationDao();
        this.messageService = new MessageServiceImpl();
    }
    
    // 用于测试的构造函数
    public RealtimeMessageServiceImpl(MessageDao messageDao, ConversationDao conversationDao, MessageService messageService) {
        this.messageDao = messageDao;
        this.conversationDao = conversationDao;
        this.messageService = messageService;
    }
    
    @Override
    public Result<MessagePollVO> pollNewMessages(Long userId, MessagePollDTO pollDTO) {
        try {
            if (userId == null) {
                logger.warn("轮询新消息失败: 用户ID为空");
                return Result.error("用户ID不能为空");
            }
            
            // 更新用户活跃时间
            updateUserActiveTime(userId);
            
            // 设置默认参数
            if (pollDTO == null) {
                pollDTO = new MessagePollDTO();
            }
            
            Long lastMessageTime = pollDTO.getLastMessageTime();
            if (lastMessageTime == null || lastMessageTime <= 0) {
                lastMessageTime = System.currentTimeMillis() - 60000; // 默认获取最近1分钟的消息
            }
            
            // 获取新消息
            LocalDateTime lastTime = LocalDateTime.now().minusSeconds((System.currentTimeMillis() - lastMessageTime) / 1000);
            List<Message> newMessages = messageDao.findNewMessagesByUserId(userId, lastTime);
            
            // 转换为VO
            List<MessageVO> messageVOs = new ArrayList<>();
            for (Message message : newMessages) {
                MessageVO vo = convertToMessageVO(message);
                messageVOs.add(vo);
            }
            
            // 获取总未读数量
            int totalUnreadCount = conversationDao.getTotalUnreadCount(userId);
            
            // 构建响应
            MessagePollVO pollVO = new MessagePollVO();
            pollVO.setNewMessages(messageVOs);
            pollVO.setTotalUnreadCount(totalUnreadCount);
            pollVO.setCurrentTime(System.currentTimeMillis());
            
            logger.debug("轮询新消息成功: userId={}, newCount={}, totalUnread={}", 
                        userId, messageVOs.size(), totalUnreadCount);
            
            return Result.success(pollVO);
            
        } catch (Exception e) {
            logger.error("轮询新消息时发生异常: userId={}", userId, e);
            return Result.error("轮询新消息失败");
        }
    }
    
    @Override
    public Result<MessagePollVO> longPollNewMessages(Long userId, MessagePollDTO pollDTO, int timeoutSeconds) {
        try {
            if (userId == null) {
                logger.warn("长轮询新消息失败: 用户ID为空");
                return Result.error("用户ID不能为空");
            }
            
            if (timeoutSeconds <= 0 || timeoutSeconds > 60) {
                timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
            }
            
            // 更新用户活跃时间
            updateUserActiveTime(userId);
            
            long startTime = System.currentTimeMillis();
            long timeoutMs = timeoutSeconds * 1000L;
            
            // 长轮询循环
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                // 检查是否有新消息
                Result<MessagePollVO> result = pollNewMessages(userId, pollDTO);
                if (result.isSuccess() && result.getData().getHasNewMessages()) {
                    logger.debug("长轮询找到新消息: userId={}, elapsed={}ms", 
                               userId, System.currentTimeMillis() - startTime);
                    return result;
                }
                
                // 等待一段时间后再次检查
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("长轮询被中断: userId={}", userId);
                    break;
                }
                
                // 更新用户活跃时间
                updateUserActiveTime(userId);
            }
            
            // 超时，返回空结果
            logger.debug("长轮询超时: userId={}, timeout={}s", userId, timeoutSeconds);
            return pollNewMessages(userId, pollDTO);
            
        } catch (Exception e) {
            logger.error("长轮询新消息时发生异常: userId={}", userId, e);
            return Result.error("长轮询新消息失败");
        }
    }
    
    @Override
    public Result<MessagePollVO> getUserRealtimeStatus(Long userId) {
        try {
            if (userId == null) {
                logger.warn("获取用户实时状态失败: 用户ID为空");
                return Result.error("用户ID不能为空");
            }
            
            // 更新用户活跃时间
            updateUserActiveTime(userId);
            
            // 获取总未读数量
            int totalUnreadCount = conversationDao.getTotalUnreadCount(userId);
            
            // 构建响应
            MessagePollVO statusVO = new MessagePollVO();
            statusVO.setNewMessages(new ArrayList<>());
            statusVO.setTotalUnreadCount(totalUnreadCount);
            statusVO.setCurrentTime(System.currentTimeMillis());
            statusVO.setHasNewMessages(false);
            
            logger.debug("获取用户实时状态成功: userId={}, totalUnread={}", userId, totalUnreadCount);
            return Result.success(statusVO);
            
        } catch (Exception e) {
            logger.error("获取用户实时状态时发生异常: userId={}", userId, e);
            return Result.error("获取用户实时状态失败");
        }
    }
    
    @Override
    public Result<Boolean> hasNewMessages(Long userId, Long lastCheckTime) {
        try {
            if (userId == null) {
                logger.warn("检查新消息失败: 用户ID为空");
                return Result.error("用户ID不能为空");
            }
            
            if (lastCheckTime == null || lastCheckTime <= 0) {
                lastCheckTime = System.currentTimeMillis() - 60000; // 默认检查最近1分钟
            }
            
            // 更新用户活跃时间
            updateUserActiveTime(userId);
            
            LocalDateTime lastTime = LocalDateTime.now().minusSeconds((System.currentTimeMillis() - lastCheckTime) / 1000);
            List<Message> newMessages = messageDao.findNewMessagesByUserId(userId, lastTime);
            
            boolean hasNew = !newMessages.isEmpty();
            
            logger.debug("检查新消息完成: userId={}, hasNew={}, count={}", 
                        userId, hasNew, newMessages.size());
            
            return Result.success(hasNew);
            
        } catch (Exception e) {
            logger.error("检查新消息时发生异常: userId={}", userId, e);
            return Result.error("检查新消息失败");
        }
    }
    
    @Override
    public void notifyNewMessage(Long userId, Long messageId) {
        try {
            if (userId == null || messageId == null) {
                logger.warn("通知新消息失败: 参数为空");
                return;
            }
            
            // 这里可以实现消息推送逻辑
            // 例如：WebSocket推送、SSE推送等
            // 当前实现中，客户端通过轮询获取新消息
            
            logger.debug("通知新消息: userId={}, messageId={}", userId, messageId);
            
        } catch (Exception e) {
            logger.error("通知新消息时发生异常: userId={}, messageId={}", userId, messageId, e);
        }
    }
    
    @Override
    public int getOnlineUserCount() {
        // 清理过期的用户活跃记录
        cleanupInactiveUsers();
        return onlineUserCount.get();
    }
    
    /**
     * 更新用户活跃时间
     */
    private void updateUserActiveTime(Long userId) {
        Long currentTime = System.currentTimeMillis();
        Long lastTime = userLastActiveTime.put(userId, currentTime);
        
        // 如果是新用户或者用户离线超过5分钟，增加在线用户数
        if (lastTime == null || currentTime - lastTime > 300000) {
            onlineUserCount.incrementAndGet();
            logger.debug("用户上线: userId={}, onlineCount={}", userId, onlineUserCount.get());
        }
    }
    
    /**
     * 清理不活跃的用户
     */
    private void cleanupInactiveUsers() {
        long currentTime = System.currentTimeMillis();
        long inactiveThreshold = 300000; // 5分钟不活跃视为离线
        
        userLastActiveTime.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > inactiveThreshold) {
                onlineUserCount.decrementAndGet();
                logger.debug("用户离线: userId={}, onlineCount={}", entry.getKey(), onlineUserCount.get());
                return true;
            }
            return false;
        });
    }
    
    /**
     * 转换Message为MessageVO
     */
    private MessageVO convertToMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        vo.setMessageId(message.getId());
        vo.setConversationId(message.getConversationId());
        vo.setSenderId(message.getSenderId());
        vo.setReceiverId(message.getReceiverId());
        vo.setProductId(message.getProductId());
        vo.setContent(message.getContent());
        vo.setMessageType(message.getMessageType());
        vo.setIsRead(message.getIsRead());
        vo.setSendTime(message.getCreateTime());
        return vo;
    }
}
