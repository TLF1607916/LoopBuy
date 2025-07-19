package com.shiwu.order.demo;

import com.shiwu.order.model.*;
import com.shiwu.order.service.OrderService;
import com.shiwu.order.service.impl.OrderServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 订单API演示类
 * 展示如何使用订单相关的功能
 */
public class OrderApiDemo {

    private final OrderService orderService;

    public OrderApiDemo() {
        this.orderService = new OrderServiceImpl();
    }

    /**
     * 演示创建订单的完整流程
     */
    public void demonstrateCreateOrder() {
        System.out.println("=== 订单创建演示 ===");

        Long buyerId = 1L;
        List<Long> productIds = Arrays.asList(2L, 3L); // 假设这些商品存在且可购买

        // 创建订单请求
        OrderCreateDTO dto = new OrderCreateDTO(productIds);

        // 调用服务
        OrderOperationResult result = orderService.createOrder(dto, buyerId);

        // 处理结果
        if (result.isSuccess()) {
            System.out.println("✅ 创建订单成功！");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            System.out.println("创建的订单数量: " + data.get("orderCount"));
            @SuppressWarnings("unchecked")
            List<Long> orderIds = (List<Long>) data.get("orderIds");
            System.out.println("订单ID列表: " + orderIds);
        } else {
            System.out.println("❌ 创建订单失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());

            // 根据错误码进行相应处理
            handleCreateOrderError(result.getErrorCode());
        }
    }

    /**
     * 处理创建订单错误
     */
    private void handleCreateOrderError(String errorCode) {
        switch (errorCode) {
            case OrderErrorCode.INVALID_PARAMS:
                System.out.println("💡 建议: 请检查请求参数是否正确");
                break;
            case OrderErrorCode.EMPTY_PRODUCT_LIST:
                System.out.println("💡 建议: 请选择要购买的商品");
                break;
            case OrderErrorCode.PRODUCT_NOT_FOUND:
                System.out.println("💡 建议: 商品可能已被删除，请刷新页面");
                break;
            case OrderErrorCode.PRODUCT_NOT_AVAILABLE:
                System.out.println("💡 建议: 商品当前不可购买，可能已下架或售出");
                break;
            case OrderErrorCode.CANT_BUY_OWN_PRODUCT:
                System.out.println("💡 建议: 不能购买自己发布的商品");
                break;
            case OrderErrorCode.UPDATE_PRODUCT_STATUS_FAILED:
                System.out.println("💡 建议: 商品状态更新失败，请稍后重试");
                break;
            case OrderErrorCode.CREATE_ORDER_FAILED:
                System.out.println("💡 建议: 订单创建失败，请稍后重试");
                break;
            case OrderErrorCode.SYSTEM_ERROR:
                System.out.println("💡 建议: 系统错误，请联系客服");
                break;
            default:
                System.out.println("💡 建议: 未知错误，请联系客服");
                break;
        }
    }

    /**
     * 演示查看买家订单功能
     */
    public void demonstrateViewBuyerOrders() {
        System.out.println("\n=== 查看买家订单演示 ===");

        Long buyerId = 1L;

        // 获取买家订单列表
        OrderOperationResult result = orderService.getBuyerOrders(buyerId);

        if (result.isSuccess()) {
            System.out.println("✅ 获取买家订单列表成功！");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            System.out.println("订单总数: " + data.get("total"));

            @SuppressWarnings("unchecked")
            List<OrderVO> orders = (List<OrderVO>) data.get("orders");
            if (orders.isEmpty()) {
                System.out.println("📝 暂无订单");
            } else {
                System.out.println("📋 订单列表:");
                for (OrderVO order : orders) {
                    System.out.println("  - 订单ID: " + order.getId());
                    System.out.println("    商品: " + order.getProductTitleSnapshot());
                    System.out.println("    价格: ¥" + order.getPriceAtPurchase());
                    System.out.println("    状态: " + order.getStatusText());
                    System.out.println("    创建时间: " + order.getCreateTime());
                    System.out.println();
                }
            }
        } else {
            System.out.println("❌ 获取买家订单列表失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 演示查看卖家订单功能
     */
    public void demonstrateViewSellerOrders() {
        System.out.println("\n=== 查看卖家订单演示 ===");

        Long sellerId = 2L;

        // 获取卖家订单列表
        OrderOperationResult result = orderService.getSellerOrders(sellerId);

        if (result.isSuccess()) {
            System.out.println("✅ 获取卖家订单列表成功！");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            System.out.println("订单总数: " + data.get("total"));

            @SuppressWarnings("unchecked")
            List<OrderVO> orders = (List<OrderVO>) data.get("orders");
            if (orders.isEmpty()) {
                System.out.println("📝 暂无订单");
            } else {
                System.out.println("📋 订单列表:");
                for (OrderVO order : orders) {
                    System.out.println("  - 订单ID: " + order.getId());
                    System.out.println("    商品: " + order.getProductTitleSnapshot());
                    System.out.println("    价格: ¥" + order.getPriceAtPurchase());
                    System.out.println("    买家: " + order.getBuyer().getNickname());
                    System.out.println("    状态: " + order.getStatusText());
                    System.out.println("    创建时间: " + order.getCreateTime());
                    System.out.println();
                }
            }
        } else {
            System.out.println("❌ 获取卖家订单列表失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 演示获取订单详情功能
     */
    public void demonstrateGetOrderDetail() {
        System.out.println("\n=== 获取订单详情演示 ===");

        Long orderId = 1L; // 假设这个订单存在
        Long userId = 1L;  // 当前用户ID

        OrderOperationResult result = orderService.getOrderById(orderId, userId);

        if (result.isSuccess()) {
            System.out.println("✅ 获取订单详情成功！");
            Order order = (Order) result.getData();
            System.out.println("📋 订单详情:");
            System.out.println("  订单ID: " + order.getId());
            System.out.println("  买家ID: " + order.getBuyerId());
            System.out.println("  卖家ID: " + order.getSellerId());
            System.out.println("  商品ID: " + order.getProductId());
            System.out.println("  商品标题: " + order.getProductTitleSnapshot());
            System.out.println("  购买价格: ¥" + order.getPriceAtPurchase());
            System.out.println("  订单状态: " + getOrderStatusText(order.getStatus()));
            System.out.println("  创建时间: " + order.getCreateTime());
        } else {
            System.out.println("❌ 获取订单详情失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 演示更新订单状态功能
     */
    public void demonstrateUpdateOrderStatus() {
        System.out.println("\n=== 更新订单状态演示 ===");

        Long orderId = 1L; // 假设这个订单存在
        Long userId = 2L;  // 卖家ID
        Integer newStatus = Order.STATUS_AWAITING_SHIPPING; // 更新为待发货状态

        OrderOperationResult result = orderService.updateOrderStatus(orderId, newStatus, userId);

        if (result.isSuccess()) {
            System.out.println("✅ 更新订单状态成功！");
            System.out.println("订单ID: " + orderId);
            System.out.println("新状态: " + getOrderStatusText(newStatus));
        } else {
            System.out.println("❌ 更新订单状态失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 获取订单状态描述
     */
    private String getOrderStatusText(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "申请退货";
            case 6: return "已退货";
            default: return "未知状态";
        }
    }

    /**
     * 运行完整演示
     */
    public void runFullDemo() {
        System.out.println("🛒 订单功能完整演示开始");
        System.out.println("=====================================");

        demonstrateCreateOrder();
        demonstrateViewBuyerOrders();
        demonstrateViewSellerOrders();
        demonstrateGetOrderDetail();
        demonstrateUpdateOrderStatus();

        System.out.println("\n=====================================");
        System.out.println("🎉 订单功能演示完成");
    }

    /**
     * 主方法，用于运行演示
     */
    public static void main(String[] args) {
        OrderApiDemo demo = new OrderApiDemo();
        demo.runFullDemo();
    }
}
