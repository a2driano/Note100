package com.a2driano.note100.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Andrii Papai on 20.12.2016.
 */

public class NoteModel {
    private UUID mId;
    private String mText;
    private Date mDate;
    private String mColor;

    public NoteModel() {
        mId = UUID.randomUUID();
    }

    public NoteModel(UUID id) {
        mId = id;
//        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }
}
