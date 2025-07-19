package com.shiwu.message.dao;

import com.shiwu.message.model.Message;
import com.shiwu.common.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息数据访问对象
 * 
 * 严格遵循项目规范：
 * 1. 使用DBUtil获取连接
 * 2. 不使用物理外键，在应用层管理关系
 * 3. 支持逻辑删除
 * 4. 所有方法都要处理异常
 * 
 * @author Shiwu Team
 * @version 1.0
 */
public class MessageDao {
    
    /**
     * 插入新消息
     */
    public Long insertMessage(Message message) {
        String sql = "INSERT INTO message (conversation_id, sender_id, receiver_id, product_id, " +
                    "content, message_type, is_read, create_time, update_time, is_deleted) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, message.getConversationId());
            stmt.setLong(2, message.getSenderId());
            stmt.setLong(3, message.getReceiverId());
            if (message.getProductId() != null) {
                stmt.setLong(4, message.getProductId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }
            stmt.setString(5, message.getContent());
            stmt.setString(6, message.getMessageType());
            stmt.setBoolean(7, message.getRead() != null ? message.getRead() : false);
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            stmt.setBoolean(10, message.getDeleted() != null ? message.getDeleted() : false);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("插入消息失败，没有行被影响");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("插入消息失败，无法获取生成的ID");
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("插入消息时发生数据库错误", e);
        }
    }
    
    /**
     * 根据会话ID查询消息列表（分页）
     */
    public List<Message> findMessagesByConversationId(String conversationId, Integer offset, Integer limit) {
        String sql = "SELECT id, conversation_id, sender_id, receiver_id, product_id, " +
                    "content, message_type, is_read, create_time, update_time, is_deleted " +
                    "FROM message " +
                    "WHERE conversation_id = ? AND is_deleted = 0 " +
                    "ORDER BY create_time ASC " +
                    "LIMIT ? OFFSET ?";
        
        List<Message> messages = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conversationId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("查询消息列表时发生数据库错误", e);
        }
        
        return messages;
    }
    
    /**
     * 根据ID查询消息
     */
    public Message findMessageById(Long messageId) {
        String sql = "SELECT id, conversation_id, sender_id, receiver_id, product_id, " +
                    "content, message_type, is_read, create_time, update_time, is_deleted " +
                    "FROM message " +
                    "WHERE id = ? AND is_deleted = 0";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, messageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMessage(rs);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("查询消息时发生数据库错误", e);
        }
        
        return null;
    }
    
    /**
     * 标记消息为已读
     */
    public boolean markMessageAsRead(Long messageId) {
        String sql = "UPDATE message SET is_read = 1, update_time = ? WHERE id = ? AND is_deleted = 0";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, messageId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("标记消息已读时发生数据库错误", e);
        }
    }
    
    /**
     * 批量标记会话中的消息为已读
     */
    public int markConversationMessagesAsRead(String conversationId, Long userId) {
        String sql = "UPDATE message SET is_read = 1, update_time = ? " +
                    "WHERE conversation_id = ? AND receiver_id = ? AND is_read = 0 AND is_deleted = 0";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, conversationId);
            stmt.setLong(3, userId);
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("批量标记消息已读时发生数据库错误", e);
        }
    }
    
    /**
     * 统计会话中的未读消息数量
     */
    public Integer countUnreadMessages(String conversationId, Long userId) {
        String sql = "SELECT COUNT(*) FROM message " +
                    "WHERE conversation_id = ? AND receiver_id = ? AND is_read = 0 AND is_deleted = 0";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conversationId);
            stmt.setLong(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("统计未读消息数量时发生数据库错误", e);
        }
        
        return 0;
    }
    
    /**
     * 逻辑删除消息
     */
    public boolean deleteMessage(Long messageId) {
        String sql = "UPDATE message SET is_deleted = 1, update_time = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, messageId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("删除消息时发生数据库错误", e);
        }
    }
    
    /**
     * 将ResultSet映射为Message对象
     */
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setConversationId(rs.getString("conversation_id"));
        message.setSenderId(rs.getLong("sender_id"));
        message.setReceiverId(rs.getLong("receiver_id"));
        
        Long productId = rs.getLong("product_id");
        if (!rs.wasNull()) {
            message.setProductId(productId);
        }
        
        message.setContent(rs.getString("content"));
        message.setMessageType(rs.getString("message_type"));
        message.setRead(rs.getBoolean("is_read"));
        message.setDeleted(rs.getBoolean("is_deleted"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            message.setCreateTime(createTime.toLocalDateTime());
        }
        
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            message.setUpdateTime(updateTime.toLocalDateTime());
        }
        
        return message;
    }
}
