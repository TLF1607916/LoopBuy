package com.shiwu.admin.controller;

import com.shiwu.admin.model.DashboardStatsVO;
import com.shiwu.admin.model.StatsPeriod;
import com.shiwu.admin.service.DashboardService;
import com.shiwu.admin.service.impl.DashboardServiceImpl;
import com.shiwu.common.result.Result;
import com.shiwu.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 仪表盘控制器
 * 根据项目规范UC-15实现
 */
@WebServlet("/admin/dashboard/*")
public class DashboardController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    private DashboardService dashboardService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.dashboardService = new DashboardServiceImpl();
        logger.info("DashboardController 初始化完成");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // 验证管理员权限
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (!JwtUtil.validateToken(token)) {
                Result.error("未授权访问").writeToResponse(response);
                return;
            }
            
            // 检查是否为管理员角色
            String role = JwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
                Result.error("权限不足").writeToResponse(response);
                return;
            }
            
            // 获取请求路径
            String pathInfo = request.getPathInfo();
            if (pathInfo == null) {
                pathInfo = "";
            }
            
            // 路由处理
            switch (pathInfo) {
                case "/stats":
                    handleGetStats(request, response);
                    break;
                case "/stats/refresh":
                    handleRefreshStats(request, response);
                    break;
                case "/stats/realtime":
                    handleGetRealTimeStats(request, response);
                    break;
                default:
                    Result.error("接口不存在").writeToResponse(response);
                    break;
            }
            
        } catch (Exception e) {
            logger.error("处理仪表盘请求失败: {}", e.getMessage(), e);
            Result.error("服务器内部错误").writeToResponse(response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // 验证管理员权限
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (!JwtUtil.validateToken(token)) {
                Result.error("未授权访问").writeToResponse(response);
                return;
            }
            
            // 检查是否为管理员角色
            String role = JwtUtil.getRoleFromToken(token);
            if (!"ADMIN".equals(role) && !"SUPER_ADMIN".equals(role)) {
                Result.error("权限不足").writeToResponse(response);
                return;
            }
            
            // 获取请求路径
            String pathInfo = request.getPathInfo();
            if (pathInfo == null) {
                pathInfo = "";
            }
            
            // 路由处理
            switch (pathInfo) {
                case "/stats/refresh":
                    handleRefreshStats(request, response);
                    break;
                default:
                    Result.error("接口不存在").writeToResponse(response);
                    break;
            }
            
        } catch (Exception e) {
            logger.error("处理仪表盘POST请求失败: {}", e.getMessage(), e);
            Result.error("服务器内部错误").writeToResponse(response);
        }
    }
    
    /**
     * 处理获取统计数据请求
     * GET /admin/dashboard/stats
     */
    private void handleGetStats(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // 获取时间段参数
            String periodParam = request.getParameter("period");
            StatsPeriod period = StatsPeriod.fromCode(periodParam);
            
            DashboardStatsVO stats;
            if (period != null) {
                stats = dashboardService.getDashboardStats(period);
                logger.debug("获取指定时间段统计数据: {}", period.getDescription());
            } else {
                stats = dashboardService.getDashboardStats();
                logger.debug("获取默认统计数据");
            }
            
            Result.success(stats).writeToResponse(response);
            
        } catch (Exception e) {
            logger.error("获取统计数据失败: {}", e.getMessage(), e);
            Result.error("获取统计数据失败").writeToResponse(response);
        }
    }
    
    /**
     * 处理刷新统计数据请求
     * POST /admin/dashboard/stats/refresh
     */
    private void handleRefreshStats(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            boolean success = dashboardService.refreshStatsCache();
            
            if (success) {
                DashboardStatsVO stats = dashboardService.getDashboardStats();
                Result.success(stats, "统计数据刷新成功").writeToResponse(response);
                logger.info("管理员手动刷新统计数据缓存成功");
            } else {
                Result.error("刷新统计数据失败").writeToResponse(response);
                logger.warn("管理员手动刷新统计数据缓存失败");
            }
            
        } catch (Exception e) {
            logger.error("刷新统计数据失败: {}", e.getMessage(), e);
            Result.error("刷新统计数据失败").writeToResponse(response);
        }
    }
    
    /**
     * 处理获取实时统计数据请求
     * GET /admin/dashboard/stats/realtime
     */
    private void handleGetRealTimeStats(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            DashboardStatsVO stats = dashboardService.getRealTimeStats();
            Result.success(stats, "实时统计数据").writeToResponse(response);
            logger.debug("获取实时统计数据成功");
            
        } catch (Exception e) {
            logger.error("获取实时统计数据失败: {}", e.getMessage(), e);
            Result.error("获取实时统计数据失败").writeToResponse(response);
        }
    }
}
