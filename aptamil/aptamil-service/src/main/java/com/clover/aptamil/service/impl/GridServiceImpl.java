/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clover.aptamil.code.service.impl.BaseServiceImpl;
import com.clover.aptamil.code.util.SqlFilter;
import com.clover.aptamil.code.vo.TreeData;
import com.clover.aptamil.service.GridService;
import com.clover.aptamil.service.dao.GridDao;
import com.clover.aptamil.service.entity.GridEntity;
import com.clover.aptamil.service.vo.UserVO;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/8 15:58
 */
@Transactional
@Service
public class GridServiceImpl extends BaseServiceImpl<GridEntity, Long>
        implements GridService {

    /**
     * @see GridDao
     */
    @Autowired
    private GridDao gridDao;

    /**
     * 根据登入名获取网格的数据
     *
     * @param loginName
     * @return
     */
    @Override
    public List<GridEntity> listGridsByLoginName(String loginName) {
        return gridDao.listGridsByLoginName(loginName);
    }

    /**
     * 返回网格数据
     *
     * @return
     */
    @Override
    public List<TreeData> listTreeData() {
        return gridDao.listTreeData();
    }

    @Override
    public Page<UserVO> listUsersByPage(int page, int size, String gridCode, SqlFilter sqlFilter) {
        return gridDao.listUsersByPage(page, size, gridCode, sqlFilter);
    }

    @Override
    public List<Map<String, Object>> listSourceUsers(Long gridCode, SqlFilter sqlFilter) {
        return gridDao.listSourceUsers(gridCode, sqlFilter);
    }

    @Override
    public List<Map<String, Object>> listTargetUsers(Long gridCode, SqlFilter sqlFilter) {
        return gridDao.listTargetUsers(gridCode, sqlFilter);
    }

    @Override
    public void updateUsers(String gridCode, String[] userIds ) {
        gridDao.updateUsers(gridCode, userIds );
    }

    @Override
    public void updateGrids(Long userId, String[] gridCodes) {
        gridDao.updateGrids(userId,gridCodes);
    }

}
