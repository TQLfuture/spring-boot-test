package com.yixin.pojo;

import java.util.Date;

public class NumberTask {

    private Integer id;
    private String number;
    //1 状态正在拨打  10 状态拨打完毕  5 第三方请求失败
    private Integer state;
    private Date callDate; //开始拨打时间
    private Date hangupdate; //挂断时间
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getHangupdate() {
        return hangupdate;
    }

    public void setHangupdate(Date hangupdate) {
        this.hangupdate = hangupdate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCallDate() {
        return callDate;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    }
}
