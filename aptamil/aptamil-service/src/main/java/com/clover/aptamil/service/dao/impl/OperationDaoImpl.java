/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.clover.aptamil.code.dao.impl.BaseDaoImpl;
import com.clover.aptamil.code.util.MapUtil;
import com.clover.aptamil.service.dao.custom.OperationDaoCustom;
import com.clover.aptamil.service.entity.OperationEntity;
import com.clover.aptamil.service.vo.OperationVO;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/7/20 上午9:29
 */
@Repository
public class OperationDaoImpl extends BaseDaoImpl<OperationEntity> implements OperationDaoCustom {

    /**
     * @see Logger
     */
    private static Logger logger = LoggerFactory.getLogger(OperationDaoImpl.class);

    @Override
    public boolean canBeDeleted(Long id) {
        String sql = "select o.id from usms_operations o where o.parent_id = ?";
        List<Object> children = super.queryForObject(sql, new Object[]{id});
        if (children != null && children.size() > 0)
            return false;
        else
            return true;
    }


    @Override
    public List<OperationVO> listOpersByPrivId(Long privilegeId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from usms_operations where id in( ")
                .append("select oper_id from usms_privilege_operation t ")
                .append("where priv_id=?) and enabled=1");
        String sql = sb.toString();
        List<Map<String, Object>> rows = super.queryForMap(sql, new Object[]{privilegeId});
        List<OperationVO> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            try {
                Map<String, Object> camelMap = MapUtil.toCamelCaseMap(row);
                OperationVO operation = MapUtil.toObject(OperationVO.class ,camelMap);
                result.add(operation);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

}
