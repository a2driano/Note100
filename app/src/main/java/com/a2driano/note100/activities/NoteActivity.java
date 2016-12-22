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
import com.a2driano.note100.model.NoteModel;

public class NoteActivity extends AppCompatActivity {
    private TextView mDateText;
    private EditText mTextNote;
    private NoteModel mNoteModel;

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

        //Take data in intent
        Intent intent = getIntent();
        mTextNote.setText(intent.getStringExtra("TEXT"));
        mDateText.setText(intent.getStringExtra("DATE"));
        mTextNote.setSelection(mTextNote.getText().length());
    }


    //When we return to NoteListActivity, instance of NoteModel update in to DB
    @Override
    protected void onPause() {
        super.onPause();
        //if mNoteModel is created earlier
        if (mNoteModel != null)
            NoteStore.get(this).updateNote(mNoteModel);
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
