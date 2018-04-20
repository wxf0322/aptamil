/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.service.impl;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.service.ApplicationService;

/**
 * 描述 OAuth Service层实现
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/4/25 8:52
 */
@Transactional
@Service
public class OAuthServiceImpl implements OAuthService {

    /**
     * 令牌有效时间
     */
    @Value("${accessToken.expires}")
    private Long accessTokenExpires;

    /**
     * 授权码有效时间
     */
    @Value("${authCode.expires}")
    private Long authCodeExpires;

    /**
     * @see ApplicationService
     */
    @Autowired
    private ApplicationService applicationService;

    /**
     * redis模板对象
     */
    private RedisTemplate<String, String> redisTemplate;

    /**
     * hash操作对象
     */
    private HashOperations<String, String, String> hashOperations;

    /**
     * redis值操作对象
     */
    private ValueOperations<String, String> valueOperations;

    /**
     * 构造函数依赖注入redisTemplate
     *
     * @param redisTemplate
     */
    @Autowired
    public OAuthServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 构建关联关系KEY
     *
     * @param loginName
     * @param clientId
     * @return
     */
    private String getRelationKey(String paramName, String loginName, String clientId) {
        StringBuilder sb = new StringBuilder();
        String relation = sb.append(paramName).append(":")
                .append(loginName).append(":").append(clientId)
                .toString();
        return relation;
    }

    /**
     * 存入授权码
     *
     * @param authCode
     * @param loginName
     * @param clientId
     */
    @Override
    public void addAuthCode(String authCode, String loginName, String clientId) {
        // 获得关联关系KEY
        String relationKey = getRelationKey(OAuth.OAUTH_CODE, loginName, clientId);

        // 设置authCode
        valueOperations.set(relationKey, authCode, authCodeExpires, TimeUnit.SECONDS);
        valueOperations.set(authCode, loginName, authCodeExpires, TimeUnit.SECONDS);
    }

    /**
     * 存入令牌
     *
     * @param accessToken
     * @param loginName
     * @param clientId
     */
    @Override
    public void addAccessToken(String accessToken, String loginName, String clientId) {
        // 获得关联关系KEY
        String relationKey = getRelationKey(OAuth.OAUTH_ACCESS_TOKEN, loginName, clientId);

        // 设置Access Token
        valueOperations.set(relationKey, accessToken, accessTokenExpires, TimeUnit.SECONDS);

        // 存入与Access Token相关的值
        hashOperations.put(accessToken, "loginName", loginName);
        hashOperations.put(accessToken, "clientId", clientId);
        redisTemplate.expire(accessToken, accessTokenExpires, TimeUnit.SECONDS);
    }

    @Override
    public void addRedirectUri(String redirectUrl, String loginName, String clientId) {
        // 获得关联关系KEY
        String relationKey = getRelationKey(OAuth.OAUTH_REDIRECT_URI, loginName, clientId);
        // 设置重定向地址
        valueOperations.set(relationKey, redirectUrl, authCodeExpires, TimeUnit.SECONDS);
    }

    @Override
    public String getRedirectUri(String loginName, String clientId) {
        String relationKey = getRelationKey(OAuth.OAUTH_REDIRECT_URI, loginName, clientId);
        return valueOperations.get(relationKey);
    }

    /**
     * 根据授权码获取相对应的用户名
     *
     * @param authCode
     * @return
     */
    @Override
    public String getLoginNameByAuthCode(String authCode) {
        return valueOperations.get(authCode);
    }

    /**
     * 根据令牌获取相对应的用户名
     *
     * @param accessToken
     * @return
     */
    @Override
    public String getLoginNameByAccessToken(String accessToken) {
        return hashOperations.get(accessToken, "loginName");
    }

    /**
     * 根据令牌获取ClientId
     *
     * @param accessToken
     * @return
     */
    @Override
    public String getClientIdByAccessToken(String accessToken) {
        return hashOperations.get(accessToken, "clientId");
    }

    /**
     * 检查授权码是否存在
     *
     * @param authCode
     * @return
     */
    @Override
    public boolean checkAuthCode(String authCode) {
        return valueOperations.get(authCode) != null;
    }

    /**
     * 检查令牌是否存在
     *
     * @param accessToken
     * @return
     */
    @Override
    public boolean checkAccessToken(String accessToken) {
        return hashOperations.get(accessToken, "loginName") != null;
    }

    /**
     * 检查ClientId是否存在
     *
     * @param clientId
     * @return
     */
    @Override
    public boolean checkClientId(String clientId) {
        return applicationService.getAppByClientId(clientId) != null;
    }

    /**
     * 检查ClientSecret是否存在
     *
     * @param clientSecret
     * @return
     */
    @Override
    public boolean checkClientSecret(String clientSecret) {
        return applicationService.getAppByClientSecret(clientSecret) != null;
    }

