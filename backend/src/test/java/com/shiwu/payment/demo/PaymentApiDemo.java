package com.shiwu.payment.demo;

import com.shiwu.payment.model.*;
import com.shiwu.payment.service.PaymentService;
import com.shiwu.payment.service.impl.PaymentServiceImpl;
import com.shiwu.payment.task.PaymentTimeoutHandler;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 支付API演示类
 * 展示如何使用支付相关的功能
 */
public class PaymentApiDemo {

    private final PaymentService paymentService;

    public PaymentApiDemo() {
        this.paymentService = new PaymentServiceImpl();
    }

    /**
     * 演示创建支付的完整流程
     */
    public void demonstrateCreatePayment() {
        System.out.println("=== 支付创建演示 ===");

        Long userId = 1L;
        List<Long> orderIds = Arrays.asList(1L, 2L); // 假设这些订单存在且可支付

        // 创建支付请求
        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(orderIds);
        dto.setTotalAmount(new BigDecimal("199.98")); // 假设两个订单总金额
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        dto.setPaymentPassword("123456");

        // 调用服务
        PaymentOperationResult result = paymentService.createPayment(dto, userId);

        // 处理结果
        if (result.isSuccess()) {
            System.out.println("✅ 创建支付成功！");
            PaymentVO paymentVO = (PaymentVO) result.getData();
            System.out.println("支付ID: " + paymentVO.getPaymentId());
            System.out.println("订单列表: " + paymentVO.getOrderIds());
            System.out.println("支付金额: ¥" + paymentVO.getPaymentAmount());
            System.out.println("支付方式: " + paymentVO.getPaymentMethodText());
            System.out.println("支付状态: " + paymentVO.getPaymentStatusText());
            System.out.println("支付页面URL: " + paymentVO.getPaymentUrl());
            System.out.println("过期时间: " + paymentVO.getExpireTime());
        } else {
            System.out.println("❌ 创建支付失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());

            // 根据错误码进行相应处理
            handleCreatePaymentError(result.getErrorCode());
        }
    }

    /**
     * 处理创建支付错误
     */
    private void handleCreatePaymentError(String errorCode) {
        switch (errorCode) {
            case PaymentErrorCode.INVALID_PARAMS:
                System.out.println("💡 建议: 请检查请求参数是否正确");
                break;
            case PaymentErrorCode.EMPTY_ORDER_LIST:
                System.out.println("💡 建议: 请选择要支付的订单");
                break;
            case PaymentErrorCode.INVALID_AMOUNT:
                System.out.println("💡 建议: 请检查支付金额是否正确");
                break;
            case PaymentErrorCode.INVALID_PAYMENT_METHOD:
                System.out.println("💡 建议: 请选择正确的支付方式");
                break;
            case PaymentErrorCode.ORDER_NOT_FOUND:
                System.out.println("💡 建议: 订单可能已被删除，请刷新页面");
                break;
            case PaymentErrorCode.ORDER_STATUS_INVALID:
                System.out.println("💡 建议: 订单状态不正确，可能已支付或已取消");
                break;
            case PaymentErrorCode.ORDER_AMOUNT_MISMATCH:
                System.out.println("💡 建议: 订单金额与支付金额不匹配，请重新计算");
                break;
            case PaymentErrorCode.ORDER_PERMISSION_DENIED:
                System.out.println("💡 建议: 无权限操作此订单");
                break;
            case PaymentErrorCode.SYSTEM_ERROR:
                System.out.println("💡 建议: 系统错误，请联系客服");
                break;
            default:
                System.out.println("💡 建议: 未知错误，请联系客服");
                break;
        }
    }

    /**
     * 演示支付处理功能
     */
    public void demonstrateProcessPayment() {
        System.out.println("\n=== 支付处理演示 ===");

        String paymentId = "PAY123456789"; // 假设这个支付记录存在
        String paymentPassword = "123456";
        Long userId = 1L;

        // 处理支付
        PaymentOperationResult result = paymentService.processPayment(paymentId, paymentPassword, userId);

        if (result.isSuccess()) {
            System.out.println("✅ 支付处理成功！");
            PaymentVO paymentVO = (PaymentVO) result.getData();
            System.out.println("支付ID: " + paymentVO.getPaymentId());
            System.out.println("支付状态: " + paymentVO.getPaymentStatusText());
            System.out.println("支付时间: " + paymentVO.getPaymentTime());
            System.out.println("第三方交易号: " + paymentVO.getThirdPartyTransactionId());
        } else {
            System.out.println("❌ 支付处理失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());

            // 根据错误码进行相应处理
            handleProcessPaymentError(result.getErrorCode());
        }
    }

    /**
     * 处理支付处理错误
     */
    private void handleProcessPaymentError(String errorCode) {
        switch (errorCode) {
            case PaymentErrorCode.PAYMENT_NOT_FOUND:
                System.out.println("💡 建议: 支付记录不存在，请检查支付ID");
                break;
            case PaymentErrorCode.PAYMENT_ALREADY_PROCESSED:
                System.out.println("💡 建议: 支付已处理，请勿重复操作");
                break;
            case PaymentErrorCode.PAYMENT_PASSWORD_ERROR:
                System.out.println("💡 建议: 支付密码错误，请重新输入");
                break;
            case PaymentErrorCode.PAYMENT_TIMEOUT:
                System.out.println("💡 建议: 支付已超时，请重新下单");
                break;
            default:
                System.out.println("💡 建议: 支付失败，请稍后重试");
                break;
        }
    }

    /**
     * 演示查看支付状态功能
     */
    public void demonstrateGetPaymentStatus() {
        System.out.println("\n=== 查看支付状态演示 ===");

        String paymentId = "PAY123456789";
        Long userId = 1L;

        PaymentOperationResult result = paymentService.getPaymentStatus(paymentId, userId);

        if (result.isSuccess()) {
            System.out.println("✅ 获取支付状态成功！");
            PaymentVO paymentVO = (PaymentVO) result.getData();
            System.out.println("📋 支付详情:");
            System.out.println("  支付ID: " + paymentVO.getPaymentId());
            System.out.println("  订单列表: " + paymentVO.getOrderIds());
            System.out.println("  支付金额: ¥" + paymentVO.getPaymentAmount());
            System.out.println("  支付方式: " + paymentVO.getPaymentMethodText());
            System.out.println("  支付状态: " + paymentVO.getPaymentStatusText());
            System.out.println("  支付时间: " + paymentVO.getPaymentTime());
            System.out.println("  过期时间: " + paymentVO.getExpireTime());
            if (paymentVO.getFailureReason() != null) {
                System.out.println("  失败原因: " + paymentVO.getFailureReason());
            }
        } else {
            System.out.println("❌ 获取支付状态失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 演示获取用户支付记录功能
     */
    public void demonstrateGetUserPayments() {
        System.out.println("\n=== 获取用户支付记录演示 ===");

        Long userId = 1L;

        PaymentOperationResult result = paymentService.getUserPayments(userId);

        if (result.isSuccess()) {
            System.out.println("✅ 获取用户支付记录成功！");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) result.getData();
            System.out.println("支付记录总数: " + data.get("total"));

            @SuppressWarnings("unchecked")
            List<PaymentVO> payments = (List<PaymentVO>) data.get("payments");
            if (payments.isEmpty()) {
                System.out.println("📝 暂无支付记录");
            } else {
                System.out.println("📋 支付记录列表:");
                for (PaymentVO payment : payments) {
                    System.out.println("  - 支付ID: " + payment.getPaymentId());
                    System.out.println("    金额: ¥" + payment.getPaymentAmount());
                    System.out.println("    方式: " + payment.getPaymentMethodText());
                    System.out.println("    状态: " + payment.getPaymentStatusText());
                    System.out.println("    时间: " + payment.getPaymentTime());
                    System.out.println();
                }
            }
        } else {
            System.out.println("❌ 获取用户支付记录失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 演示取消支付功能
     */
    public void demonstrateCancelPayment() {
        System.out.println("\n=== 取消支付演示 ===");

        String paymentId = "PAY123456789";
        Long userId = 1L;

        PaymentOperationResult result = paymentService.cancelPayment(paymentId, userId);

        if (result.isSuccess()) {
            System.out.println("✅ 取消支付成功！");
            System.out.println("支付ID: " + paymentId);
            System.out.println("相关订单已取消，商品已解锁");
        } else {
            System.out.println("❌ 取消支付失败！");
            System.out.println("错误码: " + result.getErrorCode());
            System.out.println("错误信息: " + result.getErrorMessage());
        }
    }

    /**
     * 演示支付超时处理功能
     */
    public void demonstratePaymentTimeout() {
        System.out.println("\n=== 支付超时处理演示 ===");

        // 获取超时处理器实例
        PaymentTimeoutHandler timeoutHandler = PaymentTimeoutHandler.getInstance();

        // 检查当前过期支付记录数量
        int expiredCount = timeoutHandler.getExpiredPaymentCount();
        System.out.println("当前过期支付记录数量: " + expiredCount);

        if (expiredCount > 0) {
            System.out.println("发现过期支付记录，系统会自动处理");
        } else {
            System.out.println("没有发现过期支付记录");
        }

        // 检查超时处理任务状态
        boolean isRunning = timeoutHandler.isRunning();
        System.out.println("超时处理任务运行状态: " + (isRunning ? "运行中" : "已停止"));

        // 手动处理指定的过期支付（演示）
        String paymentId = "PAY123456789";
        System.out.println("手动处理过期支付: " + paymentId);
        boolean handleSuccess = timeoutHandler.handleExpiredPayment(paymentId);
        System.out.println("处理结果: " + (handleSuccess ? "成功" : "失败"));
    }

    /**
     * 演示直接支付功能
     */
    public void demonstrateDirectPayment() {
        System.out.println("\n=== 直接支付演示 ===");

        // 演示完整的支付流程：创建支付 -> 输入密码 -> 支付成功
        System.out.println("1. 创建支付订单...");
        Long userId = 1L;
        List<Long> orderIds = Arrays.asList(1L, 2L);

        PaymentDTO dto = new PaymentDTO();
        dto.setOrderIds(orderIds);
        dto.setTotalAmount(new BigDecimal("199.98"));
        dto.setPaymentMethod(Payment.METHOD_ALIPAY);
        dto.setPaymentPassword("123456");

        PaymentOperationResult createResult = paymentService.createPayment(dto, userId);

        if (createResult.isSuccess()) {
            PaymentVO paymentVO = (PaymentVO) createResult.getData();
            String paymentId = paymentVO.getPaymentId();

            System.out.println("✅ 支付订单创建成功！");
            System.out.println("支付ID: " + paymentId);
            System.out.println("支付页面: " + paymentVO.getPaymentUrl());

            System.out.println("\n2. 用户输入密码并确认支付...");

            // 模拟用户输入密码并确认支付
            PaymentOperationResult payResult = paymentService.processPayment(paymentId, "123456", userId);

            if (payResult.isSuccess()) {
                PaymentVO resultVO = (PaymentVO) payResult.getData();
                System.out.println("✅ 支付成功！");
                System.out.println("支付状态: " + resultVO.getPaymentStatusText());
                System.out.println("交易号: " + resultVO.getThirdPartyTransactionId());
                System.out.println("支付时间: " + resultVO.getPaymentTime());
                System.out.println("相关订单状态已更新为待发货");
            } else {
                System.out.println("❌ 支付失败！");
                System.out.println("错误信息: " + payResult.getErrorMessage());
            }
        } else {
            System.out.println("❌ 创建支付订单失败！");
            System.out.println("错误信息: " + createResult.getErrorMessage());
        }
    }

    /**
     * 运行完整演示
     */
    public void runFullDemo() {
        System.out.println("💳 支付功能完整演示开始");
        System.out.println("=====================================");

        demonstrateCreatePayment();
        demonstrateProcessPayment();
        demonstrateGetPaymentStatus();
        demonstrateGetUserPayments();
        demonstrateCancelPayment();
        demonstratePaymentTimeout();
        demonstrateDirectPayment();

        System.out.println("\n=====================================");
        System.out.println("🎉 支付功能演示完成");
    }

    /**
     * 主方法，用于运行演示
     */
    public static void main(String[] args) {
        PaymentApiDemo demo = new PaymentApiDemo();
        demo.runFullDemo();
    }
}
