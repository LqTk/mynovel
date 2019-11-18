package com.org.biquge.jsoup.novel.entities;

public class ThemeBgEntity {
    boolean isChecked;
    int bgId;

    public ThemeBgEntity(boolean isChecked, int bgId) {
        this.isChecked = isChecked;
        this.bgId = bgId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getBgId() {
        return bgId;
    }

    public void setBgId(int bgId) {
        this.bgId = bgId;
    }
}
