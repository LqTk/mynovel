package com.org.biquge.jsoup.novel.entities;

public class BooksPageEntity {
    String name;
    String pageUrl;
    boolean ischeck;

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public BooksPageEntity(String name, String pageUrl, boolean ischeck) {
        this.name = name;
        this.pageUrl = pageUrl;
        this.ischeck = ischeck;
    }
}
