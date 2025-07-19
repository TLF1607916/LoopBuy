package com.shiwu.admin.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 仪表盘统计数据视图对象
 * 根据项目规范UC-15实现
 */
public class DashboardStatsVO {
    
    // 核心KPI指标
    private OverviewStats overview;
    
    // 用户统计数据
    private UserStats userStats;
    
    // 商品统计数据
    private ProductStats productStats;
    
    // 系统活动统计
    private ActivityStats activityStats;
    
    // 趋势图表数据
    private List<TrendData> userTrend;
    private List<TrendData> productTrend;
    private List<TrendData> activityTrend;
    
    // 数据更新时间
    private LocalDateTime lastUpdateTime;

    public OverviewStats getOverview() {
        return overview;
    }

    public void setOverview(OverviewStats overview) {
        this.overview = overview;
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }

    public ProductStats getProductStats() {
        return productStats;
    }

    public void setProductStats(ProductStats productStats) {
        this.productStats = productStats;
    }

    public ActivityStats getActivityStats() {
        return activityStats;
    }

    public void setActivityStats(ActivityStats activityStats) {
        this.activityStats = activityStats;
    }

    public List<TrendData> getUserTrend() {
        return userTrend;
    }

    public void setUserTrend(List<TrendData> userTrend) {
        this.userTrend = userTrend;
    }

    public List<TrendData> getProductTrend() {
        return productTrend;
    }

    public void setProductTrend(List<TrendData> productTrend) {
        this.productTrend = productTrend;
    }

    public List<TrendData> getActivityTrend() {
        return activityTrend;
    }

