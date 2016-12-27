package com.a2driano.note100.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a2driano.note100.R;
import com.a2driano.note100.data.NoteStore;
import com.a2driano.note100.model.NoteModel;

import java.util.List;

import static com.a2driano.note100.util.UtilNote.getReadableModifiedDate;

public class NoteListActivity extends AppCompatActivity {
    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;

    private Toolbar mToolbar;

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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });


        mNoteRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteRecyclerView.setHasFixedSize(true);

//        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        NoteStore noteStore = NoteStore.get(NoteListActivity.this);
        List<NoteModel> notes = noteStore.getNotes();

        if (mNoteAdapter == null) {
            mNoteAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mNoteAdapter);
        } else {
            mNoteAdapter.setNotes(notes);
            mNoteAdapter.notifyDataSetChanged();
        }
    }


    //    private void updateUI() {
//        NoteStore noteStore = NoteStore.get(NoteListActivity.this);
//        List<NoteModel> notes = noteStore.getNotes();
//        mNoteAdapter = new NoteAdapter(notes);
//        mNoteRecyclerView.setAdapter(mNoteAdapter);
//    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNoteTitle;
        private TextView mNoteDate;
        private TextView mNoteUuid;

        private NoteModel mNoteModel;

        public NoteHolder(View itemView) {
            super(itemView);

            mNoteTitle = (TextView) itemView.findViewById(R.id.text_title);
            mNoteDate = (TextView) itemView.findViewById(R.id.text_date);
            mNoteUuid = (TextView) itemView.findViewById(R.id.uuid);
            itemView.setOnClickListener(this);
        }

        public void bindNote(NoteModel noteModel) {
            mNoteModel = noteModel;
            mNoteTitle.setText(mNoteModel.getText());
            mNoteUuid.setText(noteModel.getId().toString());
            mNoteDate.setText(getReadableModifiedDate(mNoteModel.getDate()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
            intent.putExtra("UUID", mNoteUuid.getText());
            startActivity(intent);
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {
        private List<NoteModel> mNoteModelList;

        public NoteAdapter(List<NoteModel> noteModelList) {
            mNoteModelList = noteModelList;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(NoteListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_note, parent, false);
            return new NoteHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            NoteModel noteModel = mNoteModelList.get(position);
            holder.bindNote(noteModel);
        }

        @Override
        public int getItemCount() {
            return mNoteModelList.size();
        }

        public void setNotes(List<NoteModel> notes) {
            mNoteModelList = notes;
        }
    }
}
