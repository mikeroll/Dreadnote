package com.mikeroll.dreadnote.storage;

public class Note {

    private String title;
    private String content;
    private int color;

    public Note(String title) {
        this(title, 0xFFFFFFFF, "");
    }

    public Note(String title, int color) {
        this(title, color, "");
    }

    public Note(String title, int color, String content) {
        this.title = title;
        this.color = color;
        this.content = content;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
