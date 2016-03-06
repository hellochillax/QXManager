package com.taichangkeji.tckj.model;

/**
 * 用户基本信息
 * @author fenghui
 *
 */
public class BTUserInfo {
    private String name = "";
    private String age = "0";
    private String height = "0";
    private String weight = "0";
    private String sex = "0";
    private String profession = "1";
    private String step = "0";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getAge() {
        int age_int = Integer.parseInt(this.age);

        return (byte) age_int;

    }

    public void setAge(String age) {
        this.age = age;
    }

    public byte getHeight() {
        int height_int = Integer.parseInt(this.height);

        return (byte) height_int;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getWeight() {
        int weight_int = (int) (Double.parseDouble(this.weight) * 10);

        return weight_int;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBrSex() {
        return this.sex;
    }

    public byte getSex() {
        // return this.sex.equals("0") ? (byte) 0x01 : (byte) 0x81;

        if (this.profession.equalsIgnoreCase("1")) {
            return this.sex.equals("0") ? (byte) 0x01 : (byte) 0x81;
        } else if (this.profession.equalsIgnoreCase("2")) {
            return this.sex.equals("0") ? (byte) 0x02 : (byte) 0x82;
        } else if (this.profession.equalsIgnoreCase("3")) {
            return this.sex.equals("0") ? (byte) 0x03 : (byte) 0x83;
        }
        return 0x1;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public byte getProfession() {
        int profession_int = Integer.parseInt(this.profession);
        return (byte) profession_int;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public byte getStep() {
        int step_int = Integer.parseInt(this.step);
        return (byte) step_int;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
