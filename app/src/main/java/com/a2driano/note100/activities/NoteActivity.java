package com.a2driano.note100.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.a2driano.note100.R;
import com.a2driano.note100.data.NoteStore;
import com.a2driano.note100.model.NoteColor;
import com.a2driano.note100.model.NoteModel;

import java.util.Date;
import java.util.UUID;

import static com.a2driano.note100.util.UtilNote.getReadableModifiedDateForNoteActivity;

public class NoteActivity extends AppCompatActivity {
    private NoteModel mNoteModel;
    private EditText mTextNote;
    private TextView mDateText;

    private NoteStore mNoteStore;
    private boolean isNew = false;
    private String mCheckColor;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.rotate_anim, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateText = (TextView) findViewById(R.id.text_date_note_activity);
        mTextNote = (EditText) findViewById(R.id.noteText);

        //Get data from intent
        createNoteView();
    }

    private void createNoteView() {
        Intent intent = getIntent();
        if (intent.getStringExtra("UUID") == null) {
            mNoteModel = new NoteModel();
            mNoteModel.setDate(new Date());
            mNoteModel.setColor(new NoteColor().getYELLOW());

            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
            isNew = true;
        } else {
            mNoteModel = NoteStore.get(this)
                    .getNote(UUID.fromString(intent.getStringExtra("UUID")));
            //add info to view
            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
            mTextNote.setText(mNoteModel.getText());
            mTextNote.setSelection(mTextNote.getText().length());

            mCheckColor = mNoteModel.getColor();
        }
    }

    //When we return to NoteListActivity, instance of NoteModel add or update in to DB
    @Override
    protected void onPause() {
        super.onPause();
        mNoteStore = NoteStore.get(this);
        if (isNew && (mTextNote.getText().length() != 0)) {
            //if note is new and user create text
            mNoteModel.setText(mTextNote.getText().toString());
            mNoteStore.addNote(mNoteModel);
        } else if (!isNew
                && (!mNoteModel.getText().equals(mTextNote.getText().toString())
                || !mCheckColor.equals(mNoteModel.getColor().toString()))) {
            //if note is exist
            mNoteModel.setText(mTextNote.getText().toString());
            mNoteModel.setColor(mCheckColor);
            mNoteStore.updateNote(mNoteModel);
        }
    }

    //Class for draw line in text field
    public static class LineEditText extends EditText {
        private Rect mRect;
        private Paint mPaint;

        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.BLUE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int height = getHeight();
            int line_height = getLineHeight();

            int count = height / line_height;
            if (getLineCount() > count)
                count = getLineCount();

            Rect r = mRect;
            Paint paint = mPaint;
            int baseline = getLineBounds(0, r);

            for (int i = 0; i < count; i++) {
                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
                baseline += getLineHeight();
                super.onDraw(canvas);
            }
        }
    }
}
