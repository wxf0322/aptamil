/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clover.aptamil.code.service.impl.BaseServiceImpl;
import com.clover.aptamil.service.StaffService;
import com.clover.aptamil.service.dao.StaffDao;
import com.clover.aptamil.service.entity.StaffEntity;

/**
 * 描述员工相关Service
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/6/29 下午1:05
 */
@Service
public class StaffServiceImpl extends BaseServiceImpl<StaffEntity, Long>
        implements StaffService{

    /**
     * 该类被上层Controller类调用，勿删
     * @see StaffDao
     */
    @Autowired
    private StaffDao staffDao;

}
