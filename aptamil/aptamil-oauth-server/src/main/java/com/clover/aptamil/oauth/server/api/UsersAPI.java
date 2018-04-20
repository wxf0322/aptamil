/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.clover.aptamil.code.vo.ErrorStatus;
import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.service.InstitutionService;
import com.clover.aptamil.service.UserService;
import com.clover.aptamil.service.constant.Constants;
import com.clover.aptamil.service.entity.InstitutionEntity;
import com.clover.aptamil.service.entity.UserEntity;
import com.clover.aptamil.service.vo.InstitutionVO;
import com.clover.aptamil.service.vo.UserVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 描述 返回用户列表API接口
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/8 9:00
 */
@Controller
public class UsersAPI {

    /**
     * @see UserService
     */
    @Autowired
    private UserService userService;

    /**
     * @see InstitutionService
     */
    @Autowired
    private InstitutionService institutionService;

    /**
     * @see OAuthService
     */
    @Autowired
    private OAuthService oAuthService;

    /**
     * 获取用户列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/openapi/users", produces = "application/json; charset=UTF-8")
    public ResponseEntity listUsers(HttpServletRequest request) {
        List<UserEntity> users = null;
        //获取管辖区域编码
        String gridCode = request.getParameter("grid");
        if (StringUtils.isNotEmpty(gridCode)) {
            users = userService.listUsersByGridCode(gridCode);
        }
        //获取组织机构编码
        String instName = request.getParameter("institution");
        if (StringUtils.isNotEmpty(instName)) {
            users = userService.listUsersByInstName(instName);
        }
        //获取应用编码
        String appName = request.getParameter("application");
        if (StringUtils.isNotEmpty(appName)) {
            users = userService.listUsersByAppName(appName);
        }
        //获取权限编码
        String privName = request.getParameter("privilege");
        if (StringUtils.isNotEmpty(privName)) {
            users = userService.listUsersByPrivName(privName);
        }
        //获取操作编码
        String operName = request.getParameter("operation");
        if (StringUtils.isNotEmpty(operName)) {
            users = userService.listUsersByOperName(operName);
        }
        //获取角色编码
        String roleName = request.getParameter("role");
        if (StringUtils.isNotEmpty(roleName)) {
            users = userService.listUsersByRoleName(roleName);
        }
        String officalPost = request.getParameter("offical_post");
        if (StringUtils.isNotEmpty(officalPost)) {
            users = userService.listUsersByOfficalPost(officalPost);
        }
        JSONObject usersJson = new JSONObject();
        if (users == null) {
            ErrorStatus errorStatus = new ErrorStatus
                    .Builder(ErrorStatus.INVALID_PARAMS, Constants.INVALID_PARAMS)
                    .buildJSONMessage();
            return new ResponseEntity(errorStatus.getBody(), HttpStatus.BAD_REQUEST);
        } else {
            usersJson.put("users", getUsersJSONArray(users));
            return new ResponseEntity(usersJson.toString(), HttpStatus.OK);
        }
    }

    /**
     * 内部接口获得用户列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/internalapi/users", produces = "application/json; charset=UTF-8")
    public ResponseEntity listUsersForMessageCenter(HttpServletRequest request) {
        List<UserEntity> users = null;
        String loginNames = request.getParameter("login_names");
        if (StringUtils.isNotEmpty(loginNames)) {
            users = userService.listUsersByLoginNames(loginNames.split(","));
        }
        String roleNames = request.getParameter("roles");
        if (StringUtils.isNotEmpty(roleNames)) {
            users = userService.listUsersByRoleNames(roleNames.split(","));
        }
        String instNames = request.getParameter("institutions");
        if (StringUtils.isNotEmpty(instNames)) {
            users = userService.listUsersByInstNames(instNames.split(","));
        }
        JSONObject usersJson = new JSONObject();
        if (users == null) {
            ErrorStatus errorStatus = new ErrorStatus
                    .Builder(ErrorStatus.INVALID_PARAMS, Constants.INVALID_PARAMS)
                    .buildJSONMessage();
            return new ResponseEntity(errorStatus.getBody(), HttpStatus.BAD_REQUEST);
        } else {
            usersJson.put("users", getUsersJSONArray(users));
            return new ResponseEntity(usersJson.toString(), HttpStatus.OK);
        }
    }

    /**
     * 查询用户基本信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/internalapi/user", produces = "application/json; charset=UTF-8")
    public ResponseEntity getUserForTaskCenter(HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        String loginName = request.getParameter("login_name");
        String userId = request.getParameter("user_id");

        // 判断client_id是否有效
        if (!oAuthService.checkClientId(clientId)) {
            ErrorStatus errorStatus = new ErrorStatus
                    .Builder(ErrorStatus.INVALID_PARAMS, Constants.INVALID_PARAMS)
                    .buildJSONMessage();
            return new ResponseEntity(errorStatus.getBody(), HttpStatus.BAD_REQUEST);
        }

        // 如果用户名已经用户ID同时为空，返回参数无效的json
        if (StringUtils.isEmpty(loginName) && StringUtils.isEmpty(userId)) {
            ErrorStatus errorStatus = new ErrorStatus
                    .Builder(ErrorStatus.INVALID_PARAMS, Constants.INVALID_PARAMS)
                    .buildJSONMessage();
            return new ResponseEntity(errorStatus.getBody(), HttpStatus.BAD_REQUEST);
        }

        UserEntity user;
        if (StringUtils.isEmpty(loginName)) {
            // 根据id，获得用户实体类
            user = userService.findOne(Long.valueOf(userId));
        } else {
            // 根据登录名，获得用户实体类
            user = userService.getUserByLoginName(loginName);
        }
        // 如果用户为空返回用户不存在的信息
        if (user == null) {
            ErrorStatus errorStatus = new ErrorStatus
                    .Builder("error", Constants.UNKNOWN_ACCOUNT)
                    .buildJSONMessage();
            return new ResponseEntity(errorStatus.getBody(), HttpStatus.BAD_REQUEST);
        }
        loginName = user.getLoginName();
        // 获得accessToken
        String accessToken = oAuthService.getAccessToken(loginName, clientId);

        JSONObject resultJson = new JSONObject();
        JSONObject userJson;
        if (StringUtils.isEmpty(accessToken)) {
            userJson = new JSONObject();
        } else {
            userJson = getUserJSONObject(user);
            userJson.put("accessToken", accessToken);
        }
        resultJson.put("user", userJson);
        return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
    }

    /**
     * 转换用户数据为JSONArray
     *
     * @param users
     * @return
     */
    private JSONArray getUsersJSONArray(List<UserEntity> users) {
        if (users == null) {
            return new JSONArray();
        } else {
            JSONArray usersJsonArr = new JSONArray();
            for (UserEntity user : users) {
                JSONObject userJson = getUserJSONObject(user);
                usersJsonArr.add(userJson);
            }
            return usersJsonArr;
        }
    }

