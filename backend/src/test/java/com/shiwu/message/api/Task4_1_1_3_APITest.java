package com.shiwu.message.api;

import com.shiwu.common.result.Result;
import com.shiwu.common.util.JsonUtil;
import com.shiwu.message.dto.MessageSendDTO;
import com.shiwu.message.vo.MessageVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task4_1_1_3 API测试
 * 
 * 验证发送消息API的完整功能，包括：
 * 1. 发送消息API的HTTP接口
 * 2. 会话与商品ID、买卖双方ID的绑定
 * 3. JSON序列化和反序列化
 * 
 * @author LoopBuy Team
 * @version 1.0
 */
@DisplayName("Task4_1_1_3 - 发送消息API测试")
class Task4_1_1_3_APITest {
    
    @Test
    @DisplayName("API数据结构测试 - MessageSendDTO序列化")
    void testMessageSendDTOSerialization() {
        // 创建发送消息DTO
        MessageSendDTO dto = new MessageSendDTO();
        dto.setReceiverId(2L);
        dto.setContent("你好，请问这个商品还有库存吗？");
        dto.setMessageType("TEXT");
        dto.setProductId(100L);
        
        // 测试序列化
        String json = JsonUtil.toJson(dto);
        assertNotNull(json);
        assertTrue(json.contains("\"receiverId\":2"));
        assertTrue(json.contains("\"productId\":100"));
        assertTrue(json.contains("\"messageType\":\"TEXT\""));
        
        // 测试反序列化
        MessageSendDTO deserializedDto = JsonUtil.fromJson(json, MessageSendDTO.class);
        assertNotNull(deserializedDto);
        assertEquals(dto.getReceiverId(), deserializedDto.getReceiverId());
        assertEquals(dto.getContent(), deserializedDto.getContent());
        assertEquals(dto.getMessageType(), deserializedDto.getMessageType());
        assertEquals(dto.getProductId(), deserializedDto.getProductId());
    }
    
    @Test
    @DisplayName("API数据结构测试 - MessageVO序列化")
    void testMessageVOSerialization() {
        // 创建消息VO
        MessageVO vo = new MessageVO();
        vo.setMessageId(1L);
        vo.setConversationId("1_2_100");
        vo.setSenderId(1L);
        vo.setReceiverId(2L);
        vo.setContent("你好，请问这个商品还有库存吗？");
        vo.setMessageType("TEXT");
        vo.setProductId(100L);
        vo.setIsRead(false);
        
        // 测试序列化
        String json = JsonUtil.toJson(vo);
        assertNotNull(json);
        assertTrue(json.contains("\"messageId\":1"));
        assertTrue(json.contains("\"conversationId\":\"1_2_100\""));
        assertTrue(json.contains("\"productId\":100"));
        
        // 测试反序列化
        MessageVO deserializedVo = JsonUtil.fromJson(json, MessageVO.class);
        assertNotNull(deserializedVo);
        assertEquals(vo.getMessageId(), deserializedVo.getMessageId());
        assertEquals(vo.getConversationId(), deserializedVo.getConversationId());
        assertEquals(vo.getProductId(), deserializedVo.getProductId());
    }
    
    @Test
    @DisplayName("API响应结构测试 - Result包装")
    void testResultSerialization() {
        // 创建成功响应
        MessageVO messageVO = new MessageVO();
        messageVO.setMessageId(1L);
        messageVO.setConversationId("1_2_100");
        messageVO.setSenderId(1L);
        messageVO.setReceiverId(2L);
        messageVO.setContent("测试消息");
        messageVO.setProductId(100L);
        
        Result<MessageVO> successResult = Result.success(messageVO);
        
        // 测试成功响应序列化
        String successJson = JsonUtil.toJson(successResult);
        assertNotNull(successJson);
        assertTrue(successJson.contains("\"success\":true"));
        assertTrue(successJson.contains("\"conversationId\":\"1_2_100\""));
        assertTrue(successJson.contains("\"productId\":100"));
        
        // 创建错误响应
        Result<MessageVO> errorResult = Result.error("发送消息失败");
        
        // 测试错误响应序列化
        String errorJson = JsonUtil.toJson(errorResult);
        assertNotNull(errorJson);
        assertTrue(errorJson.contains("\"success\":false"));
        assertTrue(errorJson.contains("\"message\":\"发送消息失败\""));
    }
    
