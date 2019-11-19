package com.org.biquge.jsoup.novel.entities;

public class DownLoadEntity {
    int loadedPage;
    int allPage;
    int currentPage;
    String homeUrl;
    String currentPageUrl;
    int loadingStatu = 0;//0 暂停,1正在下载,2下载完成

    public DownLoadEntity(int loadedPage, int allPage, int currentPage, String homeUrl, String currentPageUrl, int loadingStatu) {
        this.loadedPage = loadedPage;
        this.allPage = allPage;
        this.currentPage = currentPage;
        this.homeUrl = homeUrl;
        this.currentPageUrl = currentPageUrl;
        this.loadingStatu = loadingStatu;
    }

    public DownLoadEntity() {
    }

    public int getLoadedPage() {
        return loadedPage;
    }

    public void setLoadedPage(int loadedPage) {
        this.loadedPage = loadedPage;
    }

    public int getAllPage() {
        return allPage;
    }

    public void setAllPage(int allPage) {
        this.allPage = allPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getHomeUrl() {
        return homeUrl;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public String getCurrentPageUrl() {
        return currentPageUrl;
    }

    public void setCurrentPageUrl(String currentPageUrl) {
        this.currentPageUrl = currentPageUrl;
    }

    public int getLoadingStatu() {
        return loadingStatu;
    }

    public void setLoadingStatu(int loadingStatu) {
        this.loadingStatu = loadingStatu;
    }
}
