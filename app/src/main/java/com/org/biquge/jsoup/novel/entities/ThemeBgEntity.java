package com.org.biquge.jsoup.novel.entities;

public class ThemeBgEntity {
    int id;
    boolean isCheck;
    String name;

    public ThemeBgEntity(int id, boolean isCheck, String name) {
        this.id = id;
        this.isCheck = isCheck;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
