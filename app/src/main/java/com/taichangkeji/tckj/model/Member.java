package com.taichangkeji.tckj.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MAC on 16/1/14.
 */
public class Member implements Parcelable{

    private int HealthUserID;
    private String UserName;
    private String Relations;
    private String Sex;
    private int Old;
    private String FileFolder;
    private boolean selected;

    public Member(String userName, String relations, String sex, int old) {
        UserName = userName;
        Relations = relations;
        Sex = sex;
        Old = old;
    }

    public Member() {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getFileFolder() {
        return FileFolder;
    }

    public void setFileFolder(String fileFolder) {
        FileFolder = fileFolder;
    }

    public int getHealthUserID() {
        return HealthUserID;
    }

    public void setHealthUserID(int healthUserID) {
        HealthUserID = healthUserID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
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

    public int getOld() {
        return Old;
    }

    public void setOld(int old) {
        Old = old;
    }

    @Override
    public String toString() {
        return "UserName="+UserName+"&Relations="+Relations+"&Sex="+Sex+"&Old="+Old;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(HealthUserID);
        dest.writeString(UserName);
        dest.writeInt(Old);
        dest.writeString(Sex);
        dest.writeString(Relations);
        dest.writeString(FileFolder);
    }

    public static final Creator<Member> CREATOR=new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel source) {
            Member member=new Member();
            member.setHealthUserID(source.readInt());
            member.setUserName(source.readString());
            member.setOld(source.readInt());
            member.setSex(source.readString());
            member.setRelations(source.readString());
            member.setFileFolder(source.readString());
            return member;
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[0];
        }
    };
}
