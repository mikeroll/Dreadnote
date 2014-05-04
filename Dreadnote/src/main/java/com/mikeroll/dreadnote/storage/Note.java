package com.mikeroll.dreadnote.storage;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {

    private String title;
    private String content;
    private int color;

    public Note(String title, int color) {
        this(title, 0xFFFFFFFF, "");
    }

    public Note(String title, int color, String content) {
        this.title = title;
        this.color = color;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(content);
        parcel.writeInt(color);
        parcel.writeString(title);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel parcel) {
            return new Note(parcel);
        }

        @Override
        public Note[] newArray(int i) {
            return new Note[0];
        }
    };

    private Note(Parcel parcel) {
        title = parcel.readString();
        color = parcel.readInt();
        content = parcel.readString();
    }
}