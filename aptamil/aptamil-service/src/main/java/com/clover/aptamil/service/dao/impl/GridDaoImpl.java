/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.clover.aptamil.code.dao.impl.BaseDaoImpl;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.code.vo.TreeData;
import com.clover.aptamil.service.dao.GridDao;
import com.clover.aptamil.service.dao.UserDao;
import com.clover.aptamil.service.dao.custom.GridDaoCustom;
import com.clover.aptamil.service.entity.GridEntity;
import com.clover.aptamil.service.entity.UserEntity;
import com.clover.aptamil.service.vo.UserVO;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午5:14
 */
@Repository
public class GridDaoImpl extends BaseDaoImpl<GridEntity> implements GridDaoCustom  {

    /**
     * @see GridDao
     */
    @Autowired
    private GridDao gridDao;

    /**
     * @see UserDao
     */
    @Autowired
    private UserDao userDao;

    @Override
    public Page<UserVO> listUsersByPage(int page, int size, String gridCode, SqlFilter sqlFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("select u.*, s.mobile from ( ")
                .append(" select u.id, u.login_name, u.name, u.enabled, u.staff_id, ug.grid_code ")
                .append(" from usms_users u left join usms_user_grid ug on u.id = ug.user_id ")
                .append(sqlFilter.getWhereSql()).append(" ) u ")
                .append(" left join usms_staffs s on u.staff_id = s.id order by u.id desc");
        String sql = sb.toString();
        Page<Map<String, Object>> pageBean = queryForMap(sql, sqlFilter.getParams().toArray(), page, size);
        List<UserVO> results = new ArrayList<>();
        for (Map<String, Object> var : pageBean.getContent()) {
            UserVO userVO = new UserVO();
            userVO.setId(((BigDecimal) var.get("ID")).longValue());
            userVO.setLoginName((String) var.get("LOGIN_NAME"));
            userVO.setName((String) var.get("NAME"));
            userVO.setMobile((String) var.get("MOBILE"));
            userVO.setEnabled(((BigDecimal) var.get("ENABLED")).longValue());
            results.add(userVO);
        }
        return new PageImpl<>(results, new PageRequest(page, size), pageBean.getTotalElements());
    }

    /**
     * 查询所有网格树形节点
     *
     * @return
     */
    @Override
    public List<TreeData> listTreeData() {
        String sql = "select id, name, code, parent_id from gsmp_loc_grids";
        List<Map<String, Object>> variables = super.queryForMap(sql, null);
        List<TreeData> result = new ArrayList<>();
        for (Map<String, Object> variable : variables) {
            TreeData treeData = new TreeData();
            treeData.setId(((BigDecimal) variable.get("ID")).longValue());
            Object parentId = variable.get("PARENT_ID");
            if (parentId != null) {
                treeData.setParentId(((BigDecimal) parentId).longValue());
            } else {
                treeData.setParentId(0L);
            }
            treeData.setLabel((String) variable.get("NAME"));

            // 设置树节点数据
            Map<String, Object> data = new HashMap<>();
            String code = (String) variable.get("CODE");
            String descripiton = (String) variable.get("DESCRIPITON");
            String dutyPhone = (String) variable.get("DUTY_PHONE");

            List<UserEntity> users = userDao.listUsersByGridCode(code);
            List<String> userNames = new ArrayList<>();
            if (users != null) {
                for (UserEntity user : users) userNames.add(user.getName());
            }
            data.put("code", code);
            data.put("descripiton", descripiton);
            data.put("dutyPhone", dutyPhone);
            data.put("userNames", userNames);
            treeData.setData(data);
            result.add(treeData);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listTargetUsers(Long gridCode, SqlFilter sqlFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("select distinct u.id, u.name from usms_users u ")
                .append(" where u.enabled=1 and u.id in (select user_id from usms_user_grid ug ")
                .append(" where ug.grid_code =?)");
        String sql = sb.toString();
        return super.queryForMap(sql, new Object[]{gridCode});
    }

    @Override
    public List<Map<String, Object>> listSourceUsers(Long gridCode, SqlFilter sqlFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("select id, name, institution_id from ( ")
                .append("select distinct u.id, u.name, ui.institution_id from usms_users u ")
                .append("left join usms_user_institution ui on ui.user_id = u.id ")
                .append("where u.enabled=1 and u.id not in ")
                .append("(select user_id from usms_user_grid ug ")
                .append("where ug.grid_code =?)) uu " + sqlFilter.getWhereSql());
        String sql = sb.toString();
        List params = new ArrayList();
        params.add(gridCode);
        if (sqlFilter.getParams().size() != 0) {
            params.addAll(sqlFilter.getParams());
        }
        return super.queryForMap(sql, params.toArray());
    }

    @Override
    public void updateUsers(String gridCode, String[] userIds) {
        String sql = "delete from usms_user_grid where grid_code =:gridCode";
        Query query = manager.createNativeQuery(sql);
        query.setParameter("gridCode", gridCode);
        query.executeUpdate();
        if (userIds != null) {
            for (String id : userIds) {
                Long userId = Long.valueOf(id);
                sql = "insert into usms_user_grid values(:userId,:gridCode)";
                query = manager.createNativeQuery(sql);
                query.setParameter("gridCode", gridCode);
                query.setParameter("userId", userId);
                query.executeUpdate();
            }
        }
    }

    @Override
    public void updateGrids(Long userId, String[] gridCodes) {
        String sql = "delete from usms_user_grid where user_id =:userId";
        Query query = manager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        query.executeUpdate();
        if (gridCodes != null) {
            for (String code : gridCodes) {
                sql = "insert into usms_user_grid values(:userId,:gridCode)";
                query = manager.createNativeQuery(sql);
                query.setParameter("gridCode", code);
                query.setParameter("userId", userId);
                query.executeUpdate();
            }
        }
    }

}
