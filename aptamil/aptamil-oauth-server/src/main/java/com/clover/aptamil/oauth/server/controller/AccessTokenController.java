/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.controller;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.service.constant.Constants;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/4/24 15:32
 */
@RestController
public class AccessTokenController {

    /**
     * 注入Service层
     */
    @Autowired
    private OAuthService oAuthService;

    @RequestMapping("/access")
    public ModelAndView access(Model model) {
        return new ModelAndView("access_token");
    }

    /**
     * client_id错误或者失效，返回400
     * client_secret错误或者失效，返回400
     * code错误返回401
     * 其他错误返回400
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     */
    @RequestMapping(value = "/accessToken", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public HttpEntity token(HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException {
        try {
            // 构建OAuth请求
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);

            String clientId = oauthRequest.getClientId();
            String clientSecret = oauthRequest.getClientSecret();

            // 检查提交的客户端id是否正确
            if (!oAuthService.checkClient(clientId, clientSecret)) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription(Constants.INVALID_CLIENT_ID)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            String authCode = oauthRequest.getParam(OAuth.OAUTH_CODE);
            // 检查验证类型，此处只检查 AUTHORIZATION_CODE 类型，其他的还有 PASSWORD 或 REFRESH_TOKEN
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString())) {
                if (!oAuthService.checkAuthCode(authCode)) {
                    OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                            .setError(OAuthError.TokenResponse.INVALID_GRANT)
                            .setErrorDescription(Constants.INVALID_AUTH_CODE)
                            .buildJSONMessage();
                    return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
                }
            }

            // 获取当前登录名
            String loginName = oAuthService.getLoginNameByAuthCode(authCode);

            // 检查重定向地址是否和上次的一样
            String preRedirectUri = oAuthService.getRedirectUri(loginName, clientId);

            if (!preRedirectUri.equals(oauthRequest.getRedirectURI())) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_REQUEST)
                        .setErrorDescription(Constants.DIFFERENT_REDIRECT_URI)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // 生成新的Access Token
            String accessToken = oAuthService.generateAccessToken(loginName, clientId, authCode);

            // 生成OAuth响应
            OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(accessToken)
                    .setExpiresIn(String.valueOf(oAuthService.getExpiresIn()))
                    .buildJSONMessage();

            //根据OAuthResponse生成ResponseEntity
            return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            // 构建错误响应
            OAuthResponse response = OAuthASResponse
                    .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .error(e)
                    .buildJSONMessage();
            return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    /**
     * 验证accessToken是否有效
     *
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/checkAccessToken")
    public ResponseEntity checkAccessToken(@RequestParam("access_token") String accessToken) {
        boolean flag = oAuthService.checkAccessToken(accessToken);
        return flag ? new ResponseEntity(HttpStatus.valueOf(HttpServletResponse.SC_OK)) :
                new ResponseEntity(HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
    }

}
