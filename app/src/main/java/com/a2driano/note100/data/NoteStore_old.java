//package com.a2driano.note100.data;
//
//import android.content.Context;
//
//import com.a2driano.note100.model.NoteModel;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * Created by a2driano on 20.12.2016.
// */
//
//public class NoteStore_old {
//    private static NoteStore_old sNoteStore;
//    private List<NoteModel> mNoteModelList;
//
//    private NoteStore_old(Context context) {
//        mNoteModelList = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            NoteModel noteModel = new NoteModel();
//            noteModel.setText("Your text #" + i);
////            noteModel.setDate(new Date());
//            noteModel.setColor("Yellow");
//            mNoteModelList.add(noteModel);
//        }
//    }
//
//    public static NoteStore_old get(Context context) {
//        if (sNoteStore == null)
//            sNoteStore = new NoteStore_old(context);
//        return sNoteStore;
//    }
//
//
//    public NoteModel getNote(UUID id) {
//        for (NoteModel note : mNoteModelList) {
//            if (note.getId().equals(id))
//                return note;
//        }
//        return null;
//    }
//
//    public List<NoteModel> getNotes() {
//        return mNoteModelList;
//    }
//
//}
