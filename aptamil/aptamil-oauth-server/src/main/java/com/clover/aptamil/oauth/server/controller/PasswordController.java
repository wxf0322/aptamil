/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.clover.aptamil.code.vo.ErrorStatus;
import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.service.PasswordHelper;
import com.clover.aptamil.service.UserService;
import com.clover.aptamil.service.constant.Constants;
import com.clover.aptamil.service.entity.UserEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/7/13 下午3:42
 */
@Controller
public class PasswordController {

    /**
     * @see Logger
     */
    private static Logger logger = LoggerFactory.getLogger(PasswordController.class);

    /**
     * @see OAuthService
     */
    @Autowired
    private OAuthService oAuthService;

    /**
     * @see UserService
     */
    @Autowired
    private UserService userService;

    /**
     * @see PasswordHelper
     */
    @Autowired
    private PasswordHelper passwordHelper;

    /**
     * 密码授权接口
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "token", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity passwordGrant(HttpServletRequest request) {
        try {
            PasswordRequest passwordRequest = new PasswordRequest(request);
            String clientId = passwordRequest.getClientId();
            String clientSecret = passwordRequest.getClientSecret();
            // 检查提交的客户端id是否正确
            if (!oAuthService.checkClient(clientId, clientSecret)) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription(Constants.INVALID_CLIENT_ID)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // 检查grant_type是否正确
            if (!"password".equalsIgnoreCase(passwordRequest.getGrantType())) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.OAUTH_ERROR)
                        .setErrorDescription(Constants.INVALID_PARAMS)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            String loginName = passwordRequest.getLoginName();
            String password = passwordRequest.getPassword();

            UserEntity user = userService.getUserByLoginName(loginName);
            if (user == null) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.OAUTH_ERROR)
                        .setErrorDescription(Constants.INCORRECT_CREDENTIALS)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // 加密过后的密码
            String encryptPwd = passwordHelper.encryptPassword(loginName, password, user.getSalt());

            // 传入加密过的密码
            UsernamePasswordToken token = new UsernamePasswordToken(loginName, encryptPwd);
            Subject subject = SecurityUtils.getSubject();

            String error = null;
            try {
                subject.login(token);
            } catch (IncorrectCredentialsException e) {
                error = Constants.INCORRECT_CREDENTIALS;
            } catch (UnknownAccountException e) {
                error = Constants.INCORRECT_CREDENTIALS;
            } catch (LockedAccountException e) {
                error = Constants.LOCKED_ACCOUNT;
            } catch (AuthenticationException e) {
                error = e.getMessage();
            }

            // 判断error是否为空，如果为空生成accessToken，否则返回错误信息
            if (error == null) {
                String accessToken = oAuthService.generateAccessToken(loginName, clientId);
                // 生成OAuth响应
                OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                        .setAccessToken(accessToken)
                        .setExpiresIn(String.valueOf(oAuthService.getExpiresIn()))
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            } else {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.OAUTH_ERROR)
                        .setErrorDescription(error)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
        } catch (OAuthSystemException e) {
            logger.error(e.getMessage(), e);
            ErrorStatus errorStatus = new ErrorStatus
                    .Builder(ErrorStatus.INVALID_PARAMS, Constants.INVALID_PARAMS)
                    .buildJSONMessage();
            return new ResponseEntity(errorStatus.getBody(), HttpStatus.BAD_REQUEST);
        }

    }

}

