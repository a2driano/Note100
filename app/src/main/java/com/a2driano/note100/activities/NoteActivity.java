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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.a2driano.note100.R;
import com.a2driano.note100.data.NoteStore;
import com.a2driano.note100.model.NoteColor;
import com.a2driano.note100.model.NoteModel;

import java.util.Date;
import java.util.UUID;

import static com.a2driano.note100.activities.NoteListActivity.EXTRA_MESSAGE_UUID;
import static com.a2driano.note100.activities.NoteListActivity.sRefreshData;
import static com.a2driano.note100.util.AnimationUtil.visibleAnimationColorTextDown;
import static com.a2driano.note100.util.UtilNote.getReadableModifiedDateForNoteActivity;

public class NoteActivity extends AppCompatActivity {
    private NoteModel mNoteModel;
    private EditText mNoteText;
    private TextView mDateText;
    private NoteStore mNoteStore;
    private boolean isNew = false;
    private boolean mNextStep = false;
    private String mCheckColor;
    private Toolbar mToolbar;
    private int mResultForIntent;
    private FrameLayout mFrameTimeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.rotate_anim, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        /** set primary color toolbar */
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            mToolbar.setBackgroundColor(color);
        }
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateText = (TextView) findViewById(R.id.text_date_note_activity);
        mNoteText = (EditText) findViewById(R.id.noteText);
        mFrameTimeLayout = (FrameLayout) findViewById(R.id.time_note_layout_host);

        /** Get data from intent */
        createNoteView();
    }

        private void createNoteView() {
        Intent intent = this.getIntent();
        if (intent.getStringExtra(EXTRA_MESSAGE_UUID) == null & !mNextStep) {
            mNoteModel = new NoteModel();
            mNoteModel.setDate(new Date());
            mNoteModel.setColor(new NoteColor().getYELLOW());
            mNoteModel.setText("");

            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
            isNew = true;
        } else if (!mNextStep) {
            mNoteModel = NoteStore.get(this)
                    .getNote(UUID.fromString(intent.getStringExtra(EXTRA_MESSAGE_UUID)));
            //add info to view
            mNoteText.setText(mNoteModel.getText());
            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));

            //set color
            String color = mNoteModel.getColor().toUpperCase();
            int colorDateBackground = getResources().getIdentifier(color, "color", getPackageName());

//            mDateText.setBackgroundResource(colorDateBackground);
            mFrameTimeLayout.setBackgroundResource(colorDateBackground);

            //hide keyboard if note is create, for user first can just read
            mDateText.setFocusableInTouchMode(true);
            mDateText.requestFocus();
//            isNew = false;
        }
        mCheckColor = mNoteModel.getColor();
        //animation
//        visibleAnimationColorTextDown(mDateText, this);
        visibleAnimationColorTextDown(mFrameTimeLayout, this);
    }



//    private void createNoteView() {
//        Intent intent = this.getIntent();
//        if (intent.getStringExtra(EXTRA_MESSAGE_UUID) == null & !mNextStep) {
//            mNoteModel = new NoteModel();
//            mNoteModel.setDate(new Date());
//            mNoteModel.setColor(new NoteColor().getYELLOW());
//            mNoteModel.setText("");
//
//            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
//            isNew = true;
//        } else if (!mNextStep) {
//            mNoteModel = NoteStoreAsync.get(this)
//                    .getNote(UUID.fromString(intent.getStringExtra(EXTRA_MESSAGE_UUID)));
//            //add info to view
//            mNoteText.setText(mNoteModel.getText());
//            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
//
//            //set color
//            String color = mNoteModel.getColor().toUpperCase();
//            int colorDateBackground = getResources().getIdentifier(color, "color", getPackageName());
//
//            mDateText.setBackgroundResource(colorDateBackground);
//
//            //hide keyboard if note is create, for user first can just read
//            mDateText.setFocusableInTouchMode(true);
//            mDateText.requestFocus();
////            isNew = false;
//        }
//        mCheckColor = mNoteModel.getColor();
//        //animation
//        visibleAnimationColorTextDown(mDateText, this);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * For back narrow in toolbar click
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        if (menuItem.getItemId() == android.R.id.home) {
//            onBackPressed();
//        }
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            /** Color change case`s */
            case R.id.change_color:
                break;
            case R.id.color_YELLOW:
                changeColor("YELLOW");
                break;
            case R.id.color_RED:
                changeColor("RED");
                break;
            case R.id.color_GREEN:
                changeColor("GREEN");
                break;
            case R.id.color_BLUE:
                changeColor("BLUE");
                break;
            case R.id.color_GREY:
                changeColor("GREY");
                break;
            case R.id.menu_delete:
                if (isNew) {
                    finish();
                } else {
                    mNoteStore = NoteStore.get(this);
                    mNoteStore.deleteNote(mNoteModel);
                    sRefreshData = true;
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void changeColor(String color) {
        int colorLayout = getResources().getIdentifier(color, "color", getPackageName());
//        mDateText.setBackgroundResource(colorLayout);
        mFrameTimeLayout.setBackgroundResource(colorLayout);
        mNoteModel.setColor(color);
    }

    /**
     * Return result to home activity
     */
    @Override
    public void onBackPressed() {
//        /** hide keyboard */
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mNoteText.getWindowToken(), 0);
        Intent intent = new Intent();
        mNoteStore = NoteStore.get(this);
        if (isNew & (mNoteText.getText().length() != 0)) {
            //if note is new and user create text
            mNoteModel.setText(mNoteText.getText().toString());
            mNoteStore.addNote(mNoteModel);
            mResultForIntent = RESULT_OK;
        } else if (!isNew
                & (!mNoteModel.getText().equals(mNoteText.getText().toString())
                || !mCheckColor.equals(mNoteModel.getColor()))) {
            //if note is exist
            mNoteModel.setText(mNoteText.getText().toString());
//            mNoteModel.setColor(mCheckColor);
            mNoteStore.updateNote(mNoteModel);
            mResultForIntent = RESULT_OK;
        } else {
            mResultForIntent = RESULT_CANCELED;
        }
        isNew = false;

        setResult(mResultForIntent, intent);
        super.onBackPressed();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        /** hide keyboard */
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mNoteText.getWindowToken(), 0);
//    }

    //    /**
//     * When we return to NoteListActivity, instance of NoteModel add or update in to DB
//     */
//    @Override
//    protected void onStop() {
//        super.onStop();
//        /** for onResume method call */
//        mNoteText.setText(mNoteModel.getText());
//        mCheckColor = mNoteModel.getColor();
//        mNextStep = true;
//
//    }

//    @Override
//    protected void onStop() {
//        /** hide keyboard */
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(mNoteText.getWindowToken(), 0);
//        super.onStop();
//    }

    /**
     * Class for draw line in text field
     */
    public static class LineEditText extends android.support.v7.widget.AppCompatEditText {
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
