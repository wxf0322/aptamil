/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.clover.aptamil.code.service.BaseService;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.code.vo.TreeData;
import com.clover.aptamil.service.entity.GridEntity;
import com.clover.aptamil.service.vo.UserVO;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/8 15:58
 */
public interface GridService extends BaseService<GridEntity, Long> {

    /**
     * 根据登入名获取网格数据
     *
     * @param loginName
     * @return
     */
    List<GridEntity> listGridsByLoginName(String loginName);

    /**
     * 返回所有的树形数据
     *
     * @return
     */
    List<TreeData> listTreeData();

    /**
     * 返回用户模型分页信息
     *
     * @param page
     * @param size
     * @param gridCode
     * @param sqlFilter
     * @return
     */
    Page<UserVO> listUsersByPage(int page, int size, String gridCode, SqlFilter sqlFilter);

    /**
     * 查询未选中的用户
     *
     * @param gridCode
     * @param sqlFilter
     * @return
     */
    List<Map<String, Object>> listSourceUsers(Long gridCode, SqlFilter sqlFilter);

    /**
     * 查询已经选中的用户
     *
     * @param gridCode
     * @param sqlFilter
     * @return
     */
    List<Map<String, Object>> listTargetUsers(Long gridCode, SqlFilter sqlFilter);

    /**
     * 更新用户
     *
     * @param gridCode
     * @param userIds
     */
    void updateUsers(String gridCode, String[] userIds);

    /**
     * 更新网格
     *
     * @param userId
     * @param gridCodes
     */
    void updateGrids(Long userId, String[] gridCodes);
}
