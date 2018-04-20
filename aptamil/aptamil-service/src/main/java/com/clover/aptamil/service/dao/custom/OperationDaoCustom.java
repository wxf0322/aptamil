/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.custom;

import java.util.List;

import com.clover.aptamil.code.dao.BaseDao;
import com.clover.aptamil.service.entity.OperationEntity;
import com.clover.aptamil.service.vo.OperationVO;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/7/20 上午9:25
 */
public interface OperationDaoCustom extends BaseDao<OperationEntity> {

    /**
     * 检查是否能被删除
     *
     * @param id
     * @return
     */
    boolean canBeDeleted(Long id);


    /**
     * 查找权限ID对应的操作列表
     *
     * @param privilegeId
     * @return
     */
    List<OperationVO> listOpersByPrivId(Long privilegeId);

}
