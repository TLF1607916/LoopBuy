#!/bin/bash

# Task4_3_1_3 API端点测试脚本
# 用于验证所有通知API接口的功能

BASE_URL="http://localhost:8080"
USER_ID="1"

echo "=== Task4_3_1_3 API端点测试 ==="
echo "基础URL: $BASE_URL"
echo "测试用户ID: $USER_ID"
echo ""

# 测试1: 获取通知列表
echo "1. 测试获取通知列表..."
curl -s -X GET "$BASE_URL/api/notification/list?page=1&size=5&onlyUnread=false" \
     -H "X-User-Id: $USER_ID" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

# 测试2: 获取未读通知数量
echo "2. 测试获取未读通知数量..."
curl -s -X GET "$BASE_URL/api/notification/unread-count" \
     -H "X-User-Id: $USER_ID" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

# 测试3: 获取未读通知列表
echo "3. 测试获取未读通知列表..."
curl -s -X GET "$BASE_URL/api/notification/list?page=1&size=3&onlyUnread=true" \
     -H "X-User-Id: $USER_ID" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

# 测试4: 标记通知已读（需要有效的通知ID）
echo "4. 测试标记通知已读（使用通知ID 20）..."
curl -s -X PUT "$BASE_URL/api/notification/mark-read?id=20" \
     -H "X-User-Id: $USER_ID" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

# 测试5: 批量标记通知已读
echo "5. 测试批量标记通知已读..."
curl -s -X PUT "$BASE_URL/api/notification/mark-all-read" \
     -H "X-User-Id: $USER_ID" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

# 测试6: 错误处理 - 无效用户ID
echo "6. 测试错误处理 - 无效用户ID..."
curl -s -X GET "$BASE_URL/api/notification/list?page=1&size=5" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

# 测试7: 错误处理 - 无效通知ID
echo "7. 测试错误处理 - 无效通知ID..."
curl -s -X PUT "$BASE_URL/api/notification/mark-read?id=invalid" \
     -H "X-User-Id: $USER_ID" \
     -H "Content-Type: application/json" | jq '.' 2>/dev/null || echo "需要安装jq工具来格式化JSON输出"
echo ""

echo "=== API端点测试完成 ==="
echo ""
echo "注意："
echo "1. 确保后端服务器在 $BASE_URL 运行"
echo "2. 安装jq工具以获得更好的JSON格式化输出: sudo apt-get install jq"
echo "3. 某些测试可能因为数据状态而返回不同结果"
