package com.shiwu.user.vo;

import java.util.List;

/**
 * 关注动态信息流响应视图对象
 * 
 * 用于Task4_2_1_3: 获取关注动态信息流API
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class FeedResponseVO {
    
    private List<FeedItemVO> feeds;
    private PaginationVO pagination;
    
    public FeedResponseVO() {
    }
    
    public FeedResponseVO(List<FeedItemVO> feeds, PaginationVO pagination) {
        this.feeds = feeds;
        this.pagination = pagination;
    }
    
    public List<FeedItemVO> getFeeds() {
        return feeds;
    }
    
    public void setFeeds(List<FeedItemVO> feeds) {
        this.feeds = feeds;
    }
    
    public PaginationVO getPagination() {
        return pagination;
    }
    
    public void setPagination(PaginationVO pagination) {
        this.pagination = pagination;
    }
    
    @Override
    public String toString() {
        return "FeedResponseVO{" +
                "feeds=" + (feeds != null ? feeds.size() : 0) + " items" +
                ", pagination=" + pagination +
                '}';
    }
}
