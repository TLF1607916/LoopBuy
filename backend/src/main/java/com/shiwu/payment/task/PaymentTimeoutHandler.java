package com.shiwu.payment.task;

import com.shiwu.payment.dao.PaymentDao;
import com.shiwu.payment.model.Payment;
import com.shiwu.payment.model.PaymentOperationResult;
import com.shiwu.payment.service.PaymentService;
import com.shiwu.payment.service.impl.PaymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 支付超时处理器
 * 定期检查并处理过期的支付记录
 */
public class PaymentTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutHandler.class);
    
    private final PaymentDao paymentDao;
    private final PaymentService paymentService;
    private final ScheduledExecutorService scheduler;
    
    // 单例模式
    private static volatile PaymentTimeoutHandler instance;
    
    private PaymentTimeoutHandler() {
        this.paymentDao = new PaymentDao();
        this.paymentService = new PaymentServiceImpl();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * 获取单例实例
     */
    public static PaymentTimeoutHandler getInstance() {
        if (instance == null) {
            synchronized (PaymentTimeoutHandler.class) {
                if (instance == null) {
                    instance = new PaymentTimeoutHandler();
                }
            }
        }
        return instance;
    }
    
    /**
     * 启动支付超时检查任务
     * 每分钟检查一次过期的支付记录
     */
    public void startTimeoutCheckTask() {
        logger.info("启动支付超时检查任务");
        
        scheduler.scheduleWithFixedDelay(
            this::checkAndHandleExpiredPayments,
            1, // 初始延迟1分钟
            1, // 每1分钟执行一次
            TimeUnit.MINUTES
        );
    }
    
    /**
     * 停止支付超时检查任务
     */
    public void stopTimeoutCheckTask() {
        logger.info("停止支付超时检查任务");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 检查并处理过期的支付记录
     */
    private void checkAndHandleExpiredPayments() {
        try {
            logger.debug("开始检查过期的支付记录");
            
            // 查询所有过期的待支付记录
            List<Payment> expiredPayments = paymentDao.findExpiredPayments();
            
            if (expiredPayments.isEmpty()) {
                logger.debug("没有发现过期的支付记录");
                return;
            }
            
            logger.info("发现{}个过期的支付记录，开始处理", expiredPayments.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (Payment payment : expiredPayments) {
                try {
                    logger.info("处理过期支付: paymentId={}, userId={}, expireTime={}", 
                               payment.getPaymentId(), payment.getUserId(), payment.getExpireTime());
                    
                    // 调用支付服务处理超时
                    PaymentOperationResult result = paymentService.handlePaymentTimeout(payment.getPaymentId());
                    
                    if (result.isSuccess()) {
                        successCount++;
                        logger.info("处理过期支付成功: paymentId={}", payment.getPaymentId());
                    } else {
                        failureCount++;
                        logger.error("处理过期支付失败: paymentId={}, error={}", 
                                    payment.getPaymentId(), result.getErrorMessage());
                    }
                    
                } catch (Exception e) {
                    failureCount++;
                    logger.error("处理过期支付时发生异常: paymentId={}, error={}", 
                                payment.getPaymentId(), e.getMessage(), e);
                }
            }
            
            logger.info("过期支付处理完成: 总数={}, 成功={}, 失败={}", 
                       expiredPayments.size(), successCount, failureCount);
            
        } catch (Exception e) {
            logger.error("检查过期支付记录时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 手动处理指定的过期支付
     * @param paymentId 支付流水号
     * @return 是否处理成功
     */
    public boolean handleExpiredPayment(String paymentId) {
        try {
            logger.info("手动处理过期支付: paymentId={}", paymentId);
            
            PaymentOperationResult result = paymentService.handlePaymentTimeout(paymentId);
            
            if (result.isSuccess()) {
                logger.info("手动处理过期支付成功: paymentId={}", paymentId);
                return true;
            } else {
                logger.error("手动处理过期支付失败: paymentId={}, error={}", paymentId, result.getErrorMessage());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("手动处理过期支付时发生异常: paymentId={}, error={}", paymentId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取当前过期支付记录数量
     * @return 过期支付记录数量
     */
    public int getExpiredPaymentCount() {
        try {
            List<Payment> expiredPayments = paymentDao.findExpiredPayments();
            return expiredPayments.size();
        } catch (Exception e) {
            logger.error("获取过期支付记录数量时发生异常: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * 检查任务是否正在运行
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return !scheduler.isShutdown();
    }
}
