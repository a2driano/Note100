package com.a2driano.note100.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.a2driano.note100.R;
import com.a2driano.note100.data.NoteDbSchema;
import com.a2driano.note100.data.NoteStore;
import com.a2driano.note100.model.NoteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.a2driano.note100.util.UtilNote.getReadableModifiedDate;

public class NoteListActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE_UUID = "com.a2driano.note100.activities.UUID";
    //    public final static String EXTRA_MESSAGE_COLOR = "com.a2driano.note100.activities.COLOR";
    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;
    private FloatingActionButton fab;
    private Toolbar mToolbar;
    private List<NoteModel> notes;
    private NoteStore mNoteStore;
    private SharedPreferences mPreferences;
    private String sortingVariable;
    private boolean reverseVariable;
    private boolean mDeleteAll;
    private boolean mMenuDeleteAllVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNote();
            }
        });

        loadSharedPreferences();

        mNoteRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteRecyclerView.setHasFixedSize(true);
        registerForContextMenu(mNoteRecyclerView);

        mDeleteAll = false;
        mMenuDeleteAllVisible = false;
    }

    /**
     * Create new note
     */
    private void createNote() {
        Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
        startActivity(intent);
    }

    /**
     * Save sort variables in Shared Preferences
     */
    private void saveSharedPreferences(String sort, boolean reverse) {
        mPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("SORT", sort);
        editor.putBoolean("REVERSE", reverse);
        editor.commit();
    }

    /**
     * Load sort variables in Shared Preferences
     */
    private void loadSharedPreferences() {
        mPreferences = getPreferences(MODE_PRIVATE);
        sortingVariable = mPreferences.getString("SORT", NoteDbSchema.NoteTable.Cols.DATE);
        reverseVariable = mPreferences.getBoolean("REVERSE", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * For refresh activity
     */
    private void updateUI() {
        mNoteStore = NoteStore.get(NoteListActivity.this);
        notes = mNoteStore.getNotes();

        sortNoteList(sortingVariable, reverseVariable);
        if (mNoteAdapter == null) {
            mNoteAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mNoteAdapter);
        } else {
            mNoteAdapter.setNotes(notes);
            mNoteAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Sorting list
     */
    private void sortNoteList(String parameter, boolean reverse) {
        if (parameter.equals(NoteDbSchema.NoteTable.Cols.TEXT)) {
            Collections.sort(notes, new Comparator<NoteModel>() {
                @Override
                public int compare(NoteModel o1, NoteModel o2) {
                    return o1.getText().toUpperCase().compareTo(o2.getText().toUpperCase());
                }
            });
            if (!reverse)
                Collections.sort(notes, new Comparator<NoteModel>() {
                    @Override
                    public int compare(NoteModel o1, NoteModel o2) {
                        return o2.getText().toUpperCase().compareTo(o1.getText().toUpperCase());
                    }
                });
        } else {
            Collections.sort(notes, new Comparator<NoteModel>() {
                @Override
                public int compare(NoteModel o1, NoteModel o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            if (!reverse)
                Collections.sort(notes, new Comparator<NoteModel>() {
                    @Override
                    public int compare(NoteModel o1, NoteModel o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
        }
    }

    /**
     * Start NoteActivity with current note(UUID)
     */
    private void createIntentForNoteActivity(String UUID) {
        Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
        intent.putExtra(EXTRA_MESSAGE_UUID, UUID);
        startActivity(intent);
    }

    /**
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.menu_delete_actionbar, mMenuDeleteAllVisible);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Action on menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_note:
                createNote();
                break;
            /** Sorting by text */
            case R.id.menu_sort_alphabet:
                if (!sortingVariable.equals(NoteDbSchema.NoteTable.Cols.TEXT)) {
                    sortingVariable = NoteDbSchema.NoteTable.Cols.TEXT;
                    reverseVariable = true;
                } else if (reverseVariable)
                    reverseVariable = false;
                else if (!reverseVariable)
                    reverseVariable = true;
                saveSharedPreferences(sortingVariable, reverseVariable);
                updateUI();
                break;
            /** Sorting by date
             * default position of top element is newest*/
            case R.id.menu_sort_date:
                if (!sortingVariable.equals(NoteDbSchema.NoteTable.Cols.DATE)) {
                    sortingVariable = NoteDbSchema.NoteTable.Cols.DATE;
                    reverseVariable = false;
                } else if (reverseVariable)
                    reverseVariable = false;
                else if (!reverseVariable)
                    reverseVariable = true;
                saveSharedPreferences(sortingVariable, reverseVariable);
                updateUI();
                break;
            case R.id.menu_settings:
                break;
            case R.id.menu_delete:
                mDeleteAll = true;
                updateUI();
                mMenuDeleteAllVisible = true;
                invalidateOptionsMenu();
                fab.setVisibility(View.GONE);
                break;
            case R.id.menu_delete_all:
                HashMap<Integer, UUID> hashMapForDelete = mNoteAdapter.mNoteHolderListForDelete;
                for (Map.Entry<Integer, UUID> entry : hashMapForDelete.entrySet()) {
                    int position = entry.getKey();
                    UUID uuid = entry.getValue();

                    NoteModel noteModel = mNoteStore.getNote(uuid);
                    mNoteStore.deleteNote(noteModel);
//                    mNoteAdapter.mNoteModelList.remove(position);
//                    mNoteAdapter.notifyItemRemoved(position);
                }
//                updateUI();
                onResume();
                fab.setVisibility(View.VISIBLE);
                hideMenuActionBar();
                break;
            /** Cancel delete menu */
            case R.id.menu_delete_cancel:
                hideMenuActionBar();
                mNoteAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideMenuActionBar() {
        mDeleteAll = false;
        mMenuDeleteAllVisible = false;
        invalidateOptionsMenu();

        /** Remember top position on screen
         * and after setAdapter
         * return to necessary position*/
        Parcelable parcelable = mNoteRecyclerView.getLayoutManager().onSaveInstanceState();
        mNoteRecyclerView.setAdapter(mNoteAdapter);
        mNoteRecyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        /** Find viewHolder on click position */
        int position = -1;
        try {
            position = mNoteAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }
        NoteHolder holder = (NoteHolder) mNoteRecyclerView.findViewHolderForLayoutPosition(position);

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
                mNoteAdapter.notifyItemRemoved(position);
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
        private LinearLayout mLinearLayoutInsideNote;

        public NoteHolder(View itemView) {
            super(itemView);

            mNoteTitle = (TextView) itemView.findViewById(R.id.text_title);
            mNoteDate = (TextView) itemView.findViewById(R.id.text_date);
            mNoteUuid = (TextView) itemView.findViewById(R.id.uuid);
            mNoteColor = (TextView) itemView.findViewById(R.id.color);
            mLinearLayout = (LinearLayout) findViewById(R.id.note_layout);
            mLinearLayoutInsideNote = (LinearLayout) findViewById(R.id.delete_note_host);
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
        private HashMap<Integer, UUID> mNoteHolderListForDelete;
        private int position;

        public NoteAdapter(List<NoteModel> noteModelList) {
            mNoteModelList = noteModelList;
            mNoteHolderListForDelete = new HashMap<>();
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(NoteListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_note, parent, false);

//            view.findViewById(R.id.delete_note_host).setVisibility(View.VISIBLE);//test

            NoteHolder holder = new NoteHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final NoteHolder holder, final int position) {
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

            /** When user click delete button in menu
             * in right side draw CheckBox
             * and save position and UUID of holder
             * in HashMap */
            if (mDeleteAll) {
                TextView view = (TextView) holder.itemView.findViewById(R.id.uuid);
                String uuidString = view.getText().toString();
                final UUID uuid = UUID.fromString(uuidString);
                holder.itemView.findViewById(R.id.delete_note_host).setVisibility(View.VISIBLE);

                CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.checkBox_for_delete);
                checkBox.setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    mNoteHolderListForDelete.put(position, uuid);
                                }
                            }
                        }
                );
            }
        }

        @Override
        public int getItemCount() {
            return mNoteModelList.size();
        }

        public void setNotes(List<NoteModel> notes) {
            mNoteModelList = notes;
            mNoteHolderListForDelete = new HashMap<>();
        }
    }
}
