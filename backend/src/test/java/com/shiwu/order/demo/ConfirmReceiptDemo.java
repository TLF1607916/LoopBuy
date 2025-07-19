package com.shiwu.order.demo;

import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.service.OrderService;
import com.shiwu.order.service.impl.OrderServiceImpl;

import java.util.Map;

/**
 * 买家确认收货功能演示
 */
public class ConfirmReceiptDemo {

    private final OrderService orderService;

    public ConfirmReceiptDemo() {
        this.orderService = new OrderServiceImpl();
    }

    /**
     * 演示买家确认收货功能
     */
    public void demonstrateConfirmReceipt() {
        System.out.println("=== 买家确认收货功能演示 ===");

        // 模拟场景：买家ID为200，订单ID为1
        Long buyerId = 200L;
        Long orderId = 1L;

        System.out.println("📦 买家准备确认收货...");
        System.out.println("买家ID: " + buyerId);
        System.out.println("订单ID: " + orderId);

        // 调用确认收货API
        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);

        if (result.isSuccess()) {
            System.out.println("✅ 确认收货成功！");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            
            System.out.println("📋 确认收货结果:");
            System.out.println("  订单ID: " + data.get("orderId"));
            System.out.println("  商品ID: " + data.get("productId"));
            System.out.println("  订单金额: ¥" + data.get("priceAtPurchase"));
            System.out.println("  订单状态: " + data.get("orderStatusText"));
            System.out.println("  商品状态: " + data.get("productStatusText"));
            System.out.println("  操作结果: " + data.get("message"));
            
            System.out.println("\n🎉 交易完成！");
            System.out.println("📊 状态更新:");
            System.out.println("  - 订单状态: SHIPPED → COMPLETED");
            System.out.println("  - 商品状态: LOCKED → SOLD");
            System.out.println("💌 系统将自动发送通知给卖家");
            
        } else {
            System.out.println("❌ 确认收货失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
            
            // 根据错误码提供建议
            handleConfirmReceiptError(result.getErrorCode());
        }
    }

    /**
     * 处理确认收货错误并提供建议
     */
    private void handleConfirmReceiptError(String errorCode) {
        System.out.println("\n💡 错误处理建议:");
        
        switch (errorCode) {
            case "ORDER_001":
                System.out.println("  - 请检查订单ID和买家ID是否正确");
                break;
            case "ORDER_202":
                System.out.println("  - 订单不存在，请检查订单ID是否正确");
                break;
            case "ORDER_401":
                System.out.println("  - 只有订单的买家才能确认收货");
                System.out.println("  - 请确认您是该订单的买家");
                break;
            case "ORDER_402":
                System.out.println("  - 只有状态为'已发货'的订单才能确认收货");
                System.out.println("  - 请检查订单当前状态");
                System.out.println("  - 可能的状态：待付款、待发货、已完成、已取消等");
                break;
            case "ORDER_403":
                System.out.println("  - 系统更新订单状态失败");
                System.out.println("  - 请稍后重试或联系技术支持");
                break;
            case "ORDER_404":
                System.out.println("  - 系统更新商品状态失败");
                System.out.println("  - 请联系技术支持处理");
                break;
            case "ORDER_500":
                System.out.println("  - 系统错误，请稍后重试");
                System.out.println("  - 如果问题持续存在，请联系技术支持");
                break;
            default:
                System.out.println("  - 未知错误，请联系技术支持");
                break;
        }
    }

    /**
     * 演示不同的确认收货场景
     */
    public void demonstrateVariousScenarios() {
        System.out.println("\n=== 确认收货功能各种场景演示 ===");

        // 场景1：正常确认收货
        System.out.println("\n📦 场景1：正常确认收货");
        demonstrateScenario(1L, 200L, "买家对已发货订单进行确认收货");

        // 场景2：无权限确认收货
        System.out.println("\n🚫 场景2：无权限确认收货");
        demonstrateScenario(1L, 999L, "非订单买家尝试确认收货");

        // 场景3：订单不存在
        System.out.println("\n❓ 场景3：订单不存在");
        demonstrateScenario(999L, 200L, "对不存在的订单确认收货");

        // 场景4：参数错误
        System.out.println("\n⚠️ 场景4：参数错误");
        demonstrateScenario(null, 200L, "订单ID为空");
    }

