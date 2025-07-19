package com.shiwu.user.model;

/**
 * 关注状态视图对象
 * 用于返回用户之间的关注关系信息
 */
public class FollowStatusVO {
    private Long userId; // 目标用户ID
    private String username; // 目标用户名
    private String nickname; // 目标用户昵称
    private Boolean isFollowing; // 当前用户是否关注了目标用户
    private Integer followerCount; // 目标用户的粉丝数量
    private Integer followingCount; // 目标用户关注的人数

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }
}
