/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.impl;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.clover.aptamil.code.dao.impl.BaseDaoImpl;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.service.dao.custom.ApplicationDaoCustom;
import com.clover.aptamil.service.entity.ApplicationEntity;

/**
 * 描述 拓展类实现
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午5:02
 */
@Repository
public class ApplicationDaoImpl extends BaseDaoImpl<ApplicationEntity> implements ApplicationDaoCustom {
    @Override
    public Page<ApplicationEntity> listAppsByPage(int page, int size, SqlFilter sqlFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from usms_applications a ")
                .append(sqlFilter.getWhereSql())
                .append(" order by id desc");
        String sql = sb.toString();
        return super.queryForClass(sql, sqlFilter.getParams().toArray(), page, size);
    }
}
