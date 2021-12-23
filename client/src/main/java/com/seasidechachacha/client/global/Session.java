package com.seasidechachacha.client.global;

import com.seasidechachacha.client.models.User;

public class Session {
    private static final Session g = new Session();
    private String userId;
    private int roleId;

    /**
     * Must only be used in Login, Logout
     * 
     * @param userId
     * @param roleId
     */
    public static void setUser(String userId, int roleId) {
        g.userId = userId;
        g.roleId = roleId;
    }

    /**
     * Must only be used in Login, Logout
     * 
     * @param user
     */
    public static void setUser(User user) {
        g.userId = user.getUserId();
        g.roleId = user.getRoleId();
    }

    public static User getUser() {
        return new User(g.userId, g.roleId);
    }
}
