/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.service;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * 描述 OAuthService
 *
 * @author wangxiaofeng
 * @version 2.0
 * @created 2017/4/25 8:50
 */
public interface OAuthService {

    /**
     * 添加 auth code
     *
     * @param authCode
     * @param loginName
     */
    void addAuthCode(String authCode, String loginName, String clientId);

    /**
     * 添加 access token
     *
     * @param accessToken
     * @param loginName
     * @param clientId
     */
    void addAccessToken(String accessToken, String loginName, String clientId);

    /**
     * 添加重定向地址
     *
     * @param redirectUrl
     * @param loginName
     * @param clientId
     */
    void addRedirectUri(String redirectUrl, String loginName, String clientId);

    /**
     * 根据关系获得重定向地址
     *
     * @param loginName
     * @param clientId
     * @return
     */
    String getRedirectUri(String loginName, String clientId);

    /**
     * 验证auth code是否有效
     *
     * @param authCode
     * @return
     */
    boolean checkAuthCode(String authCode);

    /**
     * 验证access token是否有效
     *
     * @param accessToken
     * @return
     */
    boolean checkAccessToken(String accessToken);

    /**
     * 通过 authCode 获得loginName
     *
     * @param authCode
     * @return
     */
    String getLoginNameByAuthCode(String authCode);

    /**
     * 通过accessToken获得loginName
     *
     * @param accessToken
     * @return
     */
    String getLoginNameByAccessToken(String accessToken);

    /**
     * 根据accessToke获取ClientId
     *
     * @param accessToken
     * @return
     */
    String getClientIdByAccessToken(String accessToken);

    /**
     * auth code / access token 过期时间
     *
     * @return
     */
    long getExpiresIn();

    /**
     * 判断 clientId 是否存在
     *
     * @param clientId
     * @return
     */
    boolean checkClientId(String clientId);

    /**
     * 判断 clientSecret 是否存在
     *
     * @param clientSecret
     * @return
     */
    boolean checkClientSecret(String clientSecret);

    /**
     * 判断clientId与clientSecret是否对应
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    boolean checkClient(String clientId, String clientSecret);

    /**
     * 删除授权码
     *
     * @param loginName
     * @param clientId
     */
    void deleteAuthCode(String loginName, String clientId);

    /**
     * 删除令牌
     *
     * @param loginName
     * @param clientId
     */
    void deleteAccessToken(String loginName, String clientId);

    /**
     * 删除重定向地址
     *
     * @param loginName
     * @param clientId
     */
    void deleteRedirectUri(String loginName, String clientId);

    /**
     * 删除当前用户所有的AccessToken
     *
     * @param loginName
     */
    void deleteAccountByLoginName(String loginName);

    /**
     * 获得新的AccessToken，并删除旧的AccessToken和AuthCode
     *
     * @param loginName
     * @param clientId
     * @param authCode
     * @return
     * @throws OAuthSystemException
     */
    String generateAccessToken(String loginName, String clientId, String authCode) throws OAuthSystemException;

    /**
     * 获得新的AccessToken，并删除旧的AccessToken
     *
     * @param loginName
     * @param clientId
     * @return
     */
    String generateAccessToken(String loginName, String clientId) throws OAuthSystemException;

    /**
     * 获得新的AuthCode，并删除旧的AuthCode
     *
     * @param loginName
     * @param clientId
     * @return
     * @throws OAuthSystemException
     */
    String generateAuthCode(String loginName, String clientId) throws OAuthSystemException;

    /**
     * 获得当前的授权码
     *
     * @param loginName
     * @param clientId
     * @return
     */
    String getAuthCode(String loginName, String clientId);

    /**
     * 获得当前的令牌
     *
     * @param loginName
     * @param clientId
     * @return
     */
    String getAccessToken(String loginName, String clientId);

}
