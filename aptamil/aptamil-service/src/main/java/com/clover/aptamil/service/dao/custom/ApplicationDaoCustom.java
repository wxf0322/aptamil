/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.custom;

import org.springframework.data.domain.Page;

import com.clover.aptamil.code.dao.BaseDao;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.service.entity.ApplicationEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午4:49
 */
public interface ApplicationDaoCustom extends BaseDao<ApplicationEntity> {

    /**
     * 查询权限列表
     *
     * @param page
     * @param size
     * @param sqlFilter
     * @return
     */
    Page<ApplicationEntity> listAppsByPage(int page, int size, SqlFilter sqlFilter);

}