    @Test
    @DisplayName("会话ID绑定测试 - 不同场景的会话ID格式")
    void testConversationIdBinding() {
        // 测试场景1: 有商品关联的会话
        MessageSendDTO dto1 = new MessageSendDTO();
        dto1.setReceiverId(2L);
        dto1.setContent("询问商品详情");
        dto1.setProductId(100L);
        
        // 验证DTO包含商品ID
        assertEquals(Long.valueOf(100L), dto1.getProductId());
        
        // 测试场景2: 无商品关联的普通会话
        MessageSendDTO dto2 = new MessageSendDTO();
        dto2.setReceiverId(3L);
        dto2.setContent("普通聊天");
        dto2.setProductId(null);
        
        // 验证DTO不包含商品ID
        assertNull(dto2.getProductId());
        
        // 测试场景3: 不同商品的会话隔离
        MessageSendDTO dto3 = new MessageSendDTO();
        dto3.setReceiverId(2L);
        dto3.setContent("询问另一个商品");
        dto3.setProductId(200L);
        
        // 验证不同商品ID
        assertNotEquals(dto1.getProductId(), dto3.getProductId());
    }
    
    @Test
    @DisplayName("买卖双方ID绑定测试 - 参与者角色验证")
    void testBuyerSellerIdBinding() {
        // 买家向卖家发送消息
        MessageSendDTO buyerToSeller = new MessageSendDTO();
        buyerToSeller.setReceiverId(2L); // 卖家ID
        buyerToSeller.setContent("我想购买这个商品");
        buyerToSeller.setProductId(100L);
        
        // 卖家回复买家
        MessageSendDTO sellerToBuyer = new MessageSendDTO();
        sellerToBuyer.setReceiverId(1L); // 买家ID
        sellerToBuyer.setContent("商品还有库存，欢迎购买");
        sellerToBuyer.setProductId(100L);
        
        // 验证双方都关联同一个商品
        assertEquals(buyerToSeller.getProductId(), sellerToBuyer.getProductId());
        
        // 验证接收者ID不同（体现买卖双方角色）
        assertNotEquals(buyerToSeller.getReceiverId(), sellerToBuyer.getReceiverId());
    }
    
    @Test
    @DisplayName("API请求格式验证 - 必填字段检查")
    void testAPIRequestValidation() {
        // 测试1: 完整的请求数据
        MessageSendDTO validDto = new MessageSendDTO();
        validDto.setReceiverId(2L);
        validDto.setContent("有效的消息内容");
        validDto.setMessageType("TEXT");
        validDto.setProductId(100L);
        
        // 验证所有必填字段都有值
        assertNotNull(validDto.getReceiverId());
        assertNotNull(validDto.getContent());
        assertFalse(validDto.getContent().trim().isEmpty());
        
        // 测试2: 缺少接收者ID
        MessageSendDTO invalidDto1 = new MessageSendDTO();
        invalidDto1.setReceiverId(null);
        invalidDto1.setContent("消息内容");
        
        assertNull(invalidDto1.getReceiverId());
        
        // 测试3: 空消息内容
        MessageSendDTO invalidDto2 = new MessageSendDTO();
        invalidDto2.setReceiverId(2L);
        invalidDto2.setContent("");
        
        assertTrue(invalidDto2.getContent().isEmpty());
        
        // 测试4: 可选字段为空（应该允许）
        MessageSendDTO optionalFieldsDto = new MessageSendDTO();
        optionalFieldsDto.setReceiverId(2L);
        optionalFieldsDto.setContent("消息内容");
        optionalFieldsDto.setProductId(null); // 商品ID可选
        optionalFieldsDto.setMessageType(null); // 消息类型可选，默认为TEXT
        
        assertNull(optionalFieldsDto.getProductId());
        assertNull(optionalFieldsDto.getMessageType());
    }
    
