package com.taichangkeji.tckj.model;

/**
 * Created by MAC on 16/3/11.
 */
public class Relation {

    private String RelationsID;
    private String RelationsName;

    public String getRelationsID() {
        return RelationsID;
    }

    public void setRelationsID(String relationsID) {
        RelationsID = relationsID;
    }

    public String getRelationsName() {
        return RelationsName;
    }

    public void setRelationsName(String relationsName) {
        RelationsName = relationsName;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "RelationsID='" + RelationsID + '\'' +
                ", RelationsName='" + RelationsName + '\'' +
                '}';
    }
}
