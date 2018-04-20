/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.client.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clover.aptamil.code.util.HttpUtil;
import com.clover.aptamil.code.vo.ResultStatus;

import net.sf.json.JSONObject;

/**
 * 描述 通过code获得accessToken的servlet
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/7/12 上午11:12
 */
public class AccessTokenServlet extends HttpServlet {

    /**
     * @see Logger
     */
    private static Logger logger = LoggerFactory.getLogger(AccessTokenServlet.class);

    /**
     * 获取accessToken地址
     */
    private String accessTokenUrl;

    /**
     * 服务器地址
     */
    private String serverUrl;

    /**
     * 用户信息地址
     */
    private String userUrl;

    /**
     * clientId
     */
    private String clientId;

    /**
     * clientSecret
     */
    private String clientSecret;

    /**
     * 根据授权码获取accessToken
     *
     * @param authCode
     * @return accessToken响应
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    private OAuthAccessTokenResponse makeTokenRequestWithAuthCode(String authCode, String redirectUri)
            throws OAuthProblemException, OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(this.accessTokenUrl)
                .setClientId(this.clientId)
                .setClientSecret(this.clientSecret)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setCode(authCode)
                .setRedirectURI(redirectUri)
                .buildBodyMessage();
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthAccessTokenResponse oauthResponse = oAuthClient.accessToken(request);
        return oauthResponse;
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");

        this.serverUrl = this.getInitParameter("serverUrl");
        this.clientId = this.getInitParameter("clientId");
        this.clientSecret = this.getInitParameter("clientSecret");
        this.accessTokenUrl = serverUrl + "/accessToken";
        this.userUrl = serverUrl + "/v1/openapi/user";

        String code = request.getParameter("code");
        String redirectUri = URLDecoder.decode(request.getParameter("redirectUri"), "UTF-8");

        PrintWriter writer = response.getWriter();

        try {
            // 获得accessToken
            OAuthAccessTokenResponse accessTokenResponse =
                    this.makeTokenRequestWithAuthCode(code, redirectUri);

            String accessToken = accessTokenResponse.getAccessToken();
            Long expiresIn = accessTokenResponse.getExpiresIn();

            // 将 accessToken 存入session之中
            HttpSession session = request.getSession();
            session.setAttribute("accessToken", accessToken);

            String url = this.userUrl + "?access_token=" + accessToken;
            String html = HttpUtil.httpGet(url);
            JSONObject userInfo = JSONObject.fromObject(html);

            userInfo.put("accessToken", accessToken);
            userInfo.put("expiresIn", expiresIn);

            // 将用户信息返回给前端
            writer.write(userInfo.toString());
        } catch (OAuthProblemException | OAuthSystemException e) {
            logger.error(e.getMessage());

            // 设置响应信息
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResultStatus resultStatus = new ResultStatus(false, e.getMessage());
            JSONObject resultJson = JSONObject.fromObject(resultStatus);
            writer.write(resultJson.toString());
        }
    }

}
