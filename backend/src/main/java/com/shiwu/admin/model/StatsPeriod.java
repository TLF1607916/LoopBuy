package com.shiwu.admin.model;

/**
 * 统计时间段枚举
 */
public enum StatsPeriod {
    /**
     * 今天
     */
    TODAY("TODAY", "今天"),
    
    /**
     * 昨天
     */
    YESTERDAY("YESTERDAY", "昨天"),
    
    /**
     * 本周
     */
    THIS_WEEK("THIS_WEEK", "本周"),
    
    /**
     * 上周
     */
    LAST_WEEK("LAST_WEEK", "上周"),
    
    /**
     * 本月
     */
    THIS_MONTH("THIS_MONTH", "本月"),
    
    /**
     * 上月
     */
    LAST_MONTH("LAST_MONTH", "上月"),
    
    /**
     * 过去7天
     */
    LAST_7_DAYS("LAST_7_DAYS", "过去7天"),
    
    /**
     * 过去30天
     */
    LAST_30_DAYS("LAST_30_DAYS", "过去30天"),
    
    /**
     * 过去90天
     */
    LAST_90_DAYS("LAST_90_DAYS", "过去90天"),
    
    /**
     * 过去365天
     */
    LAST_365_DAYS("LAST_365_DAYS", "过去365天"),
    
    /**
     * 全部时间
     */
    ALL_TIME("ALL_TIME", "全部时间");
    
    private final String code;
    private final String description;
    
    StatsPeriod(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取时间段
     * @param code 时间段代码
     * @return 时间段枚举，如果不存在则返回null
     */
    public static StatsPeriod fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (StatsPeriod period : values()) {
            if (period.code.equals(code)) {
                return period;
            }
        }
        return null;
    }
}
