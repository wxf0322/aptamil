/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clover.aptamil.code.util.WebUtil;
import com.clover.aptamil.service.constant.Constants;

/**
 * 描述 Oauth2全局过滤器
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/4/25 8:49
 */
public class OAuth2Filter implements Filter {

    /**
     * 日志管理器
     */
    private static Logger logger = LoggerFactory.getLogger(OAuth2Filter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            //构建OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(
                    (HttpServletRequest) request, ParameterStyle.QUERY);

            // 活动Access Token
            String accessToken = oauthRequest.getAccessToken();

            //验证Access Token
            if (!checkAccessToken((HttpServletRequest) request, accessToken)) {
                // 如果不存在/过期了，返回未验证错误，需重新验证
                oAuthFailResponse(httpResponse);
            }else {
                chain.doFilter(request, response);
            }
        } catch (OAuthProblemException e) {
            try {
                oAuthFailResponse(httpResponse);
            } catch (OAuthSystemException ex) {
                logger.error("error trying to access oauth server", ex);
            }
        } catch (OAuthSystemException e) {
            logger.error("error trying to access oauth server", e);
        }
    }

    /**
     * oAuth认证失败时的输出
     * AccessToken错误，或者失效，返回401
     * @param response
     * @throws OAuthSystemException
     * @throws IOException
     */
    private void oAuthFailResponse(HttpServletResponse response) throws OAuthSystemException, IOException {
        OAuthResponse oauthResponse = OAuthRSResponse
                .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                .setErrorDescription(Constants.INVALID_ACCESS_TOKEN)
                .buildJSONMessage();

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(oauthResponse.getResponseStatus());
        PrintWriter writer = response.getWriter();
        writer.write(oauthResponse.getBody());
        writer.flush();
        writer.close();
    }

    /**
     * 验证accessToken
     *
     * @param accessToken
     * @return
     * @throws IOException
     */
    private boolean checkAccessToken(HttpServletRequest request, String accessToken) throws IOException {
        String checkAccessCodeUrl = WebUtil.getBasePath(request) + "checkAccessToken?access_token=";
        URL url = new URL(checkAccessCodeUrl + accessToken);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.disconnect();
        return HttpServletResponse.SC_OK == conn.getResponseCode();
    }

    @Override
    public void destroy() {
    }

}
