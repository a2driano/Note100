package com.a2driano.note100.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.a2driano.note100.model.NoteModel;

import java.util.Date;
import java.util.UUID;

import static com.a2driano.note100.data.NoteDbSchema.*;

/**
 * Created by Andrii Papai on 21.12.2016.
 */

public class NoteCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public NoteModel getNote() {
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String text = getString(getColumnIndex(NoteTable.Cols.TEXT));
        String date = getString(getColumnIndex(NoteTable.Cols.DATE));
        String color = getString(getColumnIndex(NoteTable.Cols.COLOR));

        NoteModel noteModel = new NoteModel(UUID.fromString(uuidString));
        noteModel.setText(text);
        noteModel.setDate(new Date(date));
        noteModel.setColor(color);

        return noteModel;
    }
}