    /**
     * 演示特定场景
     */
    private void demonstrateScenario(Long orderId, Long buyerId, String description) {
        System.out.println("描述: " + description);
        System.out.println("订单ID: " + orderId + ", 买家ID: " + buyerId);

        OrderOperationResult result = orderService.confirmReceipt(orderId, buyerId);

        if (result.isSuccess()) {
            System.out.println("✅ 确认收货成功");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            System.out.println("结果: " + data.get("message"));
        } else {
            System.out.println("❌ 确认收货失败: " + result.getErrorMessage());
        }
    }

    /**
     * 演示API调用格式
     */
    public void demonstrateApiUsage() {
        System.out.println("\n=== API调用格式演示 ===");
        
        System.out.println("📡 HTTP请求格式:");
        System.out.println("POST /api/orders/{orderId}/confirm");
        System.out.println("Content-Type: application/json");
        System.out.println("Authorization: Bearer <token>");
        System.out.println();
        
        System.out.println("📝 请求示例:");
        System.out.println("POST /api/orders/1/confirm");
        System.out.println("(买家身份通过session验证)");
        System.out.println();
        
        System.out.println("✅ 成功响应示例:");
        System.out.println("{");
        System.out.println("  \"success\": true,");
        System.out.println("  \"data\": {");
        System.out.println("    \"orderId\": 1,");
        System.out.println("    \"productId\": 300,");
        System.out.println("    \"priceAtPurchase\": 99.99,");
        System.out.println("    \"orderStatus\": 3,");
        System.out.println("    \"orderStatusText\": \"已完成\",");
        System.out.println("    \"productStatus\": 2,");
        System.out.println("    \"productStatusText\": \"已售出\",");
        System.out.println("    \"message\": \"确认收货成功\"");
        System.out.println("  }");
        System.out.println("}");
        System.out.println();
        
        System.out.println("❌ 失败响应示例:");
        System.out.println("{");
        System.out.println("  \"success\": false,");
        System.out.println("  \"errorCode\": \"ORDER_401\",");
        System.out.println("  \"errorMessage\": \"无权限确认收货此订单，只有买家可以确认收货\"");
        System.out.println("}");
    }

    /**
     * 演示完整的订单生命周期
     */
    public void demonstrateOrderLifecycle() {
        System.out.println("\n=== 完整订单生命周期演示 ===");
        
        System.out.println("🔄 订单状态流转:");
        System.out.println("1. 创建订单 → AWAITING_PAYMENT (待付款)");
        System.out.println("2. 买家支付 → AWAITING_SHIPPING (待发货)");
        System.out.println("3. 卖家发货 → SHIPPED (已发货)");
        System.out.println("4. 买家确认收货 → COMPLETED (已完成) ← 本次实现");
        System.out.println();
        
        System.out.println("📦 商品状态流转:");
        System.out.println("1. 商品上架 → ONSALE (在售)");
        System.out.println("2. 用户下单 → LOCKED (锁定)");
        System.out.println("3. 确认收货 → SOLD (已售出) ← 本次实现");
        System.out.println();
        
        System.out.println("🎯 关键节点:");
        System.out.println("- 买家确认收货是交易的最终环节");
        System.out.println("- 确认收货后订单状态变为COMPLETED");
        System.out.println("- 同时商品状态变为SOLD，表示交易完成");
        System.out.println("- 此后买家可以对商品进行评价");
        System.out.println("- 在一定时间内买家还可以申请售后/退货");
    }

    /**
     * 运行完整演示
     */
    public void runFullDemo() {
        System.out.println("📦 买家确认收货功能完整演示开始");
        System.out.println("=====================================");

        demonstrateConfirmReceipt();
        demonstrateVariousScenarios();
        demonstrateApiUsage();
        demonstrateOrderLifecycle();

        System.out.println("\n=====================================");
        System.out.println("🎉 买家确认收货功能演示完成");
        
        System.out.println("\n📋 功能总结:");
        System.out.println("✅ 买家权限验证");
        System.out.println("✅ 订单状态检查");
        System.out.println("✅ 状态更新为COMPLETED");
        System.out.println("✅ 商品状态更新为SOLD");
        System.out.println("✅ 完整的错误处理");
        System.out.println("✅ 详细的日志记录");
        
        System.out.println("\n🔄 状态变更:");
        System.out.println("订单: SHIPPED → COMPLETED");
        System.out.println("商品: LOCKED → SOLD");
        System.out.println("交易: 进行中 → 已完成");
    }

    /**
     * 主方法，用于运行演示
     */
    public static void main(String[] args) {
        ConfirmReceiptDemo demo = new ConfirmReceiptDemo();
        demo.runFullDemo();
    }
}
