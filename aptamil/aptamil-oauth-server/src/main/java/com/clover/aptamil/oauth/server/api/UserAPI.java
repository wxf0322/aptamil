/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.oauth.server.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clover.aptamil.oauth.server.service.OAuthService;
import com.clover.aptamil.service.ApplicationService;
import com.clover.aptamil.service.GridService;
import com.clover.aptamil.service.OperationService;
import com.clover.aptamil.service.RoleService;
import com.clover.aptamil.service.StaffService;
import com.clover.aptamil.service.UserService;
import com.clover.aptamil.service.entity.ApplicationEntity;
import com.clover.aptamil.service.entity.GridEntity;
import com.clover.aptamil.service.entity.InstitutionEntity;
import com.clover.aptamil.service.entity.OperationEntity;
import com.clover.aptamil.service.entity.RoleEntity;
import com.clover.aptamil.service.entity.StaffEntity;
import com.clover.aptamil.service.entity.UserEntity;
import com.clover.aptamil.service.vo.GridVO;
import com.clover.aptamil.service.vo.InstitutionVO;
import com.clover.aptamil.service.vo.OperationVO;
import com.clover.aptamil.service.vo.StaffVO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 描述 用户信息API接口
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/4/25 8:50
 */
@RestController
@RequestMapping("/v1/openapi/user")
public class UserAPI {

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
     * @see ApplicationService
     */
    @Autowired
    private ApplicationService applicationService;

    /**
     * @see OperationService
     */
    @Autowired
    private OperationService operationService;

    /**
     * @see StaffService
     */
    @Autowired
    private StaffService staffService;

    /**
     * @see RoleService
     */
    @Autowired
    private RoleService roleService;

    /**
     * @see GridService
     */
    @Autowired
    private GridService gridService;

    /**
     * 获得员工JSON对象
     *
     * @param staffId
     * @return
     */
    private JSONObject getStaffJSONObject(Long staffId) {
        // 获得员工实体类
        StaffEntity staffEntity = staffService.findOne(staffId);
        if (staffEntity == null) return new JSONObject();
        // 获得用户model
        StaffVO staffVO = new StaffVO(staffEntity);
        JSONObject staffJson = JSONObject.fromObject(staffVO);
        return staffJson;
    }

    /**
     * 获得组织机构数组
     *
     * @param userId
     * @return
     */
    private JSONArray listInstitutions(Long userId) {
        UserEntity userEntity = userService.findOne(userId);
        // 构造机构信息
        List<InstitutionEntity> institutions = userEntity.getInstitutions();
        JSONArray instJsonArr = new JSONArray();
        for (InstitutionEntity institution : institutions) {
            InstitutionVO institutionVO = new InstitutionVO(institution);
            JSONObject instJson = JSONObject.fromObject(institutionVO);
            instJson.remove("enabled");
            instJsonArr.add(instJson);
        }
        return instJsonArr;
    }

    /**
     * 获得角色信息
     *
     * @param userId
     * @return
     */
    private JSONArray listRoles(Long userId) {
        List<RoleEntity> roles = roleService.listRolesByUserId(userId);
        JSONArray roleJsonArr = new JSONArray();
        for (RoleEntity role : roles) {
            if (role.getEnabled() == 0) continue;
            JSONObject roleJson = JSONObject.fromObject(role);
            roleJson.remove("enabled");
            roleJsonArr.add(roleJson);
        }
        return roleJsonArr;
    }


    /**
     * 设置 父节点名称-当前网格
     *
     * @param grids
     * @return
     */
    private List<GridVO> setGridParentName(List<GridVO> grids) {
        Map<Long, String> map = new HashMap<>();
        for (GridVO grid : grids) {
            map.put(grid.getId(), grid.getName());
        }
        return grids.stream().map(grid -> {
            Long parentId = grid.getParentId();
            String parentName = map.get(parentId);
            String name = grid.getName();
            if (StringUtils.isNotEmpty(parentName)) {
                grid.setName(parentName + "-" + name);
            }
            return grid;
        }).collect(Collectors.toList());
    }

    /**
     * 获得网格信息
     *
     * @param loginName
     * @return
     */
    private List<GridVO> listGrids(String loginName) {
        List<GridEntity> grids = gridService.listGridsByLoginName(loginName);
        List<GridVO> gridVOs = grids.stream().map(grid-> new GridVO(grid)).collect(Collectors.toList());
        return setGridParentName(gridVOs);
    }

    /**
     * 获得应用JSON数组
     *
     * @param clientId
     * @return
     */
    private JSONObject getApplicationJSONObject(Long userId, String clientId) {

        ApplicationEntity application = applicationService.getAppByClientId(clientId);

        JSONObject resultJson = new JSONObject();
        resultJson.put("id", application.getId());
        resultJson.put("label", application.getLabel());
        resultJson.put("name", application.getName());
        resultJson.put("clientId", application.getClientId());
        resultJson.put("clientSecret", application.getClientSecret());

        Long appId = application.getId();
        List<OperationEntity> operations = operationService.listOpersByUserIdAndAppId(userId, appId);

        // 构造操作
        JSONArray operJsonArr = new JSONArray();
        for (OperationEntity operation : operations) {
            OperationVO operationVO = new OperationVO(operation);
            JSONObject operJson = JSONObject.fromObject(operationVO);
            operJson.remove("enabled");
            operJson.remove("applicationId");
            operJsonArr.add(operJson);
        }

        resultJson.put("operations", operJsonArr);
        return resultJson;
    }

    /**
     * 获得用户信息，如果用户没有登录该系统返回空串
     *
     * @param request
     * @return
     * @throws OAuthSystemException
     * @throws OAuthProblemException
     */
    @RequestMapping(produces = "application/json; charset=UTF-8")
    public ResponseEntity getUser(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        // 构建OAuth资源请求
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);

        // 获取Access Token
        String accessToken = oauthRequest.getAccessToken();

        // 获取用户名
        String loginName = oAuthService.getLoginNameByAccessToken(accessToken);

        // 获取ClientId
        String clientId = oAuthService.getClientIdByAccessToken(accessToken);

        // 获得用户实体类
        UserEntity user = userService.getUserByLoginName(loginName);

        // 获得员工id
        Long staffId = (user.getStaffEntity() != null) ? user.getStaffEntity().getId() : null;

        // 获得相应的json对象
        JSONObject staffJson = getStaffJSONObject(staffId);
        JSONObject appJson = getApplicationJSONObject(user.getId(), clientId);
        JSONArray instJsonArr = listInstitutions(user.getId());
        JSONArray roleJsonArr = listRoles(user.getId());
        List<GridVO> gridJsonArr = listGrids(user.getLoginName());

        // 构造userJson
        JSONObject resultJson = new JSONObject();
        resultJson.put("id", user.getId());
        resultJson.put("name", user.getName());
        resultJson.put("loginName", user.getLoginName());
        resultJson.put("staff", staffJson);
        resultJson.put("institutions", instJsonArr);
        resultJson.put("roles", roleJsonArr);
        resultJson.put("grids", gridJsonArr);
        resultJson.put("application", appJson);

        return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
    }

}