    public void setActivityTrend(List<TrendData> activityTrend) {
        this.activityTrend = activityTrend;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * 总览统计数据
     */
    public static class OverviewStats {
        private Long totalUsers;           // 总用户数
        private Long totalProducts;        // 总商品数
        private Long totalActiveUsers;     // 活跃用户数（最近30天登录）
        private Long totalPendingProducts; // 待审核商品数
        private Double averageRating;      // 平台平均评分
        private Long totalFollowRelations; // 总关注关系数

        public Long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(Long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public Long getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(Long totalProducts) {
            this.totalProducts = totalProducts;
        }

        public Long getTotalActiveUsers() {
            return totalActiveUsers;
        }

        public void setTotalActiveUsers(Long totalActiveUsers) {
            this.totalActiveUsers = totalActiveUsers;
        }

        public Long getTotalPendingProducts() {
            return totalPendingProducts;
        }

        public void setTotalPendingProducts(Long totalPendingProducts) {
            this.totalPendingProducts = totalPendingProducts;
        }

        public Double getAverageRating() {
            return averageRating;
        }

        public void setAverageRating(Double averageRating) {
            this.averageRating = averageRating;
        }

        public Long getTotalFollowRelations() {
            return totalFollowRelations;
        }

        public void setTotalFollowRelations(Long totalFollowRelations) {
            this.totalFollowRelations = totalFollowRelations;
        }
    }

    /**
     * 用户统计数据
     */
    public static class UserStats {
        private Long newUsersToday;        // 今日新增用户
        private Long newUsersThisWeek;     // 本周新增用户
        private Long newUsersThisMonth;    // 本月新增用户
        private Long bannedUsers;          // 被封禁用户数
        private Long mutedUsers;           // 被禁言用户数
        private Double userGrowthRate;     // 用户增长率（月环比）

        public Long getNewUsersToday() {
            return newUsersToday;
        }

        public void setNewUsersToday(Long newUsersToday) {
            this.newUsersToday = newUsersToday;
        }

        public Long getNewUsersThisWeek() {
            return newUsersThisWeek;
        }

        public void setNewUsersThisWeek(Long newUsersThisWeek) {
            this.newUsersThisWeek = newUsersThisWeek;
        }

        public Long getNewUsersThisMonth() {
            return newUsersThisMonth;
        }

        public void setNewUsersThisMonth(Long newUsersThisMonth) {
            this.newUsersThisMonth = newUsersThisMonth;
        }

        public Long getBannedUsers() {
            return bannedUsers;
        }

        public void setBannedUsers(Long bannedUsers) {
            this.bannedUsers = bannedUsers;
        }

        public Long getMutedUsers() {
            return mutedUsers;
        }

        public void setMutedUsers(Long mutedUsers) {
            this.mutedUsers = mutedUsers;
        }

        public Double getUserGrowthRate() {
            return userGrowthRate;
        }

        public void setUserGrowthRate(Double userGrowthRate) {
            this.userGrowthRate = userGrowthRate;
        }
    }

    /**
     * 商品统计数据
     */
    public static class ProductStats {
        private Long newProductsToday;     // 今日新增商品
        private Long newProductsThisWeek;  // 本周新增商品
        private Long newProductsThisMonth; // 本月新增商品
        private Long onSaleProducts;       // 在售商品数
        private Long soldProducts;         // 已售商品数
        private Long removedProducts;      // 已下架商品数
        private Double productGrowthRate;  // 商品增长率（月环比）

        public Long getNewProductsToday() {
            return newProductsToday;
        }

        public void setNewProductsToday(Long newProductsToday) {
            this.newProductsToday = newProductsToday;
        }

        public Long getNewProductsThisWeek() {
            return newProductsThisWeek;
        }

        public void setNewProductsThisWeek(Long newProductsThisWeek) {
            this.newProductsThisWeek = newProductsThisWeek;
        }

        public Long getNewProductsThisMonth() {
            return newProductsThisMonth;
        }

        public void setNewProductsThisMonth(Long newProductsThisMonth) {
            this.newProductsThisMonth = newProductsThisMonth;
        }

        public Long getOnSaleProducts() {
            return onSaleProducts;
        }

        public void setOnSaleProducts(Long onSaleProducts) {
            this.onSaleProducts = onSaleProducts;
        }

        public Long getSoldProducts() {
            return soldProducts;
        }

        public void setSoldProducts(Long soldProducts) {
            this.soldProducts = soldProducts;
        }

        public Long getRemovedProducts() {
            return removedProducts;
        }

        public void setRemovedProducts(Long removedProducts) {
            this.removedProducts = removedProducts;
        }

        public Double getProductGrowthRate() {
            return productGrowthRate;
        }

        public void setProductGrowthRate(Double productGrowthRate) {
            this.productGrowthRate = productGrowthRate;
        }
    }

    /**
     * 系统活动统计数据
     */
    public static class ActivityStats {
        private Long adminLoginCount;      // 管理员登录次数（今日）
        private Long auditLogCount;        // 审计日志数量（今日）
        private Long userLoginCount;       // 用户登录次数（今日）
        private Long systemErrors;         // 系统错误数（今日）
        private Double systemUptime;       // 系统正常运行时间百分比

        public Long getAdminLoginCount() {
            return adminLoginCount;
        }

        public void setAdminLoginCount(Long adminLoginCount) {
            this.adminLoginCount = adminLoginCount;
        }

        public Long getAuditLogCount() {
            return auditLogCount;
        }

        public void setAuditLogCount(Long auditLogCount) {
            this.auditLogCount = auditLogCount;
        }

        public Long getUserLoginCount() {
            return userLoginCount;
        }

        public void setUserLoginCount(Long userLoginCount) {
            this.userLoginCount = userLoginCount;
        }

        public Long getSystemErrors() {
            return systemErrors;
        }

        public void setSystemErrors(Long systemErrors) {
            this.systemErrors = systemErrors;
        }

        public Double getSystemUptime() {
            return systemUptime;
        }

        public void setSystemUptime(Double systemUptime) {
            this.systemUptime = systemUptime;
        }
    }

    /**
     * 趋势数据（用于图表展示）
     */
    public static class TrendData {
        private String date;               // 日期（YYYY-MM-DD格式）
        private Long value;                // 数值
        private String label;              // 标签描述

        public TrendData() {
        }

        public TrendData(String date, Long value, String label) {
            this.date = date;
            this.value = value;
            this.label = label;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    @Override
    public String toString() {
        return "DashboardStatsVO{" +
                "overview=" + overview +
                ", userStats=" + userStats +
                ", productStats=" + productStats +
                ", activityStats=" + activityStats +
                ", userTrend=" + (userTrend != null ? userTrend.size() + " items" : null) +
                ", productTrend=" + (productTrend != null ? productTrend.size() + " items" : null) +
                ", activityTrend=" + (activityTrend != null ? activityTrend.size() + " items" : null) +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
