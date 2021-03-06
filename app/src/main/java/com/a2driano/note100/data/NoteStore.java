package com.a2driano.note100.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.a2driano.note100.model.NoteModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.a2driano.note100.data.NoteDbSchema.NoteTable;

/**
 * Created by Andrii Papai on 20.12.2016.
 */

public class NoteStore {
    private static NoteStore sNoteStore;
    private List<NoteModel> mNoteModelList;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private NoteStore(Context context) {
        mContext = context;
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();

        mNoteModelList = new ArrayList<>();
    }

    public static NoteStore get(Context context) {
        if (sNoteStore == null)
            sNoteStore = new NoteStore(context);
        return sNoteStore;
    }

    public List<NoteModel> getNotes() {
        mNoteModelList = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mNoteModelList.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mNoteModelList;
    }

    /**
     * Search func
     */
    public List<NoteModel> getNotes(String searchText) {
        mNoteModelList = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(searchText);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mNoteModelList.add(cursor.getNote());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return mNoteModelList;
    }

    public void addNote(NoteModel noteModel) {
        ContentValues values = getContentValues(noteModel);
        mDatabase.insert(NoteTable.NAME, null, values);
    }

    public NoteModel getNote(UUID id) {
        NoteCursorWrapper cursor = queryNotes(
                NoteTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    public void updateNote(NoteModel noteModel) {
        String uuidString = noteModel.getId().toString();
        ContentValues values = getContentValues(noteModel);
        mDatabase.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ? ",
                new String[]{uuidString});
    }

    public void deleteNote(NoteModel noteModel) {
        String uuidString = noteModel.getId().toString();
        ContentValues values = getContentValues(noteModel);
        mDatabase.delete(NoteTable.NAME,
                NoteTable.Cols.UUID + " = ? ",
                new String[]{uuidString});
    }

    public void deleteAllSelectedNotes(HashMap<Integer, UUID> hashMapForDelete) {
//        String[] uuidString = new String[hashMapForDelete.size()];
//        int i = 0;
        for (Map.Entry<Integer, UUID> entry : hashMapForDelete.entrySet()) {
            UUID uuid = entry.getValue();
//            uuidString[i] = uuid.toString();
            mDatabase.delete(NoteTable.NAME,
                    NoteTable.Cols.UUID + " = ? ",
                    new String[]{uuid.toString()});
        }
//        mDatabase.execSQL("DELETE FROM " + NoteTable.NAME
//                + " WHERE " + NoteTable.Cols.UUID + " (IN SELECT * FROM "
//                + NoteTable.NAME + " WHERE " + NoteTable.Cols.UUID + " = ?) ", uuidString);

//        mDatabase.execSQL("DELETE FROM " + NoteTable.NAME + " WHERE " + NoteTable.Cols.UUID
//                + " IN (SELECT " + NoteTable.Cols.UUID + " FROM  " + uuidString + " )");
//        mDatabase.execSQL("DELETE FROM " + NoteTable.NAME + " where " + NoteTable.Cols.UUID + " = " + uuidString + ";");
    }

    private static ContentValues getContentValues(NoteModel noteModel) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, noteModel.getId().toString());
        values.put(NoteTable.Cols.TEXT, noteModel.getText());
        values.put(NoteTable.Cols.DATE, noteModel.getDate().toString());
        values.put(NoteTable.Cols.COLOR, noteModel.getColor());
        return values;
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }

    private NoteCursorWrapper queryNotes(String searchText) {
        Cursor cursor = mDatabase.query(
                NoteTable.NAME,
                null,
                NoteTable.Cols.TEXT + " LIKE '%" + searchText + "%'",
                null,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }
}
