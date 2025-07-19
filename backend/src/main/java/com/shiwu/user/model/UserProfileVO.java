package com.shiwu.user.model;

import com.shiwu.product.model.ProductCardVO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户公开信息视图对象
 * 用于UC-02: View User Profile
 */
public class UserProfileVO {
    private UserVO user;
    private Integer followerCount;
    private BigDecimal averageRating;
    private List<ProductCardVO> onSaleProducts;
    private Boolean isFollowing; // 当前用户是否关注了该用户
    private LocalDateTime registrationDate;

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public List<ProductCardVO> getOnSaleProducts() {
        return onSaleProducts;
    }

    public void setOnSaleProducts(List<ProductCardVO> onSaleProducts) {
        this.onSaleProducts = onSaleProducts;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
