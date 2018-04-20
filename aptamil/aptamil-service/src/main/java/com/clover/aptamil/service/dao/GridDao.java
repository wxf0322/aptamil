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

import com.clover.aptamil.service.dao.custom.GridDaoCustom;
import com.clover.aptamil.service.entity.GridEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/8 15:58
 */
@Repository
public interface GridDao extends JpaRepository<GridEntity, Long>, GridDaoCustom {


    @Query(value = " select * from gsmp_loc_grids g " +
            "where g.code in " +
            "(select ug.grid_code from usms_user_grid ug " +
            "where ug.user_id = " +
            "(select u.id from usms_users u where u.login_name = ?1)) ", nativeQuery = true)
    List<GridEntity> listGridsByLoginName(String loginName);

}
