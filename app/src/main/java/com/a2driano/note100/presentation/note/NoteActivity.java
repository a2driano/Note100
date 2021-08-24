package com.a2driano.note100.presentation.note;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.a2driano.note100.R;
import com.a2driano.note100.data.db.NoteStore;
import com.a2driano.note100.data.model.NoteColor;
import com.a2driano.note100.data.model.NoteModel;
import com.a2driano.note100.util.CreateDialogNoteActivityDeleteUtil;

import java.util.Date;
import java.util.UUID;

import static com.a2driano.note100.presentation.list.NoteListActivity.EXTRA_MESSAGE_UUID;
import static com.a2driano.note100.presentation.list.NoteListActivity.sRefreshData;
import static com.a2driano.note100.util.AnimationUtil.visibleElementAfterTransition;
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
    private FrameLayout mTransitionView;
    private FrameLayout mTransitionWhiteBackground;
    private FrameLayout mTransitionViewBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.rotate_anim, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTransitionView = (FrameLayout) findViewById(R.id.transition_view);
        mTransitionViewBottom = (FrameLayout) findViewById(R.id.transition_view_bottom);

        //for toolbar menus
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDateText = (TextView) findViewById(R.id.text_date_note_activity);
        mNoteText = (EditText) findViewById(R.id.noteText);
        mFrameTimeLayout = (FrameLayout) findViewById(R.id.time_note_layout_host);
        mFrameTimeLayout.setVisibility(View.GONE);
        /** Get data from intent */
        createNoteView();
    }

    private void createNoteView() {
        Intent intent = this.getIntent();
        String color = new NoteColor().getYELLOW();//default
        if (intent.getStringExtra(EXTRA_MESSAGE_UUID) == null & !mNextStep) {
            mNoteModel = new NoteModel();
            mNoteModel.setDate(new Date());
            mNoteModel.setText("");
            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
            isNew = true;
        } else if (!mNextStep) {
            mNoteModel = NoteStore.get(this)
                    .getNote(UUID.fromString(intent.getStringExtra(EXTRA_MESSAGE_UUID)));
            //add info to view
            mNoteText.setText(mNoteModel.getText());
            mDateText.setText(getReadableModifiedDateForNoteActivity(mNoteModel.getDate()));
            //get color from intent
            color = mNoteModel.getColor().toUpperCase();
            //hide keyboard if note is create, for user first can just read
            mDateText.setFocusableInTouchMode(true);
            mDateText.requestFocus();
            isNew = false;

            //set transition
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mToolbar.setTransitionName(getString(R.string.transition_view));
            }
        }
        visibleElementAfterTransition(mFrameTimeLayout, this);
        mFrameTimeLayout.setVisibility(View.VISIBLE);

//        int colorLayout = getResources().getIdentifier(color, "color", getPackageName());
//        mToolbar.setBackgroundResource(colorLayout);
        setStartColor(color);
        mCheckColor = color;


    }

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
                new CreateDialogNoteActivityDeleteUtil().show(getSupportFragmentManager(), "delete note activity");
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void changeColor(String color) {
        int colorSource = getResources().getIdentifier(color, "color", getPackageName());
        int colorTo = ContextCompat.getColor(this, colorSource);
        int colorBackground = 0;
        //get curent color of toolbar
        Drawable background = mToolbar.getBackground();
        if (background instanceof ColorDrawable)
            colorBackground = ((ColorDrawable) background).getColor();

        if (colorSource == colorBackground)
            return;

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorBackground, colorTo);
        colorAnimation.setDuration(300); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mToolbar.setBackgroundColor((int) animator.getAnimatedValue());
                mTransitionView.setBackgroundColor((int) animator.getAnimatedValue());
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Window window = NoteActivity.this.getWindow();
                    window.setStatusBarColor((int) animator.getAnimatedValue());
                }
            }
        });
        colorAnimation.start();
        mNoteModel.setColor(color);
    }

    private void setStartColor(String color) {
        int colorLayout = getResources().getIdentifier(color, "color", getPackageName());
        mToolbar.setBackgroundResource(colorLayout);
        mTransitionView.setBackgroundResource(colorLayout);
        mTransitionViewBottom.setBackgroundResource(colorLayout);
        mNoteModel.setColor(color);
        //set status bar color
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, colorLayout));
        }
    }

    public void deleteNote() {
        if (isNew) {
//            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }
        } else {
            /** hide keyboard */
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mNoteText.getWindowToken(), 0);

            mNoteStore = NoteStore.get(this);
            mNoteStore.deleteNote(mNoteModel);
            sRefreshData = true;
//            finish();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }
        }
    }

    /**
     * Return result to home activity
     */
    @Override
    public void onBackPressed() {
//        /** hide keyboard */
//        hideElements(mFrameTimeLayout, this);
//        mFrameTimeLayout.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNoteText.getWindowToken(), 0);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
        super.onBackPressed();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    //    public static class CreateDeleteNoteDialog extends DialogFragment {
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the Builder class for convenient dialog construction
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setMessage(R.string.dialog_context_delete_note)
//                    .setPositiveButton(R.string.dialog_context_delete_button_text, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            deleteNote();
//                        }
//                    })
//                    .setNegativeButton(R.string.dialog_context_cancel_button_text, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            // User cancelled the dialog
//                        }
//                    });
//            // Create the AlertDialog object and return it
//            return builder.create();
//        }
//    }

    /**
     * Class for draw line in text field
     */
    public static class LineEditText extends androidx.appcompat.widget.AppCompatEditText {
        private Rect mRect;
        private Paint mPaint;

        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
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