    /**
     * 获得单个用户json数据
     *
     * @param user
     * @return
     */
    private JSONObject getUserJSONObject(UserEntity user) {
        UserVO userVO = new UserVO(user);
        JSONObject userJson = JSONObject.fromObject(userVO);
        userJson.remove("enabled");
        userJson.remove("roleNames");
        userJson.remove("institutionNames");
        userJson.remove("institutionIds");
        return userJson;
    }

    /**
     * 根据用户名模糊查找，获取用户列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/v1/openapi/users/like", produces = "application/json; charset=UTF-8")
    public ResponseEntity listUsersByNameLike(HttpServletRequest request) {
        String name = request.getParameter("name");
        if (StringUtils.isEmpty(name)) name = "";
        //获取名字关键字
        name = "%" + name + "%";
        List<UserEntity> users = userService.listUsersByNameLike(name);

        // 构建用户json数据
        JSONArray userJsonArr = new JSONArray();
        for (UserEntity user : users) {
            // 获得userVO的json数据
            JSONObject userJson = getUserJSONObject(user);
            // 获得组织机构的json数据
            Long userId = user.getId();
            List<InstitutionEntity> insts = institutionService.listInstsByUserId(userId);
            JSONArray instJsonArr = new JSONArray();
            for (InstitutionEntity inst : insts) {
                InstitutionVO instVO = new InstitutionVO(inst);
                instJsonArr.add(JSONObject.fromObject(instVO));
            }
            // 将组织机构json数据存入userVO之中
            userJson.put("institutions", instJsonArr);
            userJsonArr.add(userJson);
        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("users", userJsonArr);
        return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
    }

}

