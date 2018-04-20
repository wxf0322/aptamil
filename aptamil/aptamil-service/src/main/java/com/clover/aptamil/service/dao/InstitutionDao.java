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

import com.clover.aptamil.service.dao.custom.InstitutionDaoCustom;
import com.clover.aptamil.service.entity.InstitutionEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/8 14:25
 */
@Repository
public interface InstitutionDao extends JpaRepository<InstitutionEntity, Long>, InstitutionDaoCustom {

    /**
     * 根据登陆名获取机构列表
     *
     * @param loginName
     * @return
     */
    @Query(value = "select * from usms_institutions i where i.id in " +
            " (select ui.institution_id from usms_user_institution ui " +
            " where ui.user_id in " +
            " (select u.id from usms_users u " +
            " where u.login_name = ?1))", nativeQuery = true)
    List<InstitutionEntity> listInstsByLoginName(String loginName);

    /**
     * 查询子组织机构
     *
     * @param instName
     * @return
     */
    @Query(value = "select * from usms_institutions i where i.path like NVL((" +
            "select i.path from usms_institutions i where i.name = ?1), '-1') || '%'",
            nativeQuery = true)
    List<InstitutionEntity> listSubInstsByInstName(String instName);

    /**
     * 根据用户id查询该用户所属的组织机构
     *
     * @param userId
     * @return
     */
    @Query(value = "select * from usms_institutions i where i.id in " +
            "(select ui.institution_id from usms_user_institution ui where ui.user_id = ?1) ",
            nativeQuery = true)
    List<InstitutionEntity> listInstsByUserId(Long userId);

    /**
     * 根据组织机构编码查询组织机构信息
     *
     * @param instName
     * @return
     */
    InstitutionEntity findFirstByName(String instName);

}
