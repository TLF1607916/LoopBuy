package com.shiwu.user.vo;

/**
 * 分页信息视图对象
 * 
 * @author LoopBuy Team
 * @version 1.0
 * @since 2024-01-15
 */
public class PaginationVO {
    
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrev;
    
    public PaginationVO() {
    }
    
    public PaginationVO(int page, int size, long total) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.hasNext = page < totalPages;
        this.hasPrev = page > 1;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrev() {
        return hasPrev;
    }
    
    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }
    
    @Override
    public String toString() {
        return "PaginationVO{" +
                "page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", hasNext=" + hasNext +
                ", hasPrev=" + hasPrev +
                '}';
    }
}