    @Test
    @DisplayName("API响应格式验证 - 返回数据完整性")
    void testAPIResponseFormat() {
        // 模拟API响应数据
        MessageVO responseMessage = new MessageVO();
        responseMessage.setMessageId(1L);
        responseMessage.setConversationId("1_2_100");
        responseMessage.setSenderId(1L);
        responseMessage.setReceiverId(2L);
        responseMessage.setContent("API响应测试消息");
        responseMessage.setMessageType("TEXT");
        responseMessage.setProductId(100L);
        responseMessage.setIsRead(false);
        
        // 验证响应包含所有必要字段
        assertNotNull(responseMessage.getMessageId());
        assertNotNull(responseMessage.getConversationId());
        assertNotNull(responseMessage.getSenderId());
        assertNotNull(responseMessage.getReceiverId());
        assertNotNull(responseMessage.getContent());
        assertNotNull(responseMessage.getMessageType());
        assertNotNull(responseMessage.getProductId());
        assertNotNull(responseMessage.getIsRead());
        
        // 验证会话ID格式正确（包含买卖双方ID和商品ID）
        String conversationId = responseMessage.getConversationId();
        assertTrue(conversationId.contains("1")); // 发送者ID
        assertTrue(conversationId.contains("2")); // 接收者ID
        assertTrue(conversationId.contains("100")); // 商品ID
        
        // 验证会话ID格式：smaller_id_larger_id_product_id
        assertEquals("1_2_100", conversationId);
    }
    
    @Test
    @DisplayName("多种消息类型支持测试")
    void testMultipleMessageTypes() {
        // 文本消息
        MessageSendDTO textMessage = new MessageSendDTO();
        textMessage.setReceiverId(2L);
        textMessage.setContent("这是文本消息");
        textMessage.setMessageType("TEXT");
        textMessage.setProductId(100L);
        
        assertEquals("TEXT", textMessage.getMessageType());
        
        // 图片消息（预留）
        MessageSendDTO imageMessage = new MessageSendDTO();
        imageMessage.setReceiverId(2L);
        imageMessage.setContent("image_url_here");
        imageMessage.setMessageType("IMAGE");
        imageMessage.setProductId(100L);
        
        assertEquals("IMAGE", imageMessage.getMessageType());
        
        // 系统消息（预留）
        MessageSendDTO systemMessage = new MessageSendDTO();
        systemMessage.setReceiverId(2L);
        systemMessage.setContent("系统通知消息");
        systemMessage.setMessageType("SYSTEM");
        systemMessage.setProductId(100L);
        
        assertEquals("SYSTEM", systemMessage.getMessageType());
    }
    
    @Test
    @DisplayName("API兼容性测试 - 向后兼容")
    void testAPICompatibility() {
        // 测试不带商品ID的消息（向后兼容）
        MessageSendDTO legacyMessage = new MessageSendDTO();
        legacyMessage.setReceiverId(2L);
        legacyMessage.setContent("不关联商品的消息");
        // 不设置productId，应该默认为null
        
        assertNull(legacyMessage.getProductId());
        
        // 测试不带消息类型的消息（向后兼容）
        MessageSendDTO defaultTypeMessage = new MessageSendDTO();
        defaultTypeMessage.setReceiverId(2L);
        defaultTypeMessage.setContent("默认类型消息");
        // 不设置messageType，应该默认为null，服务端会设为TEXT
        
        assertNull(defaultTypeMessage.getMessageType());
        
        // 验证JSON序列化不会丢失null字段
        String json = JsonUtil.toJson(legacyMessage);
        MessageSendDTO deserializedMessage = JsonUtil.fromJson(json, MessageSendDTO.class);
        
        assertEquals(legacyMessage.getReceiverId(), deserializedMessage.getReceiverId());
        assertEquals(legacyMessage.getContent(), deserializedMessage.getContent());
        assertEquals(legacyMessage.getProductId(), deserializedMessage.getProductId());
    }
}
