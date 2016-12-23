package com.a2driano.note100.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Andrii Papai on 21.12.2016.
 */

public class NoteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "note100Base.db";

    public NoteBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NoteDbSchema.NoteTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                NoteDbSchema.NoteTable.Cols.UUID + ", " +
                NoteDbSchema.NoteTable.Cols.TEXT + ", " +
                NoteDbSchema.NoteTable.Cols.DATE + ", " +
                NoteDbSchema.NoteTable.Cols.COLOR +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
