/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.custom;

import com.clover.aptamil.code.dao.BaseDao;
import com.clover.aptamil.service.entity.InstitutionEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/28 下午5:25
 */
public interface InstitutionDaoCustom extends BaseDao<InstitutionEntity> {

    /**
     * 检查是否能被删除
     *
     * @param id
     * @return
     */
    boolean canBeDeleted(Long id);
}
