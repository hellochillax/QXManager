package com.taichangkeji.tckj.model;

/**
 * Created by MAC on 16/1/14.
 */
public class Member {

    private int HealthUserID;
    private String userName;
    private String Relations;
    private String Sex;
    private String Old;

    public Member(String userName, String relations, String sex, String old) {
        this.userName = userName;
        Relations = relations;
        Sex = sex;
        Old = old;
    }

    public int getHealthUserID() {
        return HealthUserID;
    }

    public void setHealthUserID(int healthUserID) {
        HealthUserID = healthUserID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRelations() {
        return Relations;
    }

    public void setRelations(String relations) {
        Relations = relations;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getOld() {
        return Old;
    }

    public void setOld(String old) {
        Old = old;
    }

    @Override
    public String toString() {
        return "HealthUserID="+HealthUserID+"&userName="+userName+"&Relations="+Relations+"&Sex="+Sex+"&Old="+Old;
    }
}
