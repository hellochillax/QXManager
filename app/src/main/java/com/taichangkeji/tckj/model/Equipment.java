package com.taichangkeji.tckj.model;

/**
 * Created by MAC on 16/3/7.
 * 倍泰设备的实体类
 */
public class Equipment {

    private String SRNo;
    private String Comment;

    public String getSRNo() {
        return SRNo;
    }

    public void setSRNo(String SRNo) {
        this.SRNo = SRNo;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "SRNo='" + SRNo + '\'' +
                ", Comment='" + Comment + '\'' +
                '}';
    }
}
