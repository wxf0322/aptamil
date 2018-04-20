/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clover.aptamil.service.dao.custom.RoleDaoCustom;
import com.clover.aptamil.service.entity.RoleEntity;

/**
 * @author Pisces Lu
 * @version 1.0
 * @created 2017-5-8 18:24
 */
@Repository
public interface RoleDao extends JpaRepository<RoleEntity, Long>, RoleDaoCustom {

    /**
     * @param enabled
     * @return
     */
    List<RoleEntity> findByEnabled(Long enabled);

    /**
     * 根据用户名查找角色列表
     *
     * @param userId
     * @return
     */
    @Query(value = "select * from usms_roles r where r.id in( " +
            "select ur.role_id from usms_user_role ur " +
            "where ur.user_id = ?1) and r.enabled = 1", nativeQuery = true)
    List<RoleEntity> listRolesByUserId(long userId);

    @Query(value = "select * from usms_roles r where r.id in " +
            "(select ur.role_id from usms_user_role ur where ur.user_id = ?1) " +
            "and r.enabled = 1 ", nativeQuery = true)
    List<RoleEntity> listRolesByUserId(Long userId);

    @Query(value = "select * from usms_roles where id in " +
            "(select role_id from usms_user_role where user_id = ?1) and enabled=1 ", nativeQuery = true)
    List<RoleEntity> listTargetRolesByUserId(Long userId);

    @Query(value = "select * from usms_roles where id not in " +
            "(select role_id from usms_user_role where user_id = ?1) and enabled=1 ", nativeQuery = true)
    List<RoleEntity> listSourceRolesByUserId(Long userId);

    /**
     * 获取该权限已选择的角色
     */
    @Query(value = "select * from usms_roles r where r.id in " +
            "(select role_id from usms_privilege_role " +
            "where priv_id =?1) and enabled=1 ", nativeQuery = true)
    List<RoleEntity> listTargetRolesByPrivId(Long privilegeId);

    /**
     * 获取该权限未选择的角色
     */
    @Query(value = "select * from usms_roles r where r.id not in " +
            "(select role_id from usms_privilege_role " +
            "where priv_id =?1) and enabled=1", nativeQuery = true)
    List<RoleEntity> listSourceRolesByPrivId(Long privilegeId);

    /**
     * 判断是否拥有此角色
     *
     * @param userId
     * @return
     */
    @Query(value = "select * from usms_roles r where r.id in( " +
            "select ur.role_id from usms_user_role ur " +
            "where ur.user_id = ?1) and r.enabled=1 and r.name= ?2", nativeQuery = true)
    List<RoleEntity> listUserRoles(long userId, String roleName);


}
