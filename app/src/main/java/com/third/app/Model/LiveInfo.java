package com.third.app.Model;

import java.io.Serializable;

/**
 * Author: Jan
 * CreateTime:on 2016/10/27.
 */

public class LiveInfo implements Serializable {
    public String id;

    public String isLook;

    public String live_Url;

    public String logo_url;

    public String remarks;

    public String video_name;

    public String wonderful;

    public String tv_name;

    public String description;

    public String create_Time;

    public String clientPic_Url;

    public String client_FirstPic_Url;

    public int orderBy;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsLook() {
        return isLook;
    }

    public void setIsLook(String isLook) {
        this.isLook = isLook;
    }

    public String getLive_Url() {
        return live_Url;
    }

    public void setLive_Url(String live_Url) {
        this.live_Url = live_Url;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getWonderful() {
        return wonderful;
    }

    public void setWonderful(String wonderful) {
        this.wonderful = wonderful;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreate_Time() {
        return create_Time;
    }

    public void setCreate_Time(String create_Time) {
        this.create_Time = create_Time;
    }

    public String getClientPic_Url() {
        return clientPic_Url;
    }

    public void setClientPic_Url(String clientPic_Url) {
        this.clientPic_Url = clientPic_Url;
    }

    public String getClient_FirstPic_Url() {
        return client_FirstPic_Url;
    }

    public void setClient_FirstPic_Url(String client_FirstPic_Url) {
        this.client_FirstPic_Url = client_FirstPic_Url;
    }

    public int getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(int orderBy) {
        this.orderBy = orderBy;
    }
}
