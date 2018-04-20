/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.controller;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/7/13 下午5:32
 */
public class PasswordRequest {

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * clientId
     */
    private String clientId;

    /**
     * clientSecret
     */
    private String clientSecret;

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public PasswordRequest() {}

    public PasswordRequest(HttpServletRequest request) {
        this.clientId = request.getParameter("client_id");
        this.clientSecret = request.getParameter("client_secret");
        this.grantType = request.getParameter("grant_type");
        this.loginName = request.getParameter("login_name");
        this.password = request.getParameter("password");
    }
}
