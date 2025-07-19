package com.shiwu.order.demo;

import com.shiwu.order.model.OrderOperationResult;
import com.shiwu.order.service.OrderService;
import com.shiwu.order.service.impl.OrderServiceImpl;

import java.util.Map;

/**
 * 卖家发货功能演示
 */
public class ShipOrderDemo {

    private final OrderService orderService;

    public ShipOrderDemo() {
        this.orderService = new OrderServiceImpl();
    }

    /**
     * 演示卖家发货功能
     */
    public void demonstrateShipOrder() {
        System.out.println("=== 卖家发货功能演示 ===");

        // 模拟场景：卖家ID为100，订单ID为1
        Long sellerId = 100L;
        Long orderId = 1L;

        System.out.println("📦 卖家准备发货...");
        System.out.println("卖家ID: " + sellerId);
        System.out.println("订单ID: " + orderId);

        // 调用发货API
        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);

        if (result.isSuccess()) {
            System.out.println("✅ 发货成功！");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            
            System.out.println("📋 发货结果:");
            System.out.println("  订单ID: " + data.get("orderId"));
            System.out.println("  商品ID: " + data.get("productId"));
            System.out.println("  订单金额: ¥" + data.get("priceAtPurchase"));
            System.out.println("  订单状态: " + data.get("statusText"));
            System.out.println("  操作结果: " + data.get("message"));
            
            System.out.println("\n🎉 订单状态已更新为 SHIPPED (已发货)");
            System.out.println("💌 系统将自动发送通知给买家");
            
        } else {
            System.out.println("❌ 发货失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
            
            // 根据错误码提供建议
            handleShipError(result.getErrorCode());
        }
    }

    /**
     * 处理发货错误并提供建议
     */
    private void handleShipError(String errorCode) {
        System.out.println("\n💡 错误处理建议:");
        
        switch (errorCode) {
            case "ORDER_001":
                System.out.println("  - 请检查订单ID和卖家ID是否正确");
                break;
            case "ORDER_202":
                System.out.println("  - 订单不存在，请检查订单ID是否正确");
                break;
            case "ORDER_301":
                System.out.println("  - 只有订单的卖家才能发货");
                System.out.println("  - 请确认您是该订单的卖家");
                break;
            case "ORDER_302":
                System.out.println("  - 只有状态为'待发货'的订单才能发货");
                System.out.println("  - 请检查订单当前状态");
                System.out.println("  - 可能的状态：待付款、已发货、已完成、已取消等");
                break;
            case "ORDER_303":
                System.out.println("  - 系统更新订单状态失败");
                System.out.println("  - 请稍后重试或联系技术支持");
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
     * 演示不同的发货场景
     */
    public void demonstrateVariousScenarios() {
        System.out.println("\n=== 发货功能各种场景演示 ===");

        // 场景1：正常发货
        System.out.println("\n📦 场景1：正常发货");
        demonstrateScenario(1L, 100L, "卖家对待发货订单进行发货");

        // 场景2：无权限发货
        System.out.println("\n🚫 场景2：无权限发货");
        demonstrateScenario(1L, 999L, "非订单卖家尝试发货");

        // 场景3：订单不存在
        System.out.println("\n❓ 场景3：订单不存在");
        demonstrateScenario(999L, 100L, "对不存在的订单发货");

        // 场景4：参数错误
        System.out.println("\n⚠️ 场景4：参数错误");
        demonstrateScenario(null, 100L, "订单ID为空");
    }

    /**
     * 演示特定场景
     */
    private void demonstrateScenario(Long orderId, Long sellerId, String description) {
        System.out.println("描述: " + description);
        System.out.println("订单ID: " + orderId + ", 卖家ID: " + sellerId);

        OrderOperationResult result = orderService.shipOrder(orderId, sellerId);

        if (result.isSuccess()) {
            System.out.println("✅ 发货成功");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            System.out.println("结果: " + data.get("message"));
        } else {
            System.out.println("❌ 发货失败: " + result.getErrorMessage());
        }
    }

    /**
     * 演示API调用格式
     */
    public void demonstrateApiUsage() {
        System.out.println("\n=== API调用格式演示 ===");
        
        System.out.println("📡 HTTP请求格式:");
        System.out.println("POST /api/orders/{orderId}/ship");
        System.out.println("Content-Type: application/json");
        System.out.println("Authorization: Bearer <token>");
        System.out.println();
        
        System.out.println("📝 请求示例:");
        System.out.println("POST /api/orders/1/ship");
        System.out.println("(卖家身份通过session验证)");
        System.out.println();
        
        System.out.println("✅ 成功响应示例:");
        System.out.println("{");
        System.out.println("  \"success\": true,");
        System.out.println("  \"data\": {");
        System.out.println("    \"orderId\": 1,");
        System.out.println("    \"productId\": 300,");
        System.out.println("    \"priceAtPurchase\": 99.99,");
        System.out.println("    \"status\": 2,");
        System.out.println("    \"statusText\": \"已发货\",");
        System.out.println("    \"message\": \"发货成功\"");
        System.out.println("  }");
        System.out.println("}");
        System.out.println();
        
        System.out.println("❌ 失败响应示例:");
        System.out.println("{");
        System.out.println("  \"success\": false,");
        System.out.println("  \"errorCode\": \"ORDER_301\",");
        System.out.println("  \"errorMessage\": \"无权限发货此订单，只有卖家可以发货\"");
        System.out.println("}");
    }

    /**
     * 运行完整演示
     */
    public void runFullDemo() {
        System.out.println("🚚 卖家发货功能完整演示开始");
        System.out.println("=====================================");

        demonstrateShipOrder();
        demonstrateVariousScenarios();
        demonstrateApiUsage();

        System.out.println("\n=====================================");
        System.out.println("🎉 卖家发货功能演示完成");
        
        System.out.println("\n📋 功能总结:");
        System.out.println("✅ 卖家权限验证");
        System.out.println("✅ 订单状态检查");
        System.out.println("✅ 状态更新为SHIPPED");
        System.out.println("✅ 完整的错误处理");
        System.out.println("✅ 详细的日志记录");
        
        System.out.println("\n🔄 订单状态流转:");
        System.out.println("待付款 → 待发货 → 已发货 → 已完成");
        System.out.println("              ↑");
        System.out.println("           卖家发货");
    }

    /**
     * 主方法，用于运行演示
     */
    public static void main(String[] args) {
        ShipOrderDemo demo = new ShipOrderDemo();
        demo.runFullDemo();
    }
}
