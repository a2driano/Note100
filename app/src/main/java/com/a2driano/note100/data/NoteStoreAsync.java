//package com.a2driano.note100.data;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.AsyncTask;
//import android.widget.TextView;
//
//import com.a2driano.note100.model.NoteModel;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import static com.a2driano.note100.data.NoteDbSchema.*;
//import static com.a2driano.note100.util.AnimationUtil.visibleAnimationColorTextDown;
//import static com.a2driano.note100.util.UtilNote.getReadableModifiedDateForNoteActivity;
//
///**
// * Created by Andrii Papai on 20.12.2016.
// */
//
//public class NoteStoreAsync {
//    private static NoteStoreAsync sNoteStore;
//    private List<NoteModel> mNoteModelList;
//    private Context mContext;
//    private SQLiteDatabase mDatabase;
//
//    private NoteStoreAsync(Context context) {
//        mContext = context;
//        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();
//
//        mNoteModelList = new ArrayList<>();
//    }
//
//    public static NoteStoreAsync get(Context context) {
//        if (sNoteStore == null)
//            sNoteStore = new NoteStoreAsync(context);
//        return sNoteStore;
//    }
//
//    public List<NoteModel> getNotes() {
//        mNoteModelList = new ArrayList<>();
//        new AsyncTask<Void, Void, List<NoteModel>>() {
//            @Override
//            protected List<NoteModel> doInBackground(final Void... params) {
//                NoteCursorWrapper cursor = queryNotes(null, null);
//                try {
//                    cursor.moveToFirst();
//                    while (!cursor.isAfterLast()) {
//                        mNoteModelList.add(cursor.getNote());
//                        cursor.moveToNext();
//                    }
//                } finally {
//                    cursor.close();
//                }
//                return mNoteModelList;
//            }
//        }.execute();
//        return mNoteModelList;
//    }
//
//    /**
//     * Search func
//     */
//    public List<NoteModel> getNotes(final String searchText) {
//        mNoteModelList = new ArrayList<>();
//        new AsyncTask<Void, Void, List<NoteModel>>() {
//            @Override
//            protected List<NoteModel> doInBackground(final Void... params) {
//                NoteCursorWrapper cursor = queryNotes(searchText);
//                try {
//                    cursor.moveToFirst();
//                    while (!cursor.isAfterLast()) {
//                        mNoteModelList.add(cursor.getNote());
//                        cursor.moveToNext();
//                    }
//                } finally {
//                    cursor.close();
//                }
//                return mNoteModelList;
//            }
//        }.execute();
//
//        return mNoteModelList;
//    }
//
//    public void addNote(final NoteModel noteModel) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(final Void... params) {
//                ContentValues values = getContentValues(noteModel);
//                mDatabase.insert(NoteTable.NAME, null, values);
//                return null;
//            }
//        }.execute();
//
//    }
//
//    //get note method for NoteActivity logic
//    public NoteModel getNote(final UUID id, final TextView text, final TextView date, String CheckColor, final Context context) {
//
////        mNoteModel = NoteStoreAsync.get(this).getNote(UUID.fromString(intent.getStringExtra(EXTRA_MESSAGE_UUID)), mNoteText, mDateText, mCheckColor, this);
//        final NoteModel[] noteModel = {new NoteModel()};
//        new AsyncTask<Void, Void, NoteModel>() {
//            @Override
//            protected NoteModel doInBackground(final Void... params) {
//                NoteCursorWrapper cursor = queryNotes(
//                        NoteTable.Cols.UUID + " = ?",
//                        new String[]{id.toString()}
//                );
//
//                try {
//                    if (cursor.getCount() == 0) {
//                        return null;
//                    }
//                    cursor.moveToFirst();
//                    return noteModel[0] = cursor.getNote();
//                } finally {
//                    cursor.close();
//                }
//            }
//
//            @Override
//            protected void onPostExecute(NoteModel noteModel) {
//                text.setText(noteModel.getText());
//                date.setText(getReadableModifiedDateForNoteActivity(noteModel.getDate()));
//                String color = noteModel.getColor().toUpperCase();
//                int colorDateBackground = context.getResources().getIdentifier(color, "color", context.getPackageName());
//                date.setBackgroundResource(colorDateBackground);
//                date.setFocusableInTouchMode(true);
//                date.requestFocus();
////                CheckColor = noteModel.getColor();
//
//                //animation
//                visibleAnimationColorTextDown(date, context);
//
//                super.onPostExecute(noteModel);
//            }
//        }.execute();
//        return noteModel[0];
//    }
//
//    public NoteModel getNote(UUID id) {
//        NoteCursorWrapper cursor = queryNotes(
//                NoteTable.Cols.UUID + " = ?",
//                new String[]{id.toString()}
//        );
//
//        try {
//            if (cursor.getCount() == 0) {
//                return null;
//            }
//            cursor.moveToFirst();
//            return cursor.getNote();
//        } finally {
//            cursor.close();
//        }
//    }
//
//    public void updateNote(final NoteModel noteModel) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(final Void... params) {
//                String uuidString = noteModel.getId().toString();
//                ContentValues values = getContentValues(noteModel);
//                mDatabase.update(NoteTable.NAME, values,
//                        NoteTable.Cols.UUID + " = ? ",
//                        new String[]{uuidString});
//                return null;
//            }
//        }.execute();
//    }
//
//    public void deleteNote(final NoteModel noteModel) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(final Void... params) {
//                String uuidString = noteModel.getId().toString();
//                mDatabase.delete(NoteTable.NAME,
//                        NoteTable.Cols.UUID + " = ? ",
//                        new String[]{uuidString});
//                return null;
//            }
//        }.execute();
//    }
//
//    public void deleteAllSelectedNotes(final HashMap<Integer, UUID> hashMapForDelete) {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(final Void... params) {
//                for (Map.Entry<Integer, UUID> entry : hashMapForDelete.entrySet()) {
//                    UUID uuid = entry.getValue();
//                    mDatabase.delete(NoteTable.NAME,
//                            NoteTable.Cols.UUID + " = ? ",
//                            new String[]{uuid.toString()});
//                }
//                return null;
//            }
//        }.execute();
//    }
//
//    private static ContentValues getContentValues(NoteModel noteModel) {
//        ContentValues values = new ContentValues();
//        values.put(NoteTable.Cols.UUID, noteModel.getId().toString());
//        values.put(NoteTable.Cols.TEXT, noteModel.getText());
//        values.put(NoteTable.Cols.DATE, noteModel.getDate().toString());
//        values.put(NoteTable.Cols.COLOR, noteModel.getColor());
//        return values;
//    }
//
//    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
//        Cursor cursor = mDatabase.query(
//                NoteTable.NAME,
//                null,
//                whereClause,
//                whereArgs,
//                null,
//                null,
//                null
//        );
//        return new NoteCursorWrapper(cursor);
//    }
//
//    private NoteCursorWrapper queryNotes(String searchText) {
//        Cursor cursor = mDatabase.query(
//                NoteTable.NAME,
//                null,
//                NoteTable.Cols.TEXT + " LIKE '%" + searchText + "%'",
//                null,
//                null,
//                null,
//                null
//        );
//        return new NoteCursorWrapper(cursor);
//    }
//}
