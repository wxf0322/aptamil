/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.impl;

import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.clover.aptamil.code.dao.impl.BaseDaoImpl;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.service.dao.custom.PrivilegeDaoCustom;
import com.clover.aptamil.service.entity.PrivilegeEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午6:10
 */
@Repository
public class PrivilegeDaoImpl extends BaseDaoImpl<PrivilegeEntity> implements PrivilegeDaoCustom {

    /**
     * 查找权限列表
     *
     * @param page
     * @param size
     * @param sqlFilter
     * @return
     */
    @Override
    public Page<PrivilegeEntity> listPrivsByPage(int page, int size, SqlFilter sqlFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from usms_privileges p ")
                .append(sqlFilter.getWhereSql())
                .append(" order by p.id desc");
        String sql = sb.toString();
        return super.queryForClass(sql, sqlFilter.getParams().toArray(), page, size);
    }

    @Override
    public void updateOperations(Long privilegeId, String[] operationIds) {
        String sql ="delete from usms_privilege_operation where priv_id =:privilegeId";
        Query query = manager.createNativeQuery(sql);
        query.setParameter("privilegeId", privilegeId);
        query.executeUpdate();
        if (operationIds != null) {
            for (String id : operationIds) {
                Long operationId = Long.valueOf(id);
                sql = "insert into usms_privilege_operation values(:privilegeId,:operationId)";
                query = manager.createNativeQuery(sql);
                query.setParameter("privilegeId", privilegeId);
                query.setParameter("operationId", operationId);
                query.executeUpdate();
            }
        }
    }

    @Override
    public void updateRoles(Long privilegeId, String[] roleIds) {
        String sql = "delete from usms_privilege_role where priv_id =:privilegeId";
        Query query = manager.createNativeQuery(sql);
        query.setParameter("privilegeId", privilegeId);
        query.executeUpdate();
        if (roleIds != null) {
            for (String id : roleIds) {
                Long roleId = Long.valueOf(id);
                sql = "insert into usms_privilege_role values(:privilegeId,:roleId)";
                query = manager.createNativeQuery(sql);
                query.setParameter("roleId", roleId);
                query.setParameter("privilegeId", privilegeId);
                query.executeUpdate();
            }
        }
    }

}
