/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.vo;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clover.aptamil.service.entity.GridEntity;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/5/27 14:59
 */
public class GridVO {

    /**
     * 日志管理器
     */
    Logger logger = LoggerFactory.getLogger(InstitutionVO.class);
    /**
     * 网格ID
     */
    private long id;
    /**
     * 网格编码
     */
    private String code;
    /**
     * 网格层级
     */
    private Long lvl;
    /**
     * 网格名称
     */
    private String name;
    /**
     * 网格类型编码
     */
    private String gridType;
    /**
     * 描述
     */
    private String descripiton;
    /**
     * 值班电话
     */
    private String dutyPhone;
    /**
     * 网格图片
     */
    private String photoUrl;
    /**
     * 网格员数量
     */
    private Long memberNum;
    /**
     * 网格户数
     */
    private Long householdeNum;
    /**
     * 网格面积（平方米）
     */
    private Long area;
    /**
     * 上级网格ID
     */
    private Long parentId;
    /**
     * 隶属行政区划编码
     */
    private String adminDivisionCode;
    /**
     * 隶属行政区划
     */
    private String adminDivision;
    /**
     * 手动排序
     */
    private Long manualSn;
    /**
     * 轮廓ID
     */
    private Long geoOutlineId;
    /**
     * 可用状态
     */
    private Long enabled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getLvl() {
        return lvl;
    }

    public void setLvl(Long lvl) {
        this.lvl = lvl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGridType() {
        return gridType;
    }

    public void setGridType(String gridType) {
        this.gridType = gridType;
    }

    public String getDescripiton() {
        return descripiton;
    }

    public void setDescripiton(String descripiton) {
        this.descripiton = descripiton;
    }

    public String getDutyPhone() {
        return dutyPhone;
    }

    public void setDutyPhone(String dutyPhone) {
        this.dutyPhone = dutyPhone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Long memberNum) {
        this.memberNum = memberNum;
    }

    public Long getHouseholdeNum() {
        return householdeNum;
    }

    public void setHouseholdeNum(Long householdeNum) {
        this.householdeNum = householdeNum;
    }

    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAdminDivisionCode() {
        return adminDivisionCode;
    }

    public void setAdminDivisionCode(String adminDivisionCode) {
        this.adminDivisionCode = adminDivisionCode;
    }

    public String getAdminDivision() {
        return adminDivision;
    }

    public void setAdminDivision(String adminDivision) {
        this.adminDivision = adminDivision;
    }

    public Long getManualSn() {
        return manualSn;
    }

    public void setManualSn(Long manualSn) {
        this.manualSn = manualSn;
    }

    public Long getGeoOutlineId() {
        return geoOutlineId;
    }

    public void setGeoOutlineId(Long geoOutlineId) {
        this.geoOutlineId = geoOutlineId;
    }

    public Long getEnabled() {
        return enabled;
    }

    public void setEnabled(Long enabled) {
        this.enabled = enabled;
    }

    public GridVO(GridEntity gridEntity) {
        try {
            BeanUtils.copyProperties(this, gridEntity);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "GridVO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", lvl=" + lvl +
                ", name='" + name + '\'' +
                ", gridType='" + gridType + '\'' +
                ", descripiton='" + descripiton + '\'' +
                ", dutyPhone='" + dutyPhone + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", memberNum=" + memberNum +
                ", householdeNum=" + householdeNum +
                ", area=" + area +
                ", parentId=" + parentId +
                ", adminDivisionCode='" + adminDivisionCode + '\'' +
                ", adminDivision='" + adminDivision + '\'' +
                ", manualSn=" + manualSn +
                ", geoOutlineId=" + geoOutlineId +
                '}';
    }
}
