package com.org.biquge.jsoup.novel.events;

public class RefreshLoadingEvent {
    public String type;
    public int position;
    public String loadEntity;

    public RefreshLoadingEvent(String type, int position, String loadEntity) {
        this.type = type;
        this.position = position;
        this.loadEntity = loadEntity;
    }
}
