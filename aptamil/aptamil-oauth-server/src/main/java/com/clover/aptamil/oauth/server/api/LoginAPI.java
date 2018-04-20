/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clover.aptamil.code.vo.ResultStatus;
import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.oauth.server.shiro.ShiroSecurityHelper;

import net.sf.json.JSONObject;

/**
 * 描述 登录相关控制器
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/3 16:21
 */
@Controller
@RequestMapping("/v1/openapi/")
public class LoginAPI {

    /**
     * @see Logger
     */
    private static Logger logger = LoggerFactory.getLogger(LoginAPI.class);

    /**
     * @see OAuthService
     */
    @Autowired
    private OAuthService oAuthService;

    /**
     * @see ShiroSecurityHelper
     */
    @Autowired
    private ShiroSecurityHelper shiroSecurityHelper;

    /**
     * 登出接口
     *
     * @param request
     * @return
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    @ResponseBody
    @RequestMapping(value = "logout", produces = "application/json; charset=UTF-8")
    public ResponseEntity logout(HttpServletRequest request) throws OAuthProblemException, OAuthSystemException {
        // 构建OAuth资源请求
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);

        // 获取Access Token
        String accessToken = oauthRequest.getAccessToken();

        // 获取用户
        String loginName = oAuthService.getLoginNameByAccessToken(accessToken);
        try {
            shiroSecurityHelper.kickOutUser(loginName);
            oAuthService.deleteAccountByLoginName(loginName);
            logger.info("[{}] 登出系统成功！", loginName);
            ResultStatus resultStatus = new ResultStatus(true, "登出成功");
            JSONObject resultJson = JSONObject.fromObject(resultStatus);
            return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
        } catch (Exception e) {
            logger.info("[{}] 登出系统失败！", loginName);
            ResultStatus resultStatus = new ResultStatus(false, "登出失败");
            JSONObject resultJson = JSONObject.fromObject(resultStatus);
            return new ResponseEntity(resultJson.toString(), HttpStatus.BAD_REQUEST);
        }
    }

}

