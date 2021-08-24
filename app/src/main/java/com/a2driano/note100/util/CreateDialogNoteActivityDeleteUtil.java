package com.a2driano.note100.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.a2driano.note100.R;
import com.a2driano.note100.activities.NoteActivity;

/**
 * Created by Andrii Papai on 08.07.2017.
 */

public class CreateDialogNoteActivityDeleteUtil extends DialogFragment {
    NoteActivity mNoteActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_context_delete_note)
                .setPositiveButton(R.string.dialog_context_delete_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mNoteActivity = (NoteActivity) getActivity();
                        mNoteActivity.deleteNote();
                    }
                })
                .setNegativeButton(R.string.dialog_context_cancel_button_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
