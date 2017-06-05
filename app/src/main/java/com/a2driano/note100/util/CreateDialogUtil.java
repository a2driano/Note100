package com.a2driano.note100.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.a2driano.note100.R;
import com.a2driano.note100.activities.NoteListActivity;
import com.a2driano.note100.model.NoteModel;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Andrii Papai on 30.04.2017.
 */

public class CreateDialogUtil extends DialogFragment {

    public static final int ID_DELETE_NOTE = 0;
    public static final int ID_DELETE_ALL_SELECTED_NOTES = 1;

    public static NoteModel noteModel;
    public static int position;
    public static HashMap<Integer, UUID> hashMapDelete;
    public static String setTextMessage;
    public static String setTextPositiveButton;
    public static String setTextNegativeButton;

    private int id;

//    public interface YesNoListener {
//        void onYes();
//        void onNo();
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(setTextMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_context_delete_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NoteListActivity noteListActivity = (NoteListActivity) getActivity();
                        if (hashMapDelete == null) {
                            noteListActivity.deleteNote(noteModel, position);
                        } else {
                            noteListActivity.deleteNotes(hashMapDelete);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_context_cancel_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        hashMapDelete = null;
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

//    switch(id){
//        case ID_DELETE_NOTE:
//            break;
//        case ID_DELETE_ALL_SELECTED_NOTES:
//            break;
//
//    }
}
