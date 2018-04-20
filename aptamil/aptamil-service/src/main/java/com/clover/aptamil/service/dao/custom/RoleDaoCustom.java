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
import com.clover.aptamil.service.entity.RoleEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午6:31
 */
public interface RoleDaoCustom extends BaseDao<RoleEntity> {

    /**
     * 所有角色列表
     *
     * @param page
     * @param size
     * @return
     */
    Page<RoleEntity> listRolesByPage(int page, int size, SqlFilter sqlFilter);


    /**
     * 根据角色ID查找已选用户列表
     *
     * @param roleId
     * @return
     */
    List<Map<String, Object>> listTargetUsers(Long roleId, SqlFilter sqlFilter);

    /**
     * 根据角色ID查找未选用户列表
     *
     * @param roleId
     * @param institutionId
     * @param key
     * @return
     */
    List<Map<String, Object>> listSourceUsers(Long roleId, Long institutionId, String key);

    /**
     * 更新角色对应的权限列表
     *
     * @param roleId
     * @param privilegeIds
     */
    void updatePrivileges(Long roleId, String[] privilegeIds);

    /**
     * 更新用户列表
     *
     * @param roleId
     * @param userIds
     */
    void updateUsers(Long roleId, String[] userIds);

}
