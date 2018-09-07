package com.yixin.pojo;

import java.util.Date;

public class AutodialerTask {

    private String uuid;
    private String name;
    private Date createDatetime;
    private Date alterDatetime;;

    private Integer start;
    private String destinationExtension;
    private String destinationDialplan;
    private String dialFormat;
    private String originationCallerIdNumber;
    private Integer callPauseSecond; //打完一个电话 延迟时间在打下一个号码
    private String remark; //用来存储外显号码
    private String destinationContext;
    private Integer disableDialTimeGroup; //禁止拨打时间的 的group编号

    public Integer getDisableDialTimeGroup() {
        return disableDialTimeGroup;
    }

    public void setDisableDialTimeGroup(Integer disableDialTimeGroup) {
        this.disableDialTimeGroup = disableDialTimeGroup;
    }

    public String getDestinationContext() {
        return destinationContext;
    }

    public void setDestinationContext(String destinationContext) {
        this.destinationContext = destinationContext;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCallPauseSecond() {
        return callPauseSecond;
    }

    public void setCallPauseSecond(Integer callPauseSecond) {
        this.callPauseSecond = callPauseSecond;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Date getAlterDatetime() {
        return alterDatetime;
    }

    public void setAlterDatetime(Date alterDatetime) {
        this.alterDatetime = alterDatetime;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getDestinationExtension() {
        return destinationExtension;
    }

    public void setDestinationExtension(String destinationExtension) {
        this.destinationExtension = destinationExtension;
    }

    public String getDestinationDialplan() {
        return destinationDialplan;
    }

    public void setDestinationDialplan(String destinationDialplan) {
        this.destinationDialplan = destinationDialplan;
    }

    public String getDialFormat() {
        return dialFormat;
    }

    public void setDialFormat(String dialFormat) {
        this.dialFormat = dialFormat;
    }

    public String getOriginationCallerIdNumber() {
        return originationCallerIdNumber;
    }

    public void setOriginationCallerIdNumber(String originationCallerIdNumber) {
        this.originationCallerIdNumber = originationCallerIdNumber;
    }

    @Override
    public String toString() {
        return "AutodialerTask{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", createDatetime=" + createDatetime +
                ", alterDatetime=" + alterDatetime +
                ", start=" + start +
                ", destinationExtension='" + destinationExtension + '\'' +
                ", destinationDialplan='" + destinationDialplan + '\'' +
                ", dialFormat='" + dialFormat + '\'' +
                ", originationCallerIdNumber='" + originationCallerIdNumber + '\'' +
                '}';
    }
}
