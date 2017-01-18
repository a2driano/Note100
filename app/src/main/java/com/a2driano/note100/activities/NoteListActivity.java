package com.a2driano.note100.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2driano.note100.R;
import com.a2driano.note100.data.NoteStore;
import com.a2driano.note100.model.NoteModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.a2driano.note100.util.UtilNote.getReadableModifiedDate;

public class NoteListActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE_UUID = "com.a2driano.note100.activities.UUID";
//    public final static String EXTRA_MESSAGE_COLOR = "com.a2driano.note100.activities.COLOR";

    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;

    private Toolbar mToolbar;
    private LinearLayout mLinearLayout;
    private int mSelectedPosition;
    private List<NoteModel> notes;
    private NoteStore mNoteStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        mNoteRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteRecyclerView.setHasFixedSize(true);
        registerForContextMenu(mNoteRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        mNoteStore = NoteStore.get(NoteListActivity.this);
        List<NoteModel> notes = mNoteStore.getNotes();

        if (mNoteAdapter == null) {
            mNoteAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mNoteAdapter);
        } else {
            mNoteAdapter.setNotes(notes);
            mNoteAdapter.notifyDataSetChanged();
        }
    }

    private void createIntentForNoteActivity(String UUID) {
        Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
        intent.putExtra(EXTRA_MESSAGE_UUID, UUID);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        /** Find viewHolder on click position */
        int position = -1;
        try {
            position = (mNoteAdapter.getPosition());
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        NoteHolder holder = mNoteAdapter.mNoteHolderList.get(position);

//        for (int i = 0; i < mNoteAdapter.mNoteHolderList.size(); i++) {
//            System.out.println("---------mNoteHolderList --- position:" + i + " ---and---" + mNoteAdapter.mNoteHolderList.get(i));
//
//        }

        /** Get NoteStore instance and current NoteModel object */
        mNoteStore = NoteStore.get(NoteListActivity.this);
        UUID id = mNoteAdapter.mNoteModelList.get(position).getId();
        NoteModel noteModel = mNoteStore.getNote(id);

        String color = "YELLOW";
        switch (item.getItemId()) {
            /** Color change case`s */
            case R.id.color_YELLOW:
                holder.itemView.setBackgroundResource(R.color.YELLOW);
                color = "YELLOW";
                break;
            case R.id.color_RED:
                holder.itemView.setBackgroundResource(R.color.RED);
                color = "RED";
                break;
            case R.id.color_GREEN:
                holder.itemView.setBackgroundResource(R.color.GREEN);
                color = "GREEN";
                break;
            case R.id.color_BLUE:
                holder.itemView.setBackgroundResource(R.color.BLUE);
                color = "BLUE";
                break;
            case R.id.color_GREY:
                holder.itemView.setBackgroundResource(R.color.GREY);
                color = "GREY";
                break;
            /** Delete note from app; */
            case R.id.context_menu_delete:
                mNoteStore.deleteNote(noteModel);
                mNoteAdapter.mNoteModelList.remove(position);
                mNoteAdapter.mNoteHolderList.remove(position);
                mNoteAdapter.notifyItemRemoved(position);
                mNoteAdapter.notifyItemRangeChanged(position, mNoteAdapter.mNoteHolderList.size());
                return super.onContextItemSelected(item);
        }
        /** Change color action */
        noteModel.setColor(color);
        mNoteStore.updateNote(noteModel);
        return super.onContextItemSelected(item);
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener {

        private TextView mNoteTitle;
        private TextView mNoteDate;
        private TextView mNoteUuid;
        private TextView mNoteColor;

        private NoteModel mNoteModel;
        private LinearLayout mLinearLayout;

        public NoteHolder(View itemView) {
            super(itemView);

            mNoteTitle = (TextView) itemView.findViewById(R.id.text_title);
            mNoteDate = (TextView) itemView.findViewById(R.id.text_date);
            mNoteUuid = (TextView) itemView.findViewById(R.id.uuid);
            mNoteColor = (TextView) itemView.findViewById(R.id.color);
            mLinearLayout = (LinearLayout) findViewById(R.id.note_layout);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setLongClickable(true);
        }


        public void bindNote(NoteModel noteModel) {
            mNoteModel = noteModel;
            mNoteTitle.setText(mNoteModel.getText());
            mNoteUuid.setText(mNoteModel.getId().toString());
            mNoteColor.setText(mNoteModel.getColor());
            mNoteDate.setText(getReadableModifiedDate(mNoteModel.getDate()));
        }

        @Override
        public void onClick(View v) {
            String uuidString = mNoteUuid.getText().toString();
            createIntentForNoteActivity(uuidString);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }


    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {
        private List<NoteModel> mNoteModelList;
        private List<NoteHolder> mNoteHolderList;
        private int position;

        public NoteAdapter(List<NoteModel> noteModelList) {
            mNoteModelList = noteModelList;
            mNoteHolderList = new ArrayList<>();
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public NoteHolder getItem(int position) {
            return mNoteHolderList.get(position);
        }

        public void addItem(NoteHolder holder) {
            mNoteHolderList.add(holder);
        }


        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(NoteListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_note, parent, false);

            NoteHolder holder = new NoteHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final NoteHolder holder, int position) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(holder.getAdapterPosition());
                    return false;
                }
            });
            NoteModel noteModel = mNoteModelList.get(position);
            holder.bindNote(noteModel);
            /** Change color */
            String color = noteModel.getColor().toUpperCase();
            int colorLayout = getResources().getIdentifier(color, "color", getPackageName());
            holder.itemView.setBackgroundResource(colorLayout);
            addItem(holder);
        }

        @Override
        public int getItemCount() {
            return mNoteModelList.size();
        }

        public void setNotes(List<NoteModel> notes) {
            mNoteModelList = notes;
            mNoteHolderList = new ArrayList<>();
        }
    }
}
