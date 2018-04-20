/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.client.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clover.aptamil.code.vo.ResultStatus;

import net.sf.json.JSONObject;

/**
 * 描述 登出servlet
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/7/17 下午4:50
 */
public class LogoutServlet extends HttpServlet {

    /**
     * @see Logger
     */
    private static Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

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

        String logoutUrl = this.getInitParameter("logoutUrl");

        HttpSession session = request.getSession();
        String accessToken = (String) session.getAttribute("accessToken");

        URL url = new URL(logoutUrl + "?access_token=" + accessToken);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.disconnect();

        PrintWriter writer = response.getWriter();
        if (HttpServletResponse.SC_OK == conn.getResponseCode()) {
            logger.info("登出成功！");
            session.invalidate();
            ResultStatus resultStatus = new ResultStatus(true, "登出成功");
            JSONObject resultJson = JSONObject.fromObject(resultStatus);
            writer.write(resultJson.toString());
        } else {
            logger.info("登出失败！");
            ResultStatus resultStatus = new ResultStatus(false, "登出失败");
            JSONObject resultJson = JSONObject.fromObject(resultStatus);
            writer.write(resultJson.toString());
        }
    }

}

