/*
 * Copyright (c) 2005, 2017, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.clover.aptamil.service.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 描述
 *
 * @author wangxiaofeng
 * @version 1.0
 * @created 2017/4/26 11:31
 */
@Entity
@Table(name = "GSMP_LOC_GRIDS")
public class GridEntity {
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
     * 创建时间
     */
    private Date timeCreated;

    /**
     * 创建人id
     */
    private Long creatorId;

    /**
     * 创建ORG的ID
     */
    private Long createOrgId;

    /**
     * 变更人id
     */
    private Long modifierId;

    /**
     * 修改时间
     */
    private Date timeModified;

    /**
     * 可用状态
     */
    private Long enabled;

    @Id
    @Column(name = "ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "LVL")
    public Long getLvl() {
        return lvl;
    }

    public void setLvl(Long lvl) {
        this.lvl = lvl;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "GRID_TYPE")
    public String getGridType() {
        return gridType;
    }

    public void setGridType(String gridType) {
        this.gridType = gridType;
    }

    @Basic
    @Column(name = "DESCRIPITON")
    public String getDescripiton() {
        return descripiton;
    }

    public void setDescripiton(String descripiton) {
        this.descripiton = descripiton;
    }

    @Basic
    @Column(name = "DUTY_PHONE")
    public String getDutyPhone() {
        return dutyPhone;
    }

    public void setDutyPhone(String dutyPhone) {
        this.dutyPhone = dutyPhone;
    }

    @Basic
    @Column(name = "PHOTO_URL")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Basic
    @Column(name = "MEMBER_NUM")
    public Long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Long memberNum) {
        this.memberNum = memberNum;
    }

    @Basic
    @Column(name = "HOUSEHOLDE_NUM")
    public Long getHouseholdeNum() {
        return householdeNum;
    }

    public void setHouseholdeNum(Long householdeNum) {
        this.householdeNum = householdeNum;
    }

    @Basic
    @Column(name = "AREA")
    public Long getArea() {
        return area;
    }

    public void setArea(Long area) {
        this.area = area;
    }

    @Basic
    @Column(name = "PARENT_ID")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "ADMIN_DIVISION_CODE")
    public String getAdminDivisionCode() {
        return adminDivisionCode;
    }

    public void setAdminDivisionCode(String adminDivisionCode) {
        this.adminDivisionCode = adminDivisionCode;
    }

    @Basic
    @Column(name = "ADMIN_DIVISION")
    public String getAdminDivision() {
        return adminDivision;
    }

    public void setAdminDivision(String adminDivision) {
        this.adminDivision = adminDivision;
    }

    @Basic
    @Column(name = "MANUAL_SN")
    public Long getManualSn() {
        return manualSn;
    }

    public void setManualSn(Long manualSn) {
        this.manualSn = manualSn;
    }

    @Basic
    @Column(name = "GEO_OUTLINE_ID")
    public Long getGeoOutlineId() {
        return geoOutlineId;
    }

    public void setGeoOutlineId(Long geoOutlineId) {
        this.geoOutlineId = geoOutlineId;
    }

    @Basic
    @Column(name = "TIME_CREATED")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Basic
    @Column(name = "creator_id")
    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    @Basic
    @Column(name = "create_org_id")
    public Long getCreateOrgId() {
        return createOrgId;
    }

    public void setCreateOrgId(Long createOrgId) {
        this.createOrgId = createOrgId;
    }

    @Basic
    @Column(name = "modifier_id")
    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    @Basic
    @Column(name = "time_modified")
    public Date getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Date timeModified) {
        this.timeModified = timeModified;
    }

    @Basic
    @Column(name = "ENABLED")
    public Long getEnabled() {
        return enabled;
    }

    public void setEnabled(Long enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GridEntity that = (GridEntity) o;

        if (id != that.id) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (lvl != null ? !lvl.equals(that.lvl) : that.lvl != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (gridType != null ? !gridType.equals(that.gridType) : that.gridType != null) return false;
        if (descripiton != null ? !descripiton.equals(that.descripiton) : that.descripiton != null) return false;
        if (dutyPhone != null ? !dutyPhone.equals(that.dutyPhone) : that.dutyPhone != null) return false;
        if (photoUrl != null ? !photoUrl.equals(that.photoUrl) : that.photoUrl != null) return false;
        if (memberNum != null ? !memberNum.equals(that.memberNum) : that.memberNum != null) return false;
        if (householdeNum != null ? !householdeNum.equals(that.householdeNum) : that.householdeNum != null)
            return false;
        if (area != null ? !area.equals(that.area) : that.area != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (adminDivisionCode != null ?
                !adminDivisionCode.equals(that.adminDivisionCode) : that.adminDivisionCode != null)
            return false;
        if (adminDivision != null ? !adminDivision.equals(that.adminDivision) : that.adminDivision != null)
            return false;
        if (manualSn != null ? !manualSn.equals(that.manualSn) : that.manualSn != null) return false;
        if (geoOutlineId != null ? !geoOutlineId.equals(that.geoOutlineId) : that.geoOutlineId != null) return false;
        if (timeCreated != null ? !timeCreated.equals(that.timeCreated) : that.timeCreated != null) return false;
        if (creatorId != null ? !creatorId.equals(that.creatorId) : that.creatorId != null) return false;
        if (createOrgId != null ? !createOrgId.equals(that.createOrgId) : that.createOrgId != null) return false;
        if (modifierId != null ? !modifierId.equals(that.modifierId) : that.modifierId != null) return false;
        if (timeModified != null ? !timeModified.equals(that.timeModified) : that.timeModified != null) return false;
        return enabled != null ? enabled.equals(that.enabled) : that.enabled == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (lvl != null ? lvl.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (gridType != null ? gridType.hashCode() : 0);
        result = 31 * result + (descripiton != null ? descripiton.hashCode() : 0);
        result = 31 * result + (dutyPhone != null ? dutyPhone.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        result = 31 * result + (memberNum != null ? memberNum.hashCode() : 0);
        result = 31 * result + (householdeNum != null ? householdeNum.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (adminDivisionCode != null ? adminDivisionCode.hashCode() : 0);
        result = 31 * result + (adminDivision != null ? adminDivision.hashCode() : 0);
        result = 31 * result + (manualSn != null ? manualSn.hashCode() : 0);
        result = 31 * result + (geoOutlineId != null ? geoOutlineId.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (createOrgId != null ? createOrgId.hashCode() : 0);
        result = 31 * result + (modifierId != null ? modifierId.hashCode() : 0);
        result = 31 * result + (timeModified != null ? timeModified.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "GridEntity{" +
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
                ", timeCreated=" + timeCreated +
                ", creatorId=" + creatorId +
                ", createOrgId=" + createOrgId +
                ", modifierId=" + modifierId +
                ", timeModified=" + timeModified +
                ", enabled=" + enabled +
                '}';
    }
}
