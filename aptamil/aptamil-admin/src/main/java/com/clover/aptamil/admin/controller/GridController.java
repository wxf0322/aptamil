/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.admin.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.code.vo.ResultStatus;
import com.clover.aptamil.code.vo.TreeData;
import com.clover.aptamil.service.GridService;
import com.clover.aptamil.service.vo.UserVO;

/**
 * 描述 管辖区域管理Controller层
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/17 11:03
 */
@Controller
@RequestMapping("/grid")
public class GridController {

    /**
     * 注入GridService
     */
    @Autowired
    private GridService gridService;

    /**
     * 返回树形节点数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "tree")
    public List<TreeData> listTreeData() {
        return gridService.listTreeData();
    }

    /**
     * 返回分页数据
     *
     * @param page
     * @param size
     * @param gridCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public Page<UserVO> listUsersByPage(int page, int size, String gridCode) {
        SqlFilter sqlFilter = new SqlFilter();
        if (!StringUtils.isEmpty(gridCode)) {
            sqlFilter.addFilter("QUERY_ug#grid_code_S_EQ", gridCode);
        } else {
            sqlFilter.addFilter("QUERY_ug#grid_code_S_EQNULL", "");
        }
        return gridService.listUsersByPage(page, size, gridCode, sqlFilter);
    }

    /**
     * 获取已选择用户
     */
    @ResponseBody
    @RequestMapping(value = "users/source")
    public List<Map<String, Object>> listSourceUsers(Long institutionId, Long gridCode, HttpServletRequest request) {
        SqlFilter sqlFilter = new SqlFilter();
        if (institutionId != null) {
            sqlFilter.addFilter("QUERY_uu#institution_id_L_EQ",
                    request.getParameter("institutionId"));
        } else {
            sqlFilter.addFilter("QUERY_uu#institution_id_L_EQNULL", "1");
        }
        if (!StringUtils.isEmpty(request.getParameter("key"))) {
            sqlFilter.addFilter("QUERY_uu#name_S_LK", request.getParameter("key"));
        }
        return gridService.listSourceUsers(gridCode, sqlFilter);
    }

    /**
     * 获取可选择用户
     */
    @ResponseBody
    @RequestMapping(value = "users/target")
    public List<Map<String, Object>> listTargetUsers(Long gridCode) {
        SqlFilter sqlFilter = new SqlFilter();
        return gridService.listTargetUsers(gridCode, sqlFilter);
    }

    @ResponseBody
    @RequestMapping(value = "updateUsers")
    public ResultStatus updateUsers(String gridCode, String userIds) {
        String[] ids;
        if (StringUtils.isEmpty(userIds)) {
            ids = null;
        } else {
            ids = userIds.split(",");
        }
        gridService.updateUsers(gridCode, ids);
        return new ResultStatus(true, "");
    }

    @ResponseBody
    @RequestMapping(value = "updateGrids")
    public ResultStatus updateGrids(Long userId, String gridCodes) {
        String[] codes;
        if (StringUtils.isEmpty(gridCodes)) {
            codes = null;
        } else {
            codes = gridCodes.split(",");
        }
        gridService.updateGrids(userId, codes);
        return new ResultStatus(true, "");
    }

}