    /**
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public boolean checkClient(String clientId, String clientSecret) {
        return applicationService.getApplication(clientId, clientSecret) != null;
    }

    /**
     * 获得令牌超时时间
     *
     * @return
     */
    @Override
    public long getExpiresIn() {
        return accessTokenExpires;
    }

    /**
     * 删除关联关系所对应的值
     *
     * @param paramName
     * @param loginName
     * @param clientId
     */
    private void deleteRelation(String paramName, String loginName, String clientId) {
        // 获得关联关系
        String relationKey = getRelationKey(paramName, loginName, clientId);
        String relationValue = valueOperations.get(relationKey);
        // 删除关系KEY
        if (StringUtils.isNotEmpty(relationKey)) redisTemplate.delete(relationKey);
        // 删除关系值
        if (StringUtils.isNotEmpty(relationValue)) redisTemplate.delete(relationValue);
    }

    /**
     * 删除授权码
     *
     * @param loginName
     * @param clientId
     */
    @Override
    public void deleteAuthCode(String loginName, String clientId) {
        deleteRelation(OAuth.OAUTH_CODE, loginName, clientId);
    }

    /**
     * 删除令牌
     *
     * @param loginName
     * @param clientId
     */
    @Override
    public void deleteAccessToken(String loginName, String clientId) {
        deleteRelation(OAuth.OAUTH_ACCESS_TOKEN, loginName, clientId);
    }

    /**
     * 删除重定向地址
     *
     * @param loginName
     * @param clientId
     */
    @Override
    public void deleteRedirectUri(String loginName, String clientId) {
        // 获得关联关系KEY
        String relationKey = getRelationKey(OAuth.OAUTH_REDIRECT_URI, loginName, clientId);
        // 删除关系KEY
        if (StringUtils.isNotEmpty(relationKey)) redisTemplate.delete(relationKey);
    }

    /**
     * 根据用户名删除该用户所有的AccessToken
     *
     * @param loginName
     */
    @Override
    public void deleteAccountByLoginName(String loginName) {
        String relationKey = getRelationKey("*", loginName, "*");
        Set<String> keySet = redisTemplate.keys(relationKey);
        keySet.forEach(key -> {
            String value = valueOperations.get(key);
            redisTemplate.delete(value);
            redisTemplate.delete(key);
        });
    }

    /**
     * 获得新的AccessToken，并删除旧的AccessToken和已获得的AuthCode
     *
     * @param loginName
     * @param clientId
     * @param authCode
     * @return
     * @throws OAuthSystemException
     */
    @Override
    public String generateAccessToken(String loginName, String clientId, String authCode) throws OAuthSystemException {
        OAuthIssuer oAuthIssuer = new OAuthIssuerImpl(new MD5Generator());
        String accessToken = oAuthIssuer.accessToken();
        // 删除旧的Access Token
        deleteAccessToken(loginName, clientId);
        // 生成新的Access Token
        addAccessToken(accessToken, loginName, clientId);
        // 得到新的Access Token之后，删除授权码
        deleteAuthCode(loginName, clientId);
        // 删除重定向地址
        deleteRedirectUri(loginName, clientId);
        return accessToken;
    }

    @Override
    public String generateAccessToken(String loginName, String clientId) throws OAuthSystemException {
        OAuthIssuer oAuthIssuer = new OAuthIssuerImpl(new MD5Generator());
        String accessToken = oAuthIssuer.accessToken();
        // 删除旧的Access Token
        deleteAccessToken(loginName, clientId);
        // 生成新的Access Token
        addAccessToken(accessToken, loginName, clientId);
        return accessToken;
    }

    /**
     * 删除旧的AuthCode，并获得新的AuthCode
     *
     * @param loginName
     * @param clientId
     * @return
     * @throws OAuthSystemException
     */
    @Override
    public String generateAuthCode(String loginName, String clientId) throws OAuthSystemException {
        OAuthIssuerImpl oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
        String authCode = oauthIssuer.authorizationCode();
        // 删除旧的授权码
        deleteAuthCode(loginName, clientId);
        // 生成新的Access Token
        addAuthCode(authCode, loginName, clientId);
        return authCode;
    }

    /**
     * 返回当前用户的授权码
     *
     * @param loginName
     * @param clientId
     * @return
     */
    @Override
    public String getAuthCode(String loginName, String clientId) {
        String relationKey = getRelationKey(OAuth.OAUTH_CODE, loginName, clientId);
        String authCode = valueOperations.get(relationKey);
        return authCode;
    }

    /**
     * 获得当前的令牌
     *
     * @param loginName
     * @param clientId
     * @return
     */
    @Override
    public String getAccessToken(String loginName, String clientId) {
        String relationKey = getRelationKey(OAuth.OAUTH_ACCESS_TOKEN, loginName, clientId);
        String accessToken = valueOperations.get(relationKey);
        return accessToken;
    }
}

