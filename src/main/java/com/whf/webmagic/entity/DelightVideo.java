package com.whf.webmagic.entity;

import java.util.Date;

public class DelightVideo {
    private String localUrl;
    private String videoName;
    private Integer palyTime;
    private String flag;
    private Date createTime;

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public Integer getPalyTime() {
        return palyTime;
    }

    public void setPalyTime(Integer palyTime) {
        this.palyTime = palyTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
