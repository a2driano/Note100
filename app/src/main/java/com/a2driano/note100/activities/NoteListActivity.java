package com.a2driano.note100.activities;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2driano.note100.R;
import com.a2driano.note100.data.NoteDbSchema;
import com.a2driano.note100.data.NoteStore;
import com.a2driano.note100.model.NoteModel;
import com.a2driano.note100.util.CommonToast;
import com.a2driano.note100.util.CreateDialogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.a2driano.note100.util.UtilNote.getReadableModifiedDate;

public class NoteListActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE_UUID = "com.a2driano.note100.activities.UUID";
//    public static boolean mDialogPositive = false;
    //    public static final String EXTRA_MESSAGE_SEARCH_IS_ACTIVE = "com.a2driano.note100.activities.Search";
    //    public final static String EXTRA_MESSAGE_COLOR = "com.a2driano.note100.activities.COLOR";

    public static NoteListActivity mNoteListActivity;
    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mNoteAdapter;
    private FloatingActionButton fab;
    private Toolbar mToolbar;
    private List<NoteModel> mNotes;
    private HashMap<Integer, UUID> mHashDeleteNotes = null;
    private NoteStore mNoteStore;
    private SharedPreferences mPreferences;
    private String sortingVariable;
    private boolean reverseVariable;
    private boolean mDeleteAll;
    private boolean mMenuDeleteAllVisible;
    private boolean mIsSearshActive;
    private String mSearchText;
    private SearchView mSearchView;
    private EditText mSearchViewEditText;
    private LinearLayout mSearchLayout;
    private Menu mActionBarMenu;
    static final int NOTE_START_ACTIVITY = 1;

    /**
     * Save instance variable
     */
    private final String MENU_DELETE_IS_ACTIVE = "active";
    private final String MENU_DELETE_ITEMS = "selected items";
    private final String SEARCH_IS_ACTIVE = "search is active process";
    private final String SEARCH_LETTERS = "searching letter in EditText";

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
                createIntentForNoteActivity(null);
            }
        });

        mIsSearshActive = false;
        mSearchText = "";

        loadSharedPreferences();

        mNoteRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteRecyclerView.setHasFixedSize(true);
        mNoteRecyclerView.setItemViewCacheSize(30);
        registerForContextMenu(mNoteRecyclerView);

        mDeleteAll = false;
        mMenuDeleteAllVisible = false;

        if (savedInstanceState != null) {
            if (mMenuDeleteAllVisible = savedInstanceState.getBoolean(MENU_DELETE_IS_ACTIVE)) {
                mDeleteAll = true;
                if (savedInstanceState.getSerializable(MENU_DELETE_ITEMS) != null)
                    mHashDeleteNotes = (HashMap<Integer, UUID>) savedInstanceState.getSerializable(MENU_DELETE_ITEMS);
            } else if (mIsSearshActive = savedInstanceState.getBoolean(SEARCH_IS_ACTIVE)) {
                mSearchText = savedInstanceState.getString(SEARCH_LETTERS);
            }
        }
        updateUI();
    }

    /**
     * Save sort variables in Shared Preferences
     */
    private void saveSharedPreferences(String sort, boolean reverse) {
        mPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("SORT", sort);
        editor.putBoolean("REVERSE", reverse);
//        editor.putBoolean("SEARCH", reverse);
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

    /**
     * Save instance state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putBoolean(MENU_DELETE_IS_ACTIVE, mMenuDeleteAllVisible);
        savedInstanceState.putBoolean(SEARCH_IS_ACTIVE, mIsSearshActive);
        savedInstanceState.putString(SEARCH_LETTERS, mSearchText);
        if (mNoteAdapter.mNoteHolderListForDelete.size() != 0)
            savedInstanceState.putSerializable(MENU_DELETE_ITEMS, mNoteAdapter.mNoteHolderListForDelete);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        updateUI();
    }

    /**
     * For refresh activity
     */
    private void updateUI() {
        mNoteStore = NoteStore.get(NoteListActivity.this);
        mNotes = mNoteStore.getNotes();
        sortListNoteForAdapter();
    }

    private void updateUI(String text) {
        mSearchText = text;
        mNoteStore = NoteStore.get(NoteListActivity.this);
        mNotes = mNoteStore.getNotes(text);
        sortListNoteForAdapter();
    }

    private void sortListNoteForAdapter() {
        sortNoteList(sortingVariable, reverseVariable);
        if (mNoteAdapter == null) {
            mNoteAdapter = new NoteAdapter(mNotes);
            if (mHashDeleteNotes != null) {
                mNoteAdapter.mNoteHolderListForDelete = mHashDeleteNotes;
            }
            mNoteRecyclerView.setAdapter(mNoteAdapter);
        } else {
            mNoteAdapter.setNotes(mNotes);
            mNoteAdapter.notifyDataSetChanged();
        }

    }

    /**
     * Sorting list
     */
    private void sortNoteList(String parameter, boolean reverse) {
        if (parameter.equals(NoteDbSchema.NoteTable.Cols.TEXT)) {
            Collections.sort(mNotes, new Comparator<NoteModel>() {
                @Override
                public int compare(NoteModel o1, NoteModel o2) {
                    return o1.getText().toUpperCase().compareTo(o2.getText().toUpperCase());
                }
            });
            if (!reverse)
                Collections.sort(mNotes, new Comparator<NoteModel>() {
                    @Override
                    public int compare(NoteModel o1, NoteModel o2) {
                        return o2.getText().toUpperCase().compareTo(o1.getText().toUpperCase());
                    }
                });
        } else {
            Collections.sort(mNotes, new Comparator<NoteModel>() {
                @Override
                public int compare(NoteModel o1, NoteModel o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            if (!reverse)
                Collections.sort(mNotes, new Comparator<NoteModel>() {
                    @Override
                    public int compare(NoteModel o1, NoteModel o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
        }
    }

    /**
     * Start NoteActivity with current note(UUID) or new note
     */
    private void createIntentForNoteActivity(String UUID) {
        Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
        if (UUID != null)
            intent.putExtra(EXTRA_MESSAGE_UUID, UUID);
//        intent.putExtra(EXTRA_MESSAGE_SEARCH_IS_ACTIVE, mIsSearshActive);
        startActivityForResult(intent, NOTE_START_ACTIVITY);
    }

    /**
     * Return result to current activity from NoteActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** If search not active */
        if (resultCode == RESULT_OK & mSearchText != "")
            updateUI(mSearchText);
        else if (resultCode == RESULT_OK)
            updateUI();
    }

    /**
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.mActionBarMenu = menu;

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDeleteAll == true) {
            /** Show the delete menu option */
            mActionBarMenu.setGroupVisible(R.id.main_menu, false);
            mActionBarMenu.setGroupVisible(R.id.menu_delete_actionbar, true);
            mActionBarMenu.setGroupVisible(R.id.search_block_visible, false);
            fab.setVisibility(View.GONE);
        } else {
            menu.setGroupVisible(R.id.menu_delete_actionbar, mMenuDeleteAllVisible);
        }

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_view));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchViewEditText = ((EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));

        /** Start search logic from bundle*/
        if (mIsSearshActive) {
            openSearchView();
            mSearchViewEditText.setText(mSearchText);
            mSearchView.setQuery(mSearchView.getQuery(), true);
            /** Programmatically click enter button in keyboard */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                }
            }).start();
        }
        /** Search logic */
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                updateUI(query);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Action on menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String messageToast = "";
        switch (item.getItemId()) {
            case R.id.create_note:
                createIntentForNoteActivity(null);
                break;
            /** Sorting by text */
            case R.id.menu_sort_alphabet:
                if (!sortingVariable.equals(NoteDbSchema.NoteTable.Cols.TEXT)) {
                    sortingVariable = NoteDbSchema.NoteTable.Cols.TEXT;
                    reverseVariable = true;
                    messageToast = "Sort note from A to Z";
                } else if (reverseVariable) {
                    reverseVariable = false;
                    messageToast = "Sort note from Z to A";
                } else if (!reverseVariable) {
                    reverseVariable = true;
                    messageToast = "Sort note from A to Z";
                }
                saveSharedPreferences(sortingVariable, reverseVariable);
                updateUI();
                CommonToast.showToast(this, messageToast);
                break;
            /** Sorting by date default position of top element is newest*/
            case R.id.menu_sort_date:
                if (!sortingVariable.equals(NoteDbSchema.NoteTable.Cols.DATE)) {
                    sortingVariable = NoteDbSchema.NoteTable.Cols.DATE;
                    reverseVariable = false;
                    messageToast = "Sort by newest";
                } else if (reverseVariable) {
                    reverseVariable = false;
                    messageToast = "Sort by newest";
                } else if (!reverseVariable) {
                    reverseVariable = true;
                    messageToast = "Sort by oldest";
                }
                saveSharedPreferences(sortingVariable, reverseVariable);
                updateUI();
                CommonToast.showToast(this, messageToast);
                break;
            case R.id.menu_settings:
                break;
            /** Delete menu option click*/
            case R.id.menu_delete:
                mDeleteAll = true;
                mMenuDeleteAllVisible = true;
                if (mActionBarMenu != null) {
                    mActionBarMenu.setGroupVisible(R.id.main_menu, false);
                    mActionBarMenu.setGroupVisible(R.id.menu_delete_actionbar, true);
                    mActionBarMenu.setGroupVisible(R.id.search_block_visible, false);
                }
                updateUI();
//                invalidateOptionsMenu();
                fab.setVisibility(View.GONE);
                break;
            /** Delete all option click*/
            case R.id.menu_delete_all:
                HashMap<Integer, UUID> hashMapForDelete = mNoteAdapter.mNoteHolderListForDelete;
                for (Map.Entry<Integer, UUID> entry : hashMapForDelete.entrySet()) {
                    int position = entry.getKey();
                    UUID uuid = entry.getValue();

                    NoteModel noteModel = mNoteStore.getNote(uuid);
                    mNoteStore.deleteNote(noteModel);
                }
                mHashDeleteNotes = null;
                updateUI();
//                onResume();
//                fab.setVisibility(View.VISIBLE);
                hideMenuActionBar();
                break;
            /** Cancel delete menu */
            case R.id.menu_delete_cancel:
                mHashDeleteNotes = null;
                hideMenuActionBar();
                mNoteAdapter.notifyDataSetChanged();
                break;
            /** Search icon click */
            case R.id.search_note:
                mIsSearshActive = true;
                openSearchView();
                break;
            /** Search cancel icon click */
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSearchView() {
        fab.setVisibility(View.GONE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24px);
        mToolbar.setBackgroundColor(Color.WHITE);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.divider));
            mToolbar.setElevation(12.0f);
        }

        /** add focus on search view */
        mSearchView.onActionViewExpanded();
        mSearchViewEditText.requestFocus();
