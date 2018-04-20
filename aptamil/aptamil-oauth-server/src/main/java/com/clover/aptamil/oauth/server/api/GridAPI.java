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
import com.clover.aptamil.service.GridService;
import com.clover.aptamil.service.entity.GridEntity;
import com.clover.aptamil.service.vo.GridVO;

import net.sf.json.JSONObject;

/**
 * 描述 网格相关API接口
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/8 17:25
 */
@RestController
@RequestMapping("/v1/openapi/")
public class GridAPI {

    /**
     * 注入OAuthService
     */
    @Autowired
    private OAuthService oAuthService;

    /**
     * 注入GridService
     */
    @Autowired
    private GridService gridService;


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
     * 根据 access_token 返回网格信息
     *
     * @param request
     * @return
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    @RequestMapping(value = "/grids", produces = "application/json; charset=UTF-8")
    public ResponseEntity getGrids(HttpServletRequest request) throws OAuthProblemException, OAuthSystemException {
        // 构建OAuth资源请求
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
        // 获取Access Token
        String accessToken = oauthRequest.getAccessToken();
        // 获取用户名
        String loginName = oAuthService.getLoginNameByAccessToken(accessToken);
        // 获取网格数据
        List<GridEntity> grids = gridService.listGridsByLoginName(loginName);

        // 遍历网格数据
        List<GridVO> gridVOs = grids.stream().map(grid -> new GridVO(grid)).collect(Collectors.toList());
        gridVOs = setGridParentName(gridVOs);
        JSONObject resultJson = new JSONObject();
        resultJson.put("grids", gridVOs);
        return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
    }

    /**
     * 返回所有网格的信息
     *
     * @return
     */
    @RequestMapping(value = "/grids/all", produces = "application/json; charset=UTF-8")
    public ResponseEntity findAll() {
        List<GridEntity> grids = gridService.findAll();
        List<GridVO> gridVOs = grids.stream().map(grid -> new GridVO(grid)).collect(Collectors.toList());
        gridVOs = setGridParentName(gridVOs);
        JSONObject resultJson = new JSONObject();
        resultJson.put("grids", gridVOs);
        return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
    }

}

