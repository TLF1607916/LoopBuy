package com.shiwu.payment.listener;

import com.shiwu.payment.task.PaymentTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 支付超时监听器
 * 在应用启动时启动支付超时检查任务，在应用关闭时停止任务
 */
@WebListener
public class PaymentTimeoutListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("应用启动，开始初始化支付超时检查任务");
        
        try {
            // 启动支付超时检查任务
            PaymentTimeoutHandler.getInstance().startTimeoutCheckTask();
            logger.info("支付超时检查任务启动成功");
        } catch (Exception e) {
            logger.error("启动支付超时检查任务失败: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("应用关闭，开始停止支付超时检查任务");
        
        try {
            // 停止支付超时检查任务
            PaymentTimeoutHandler.getInstance().stopTimeoutCheckTask();
            logger.info("支付超时检查任务停止成功");
        } catch (Exception e) {
            logger.error("停止支付超时检查任务失败: {}", e.getMessage(), e);
        }
    }
}