//        mIsSearshActive = true;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearchView, InputMethodManager.SHOW_IMPLICIT);
        if (mActionBarMenu != null) {
            mActionBarMenu.setGroupVisible(R.id.search_block_visible, true);
            mActionBarMenu.setGroupVisible(R.id.main_menu, false);
            mActionBarMenu.setGroupVisible(R.id.menu_delete_actionbar, false);
        }
    }

    @Override
    public void onBackPressed() {
        /** hide search */
        if (mIsSearshActive) {
            /** hide up button on toolbar */
            mToolbar.setNavigationIcon(null);
            /** hide keyboard */
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchViewEditText.getWindowToken(), 0);
            mSearchViewEditText.setText("");
            mSearchText = "";
            mIsSearshActive = false;
            /** set primary color toolbar and statusbar*/
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            mToolbar.setBackgroundColor(color);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = this.getWindow();
                window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
                mToolbar.setElevation(4.0f);
            }
            hideMenuActionBar();
            return;
        }
        super.onBackPressed();
    }

    private void hideMenuActionBar() {
        mDeleteAll = false;
        mMenuDeleteAllVisible = false;
        invalidateOptionsMenu();
        fab.setVisibility(View.VISIBLE);

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
                CreateDialogUtil.noteModel = noteModel;
                CreateDialogUtil.position = position;
                new CreateDialogUtil().show(getSupportFragmentManager(), "delete note");
                return super.onContextItemSelected(item);
        }
        /** Change color action */
        noteModel.setColor(color);
        mNoteStore.updateNote(noteModel);
        mNoteAdapter.mNoteModelList.get(position).setColor(color);
        return super.onContextItemSelected(item);
    }

    /** Delete note after dialog  */
    public void deleteNote(NoteModel noteModel, int position) {
        mNoteStore.deleteNote(noteModel);
        mNoteAdapter.mNoteModelList.remove(position);
        mNoteAdapter.notifyItemRemoved(position);
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
        private Context context = NoteListActivity.this;
        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

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


                if ((mHashDeleteNotes != null) && (mHashDeleteNotes.get(position) != null)) {
                    checkBox.setChecked(true);
                }

                checkBox.setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    mNoteHolderListForDelete.put(position, uuid);
                                } else
                                    mNoteHolderListForDelete.remove(position);
                            }
                        }
                );
            }
            // Here you apply the animation when the view is bound
//            setAnimation(holder.itemView, position);
        }

        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
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
