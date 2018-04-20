/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.clover.aptamil.code.dao.BaseDao;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.code.vo.TreeData;
import com.clover.aptamil.service.entity.GridEntity;
import com.clover.aptamil.service.vo.UserVO;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午5:13
 */
public interface GridDaoCustom extends BaseDao<GridEntity> {

    /**
     * 分页查询用户Model具体信息
     *
     * @param page
     * @param size
     * @param gridCode
     * @param sqlFilter
     * @return
     */
    Page<UserVO> listUsersByPage(int page, int size, String gridCode, SqlFilter sqlFilter);

    /**
     * 查询所有的树形组织机构数据
     *
     * @return
     */
    List<TreeData> listTreeData();

    /**
     * 获得已经选中的用户
     *
     * @param gridCode
     * @param sqlFilter
     * @return
     */
    List<Map<String, Object>> listTargetUsers(Long gridCode, SqlFilter sqlFilter);

    /**
     * 获得未选中的用户
     *
     * @param gridCode
     * @param sqlFilter
     * @return
     */
    List<Map<String, Object>> listSourceUsers(Long gridCode, SqlFilter sqlFilter);

    /**
     * 更新网格用户关系
     *
     * @param gridCode
     * @param userIds
     */
    void updateUsers(String gridCode, String[] userIds);

    /**
     * 更新网格用户关系
     *
     * @param userId
     * @param gridCodes
     */
    void updateGrids(Long userId, String[] gridCodes);
}
