/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.oauth.server.shiro.ShiroSecurityHelper;
import com.clover.aptamil.service.PasswordHelper;
import com.clover.aptamil.service.UserService;
import com.clover.aptamil.service.constant.Constants;
import com.clover.aptamil.service.entity.UserEntity;

import net.evecom.gsmp.srv.log.op.app.annotation.Log;

/**
 * 描述 权限Controller层
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/4/24 15:38
 */
@Controller
public class AuthorizeController {

    /**
     * @see Logger
     */
    private static Logger logger = LoggerFactory.getLogger(AuthorizeController.class);

    /**
     * 令牌有效时间
     */
    @Value("${accessToken.expires}")
    private Long accessTokenExpires;

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
     * @see ShiroSecurityHelper
     */
    @Autowired
    private ShiroSecurityHelper shiroSecurityHelper;

    /**
     * 获得授权码，支持授权码模式，与密码模式
     * client_id错误或者失效，返回400
     * 重定向成功，返回302
     * redirect_url未空，返回404
     * 其他错误，返回400
     *
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws OAuthSystemException
     */
    @Log
    @RequestMapping(value = "/authorize", produces = "application/json; charset=UTF-8")
    public Object authorize(HttpServletRequest request) throws URISyntaxException, OAuthSystemException {
        try {
            // 构建 OAuth 授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);

            // 获得clientId
            String clientId = oauthRequest.getClientId();

            // 检查传入的clientId是否正确
            if (!oAuthService.checkClientId(clientId)) {
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription(Constants.INVALID_CLIENT_ID)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // 获得相应类型
            String responseType = oauthRequest.getResponseType();

            if (responseType.equals(ResponseType.CODE.toString())) {
                return authCodeGrant(request);
            } else if (responseType.equals(ResponseType.TOKEN.toString())) {
                return implicitGrant(request);
            }

            OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                    .setErrorDescription(Constants.INVALID_PARAMS)
                    .buildJSONMessage();
            return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
        } catch (OAuthProblemException e) {
            // 无重定向出错
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
                //告诉客户端没有传入redirectUri
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_REQUEST)
                        .setErrorDescription(Constants.INVALID_REDIRECT_URI)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // 无相应类型出错
            String responseType = e.get("response_type");
            if (OAuthUtils.isEmpty(responseType)) {
                //告诉客户端没有传入responseType
                OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_REQUEST)
                        .setErrorDescription(Constants.INVALID_RESPONSE_TYPE)
                        .buildJSONMessage();
                return new ResponseEntity(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }

            // 返回错误消息（如?error=）
            OAuthResponse response = OAuthASResponse
                    .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .error(e)
                    .location(redirectUri)
                    .buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    /**
     * 授权码模式
     *
     * @param request
     * @return
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     * @throws URISyntaxException
     */
    private Object authCodeGrant(HttpServletRequest request)
            throws OAuthSystemException, OAuthProblemException, URISyntaxException {
        OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);

        Subject subject = SecurityUtils.getSubject();
        UserEntity user = (UserEntity) subject.getSession().getAttribute("user");

        // 如果session中不存在该用户，且用户没有登录，或者登录失败，跳转到登陆页面
        if (user == null && !login(request)) {
            return "oauth2_login";
        }

        // 获取用户名
        String loginName = (user == null) ?
                request.getParameter("loginName") : user.getLoginName();

        // 获得clientId
        String clientId = oauthRequest.getClientId();

        // 生成授权码
        String authCode = oAuthService.generateAuthCode(loginName, clientId);

        // 进行OAuth响应构建
        OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);

        // 设置授权码
        builder.setCode(authCode);

        // 得到到客户端重定向地址
        String redirectURI = oauthRequest.getRedirectURI();

        // 构建响应
        OAuthResponse response = builder.location(redirectURI).buildQueryMessage();

        // 如果响应构建成功，那么获得重定向地址，并存入Redis
        String redirectUri = oauthRequest.getRedirectURI();
        oAuthService.addRedirectUri(redirectUri, loginName, clientId);

        // 根据OAuthResponse返回ResponseEntity响应
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(response.getLocationUri()));

        return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
    }

    /**
     * 简化模式
     *
     * @param request
     * @return
     */
    private Object implicitGrant(HttpServletRequest request)
            throws OAuthProblemException, OAuthSystemException, URISyntaxException {
        OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);

        Subject subject = SecurityUtils.getSubject();
        UserEntity user = (UserEntity) subject.getSession().getAttribute("user");

        // 如果session中不存在该用户，且用户没有登录，或者登录失败，跳转到登陆页面
        if (user == null && !login(request)) {
            return "oauth2_login";
        }

        // 获取用户名
        String loginName = (user == null) ?
                request.getParameter("loginName") : user.getLoginName();

        // 获得clientId
        String clientId = oauthRequest.getClientId();

        // 获得redirectUri
        String redirectUri = oauthRequest.getRedirectURI();

        OAuthASResponse.OAuthTokenResponseBuilder builder =
                OAuthASResponse.tokenResponse(HttpServletResponse.SC_FOUND);

        String accessToken = oAuthService.generateAccessToken(loginName, clientId);

        builder.setAccessToken(accessToken);
        builder.setExpiresIn(accessTokenExpires.toString());

        // 构建响应
        OAuthResponse response = builder.location(redirectUri).buildQueryMessage();

        // 根据OAuthResponse返回ResponseEntity响应
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(response.getLocationUri()));

        return new ResponseEntity(headers, HttpStatus.valueOf(response.getResponseStatus()));
    }

    /**
     * 登录判断
     *
     * @param request
     * @return
     */
    private boolean login(HttpServletRequest request) {
        // loginName 与 password 只能用post请求提交
        if ("get".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");

        if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)) {
            request.setAttribute("error", Constants.EMPTY_ACCOUNT_OR_PASSWORD);
            return false;
        }

        UserEntity user = userService.getUserByLoginName(loginName);
        if (user == null) {
            request.setAttribute("error", Constants.UNKNOWN_ACCOUNT);
            return false;
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

        if (error != null) {
            request.setAttribute("error", error);
            return false;
        } else {
            shiroSecurityHelper.kickOutUser(user.getLoginName());

            // 设置session
            Session session = subject.getSession(true);
            session.setAttribute("user", user);

            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("applicationName", "net.evecom.common.usms");
            httpSession.setAttribute("applicationLabel", "统一用户管理系统");
            httpSession.setAttribute("userId", user.getId());
            httpSession.setAttribute("userName", user.getName());

            logger.info("[{}]用户登录成功！", loginName);
            return true;
        }
    }

}
