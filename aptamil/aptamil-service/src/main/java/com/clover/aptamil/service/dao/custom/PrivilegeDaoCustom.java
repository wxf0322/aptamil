/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.custom;

import org.springframework.data.domain.Page;

import com.clover.aptamil.code.dao.BaseDao;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.service.entity.PrivilegeEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午6:08
 */
public interface PrivilegeDaoCustom extends BaseDao<PrivilegeEntity> {

    /**
     * 查询权限列表
     *
     * @param page
     * @param size
     * @param sqlFilter
     * @return
     */
    Page<PrivilegeEntity> listPrivsByPage(int page, int size, SqlFilter sqlFilter);

    /**
     * 根据权限ID，更新对应操作的关系
     *
     * @param privilegeId
     * @param operationIds
     */
    void updateOperations(Long privilegeId, String[] operationIds);

    /**
     * 更新权限与用户之间的关系
     *
     * @param privilegeId
     * @param roleIds
     */
    void updateRoles(Long privilegeId, String[] roleIds);

}
