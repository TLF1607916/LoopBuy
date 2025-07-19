package com.shiwu.admin.service;

import com.shiwu.admin.model.DashboardStatsVO;
import com.shiwu.admin.model.StatsPeriod;

/**
 * 仪表盘服务接口
 * 根据项目规范UC-15实现
 */
public interface DashboardService {
    
    /**
     * 获取仪表盘统计数据
     * @return 仪表盘统计数据视图对象
     */
    DashboardStatsVO getDashboardStats();
    
    /**
     * 获取指定时间段的统计数据
     * @param period 统计时间段
     * @return 仪表盘统计数据视图对象
     */
    DashboardStatsVO getDashboardStats(StatsPeriod period);
    
    /**
     * 刷新统计数据缓存
     * @return 是否刷新成功
     */
    boolean refreshStatsCache();
    
    /**
     * 获取实时统计数据（不使用缓存）
     * @return 仪表盘统计数据视图对象
     */
    DashboardStatsVO getRealTimeStats();
}
