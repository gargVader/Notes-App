package com.example.notesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.R;
import com.example.notesapp.adapter.NotesAdapter;
import com.example.notesapp.data.model.Note;
import com.example.notesapp.view_models.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnItemClickListener {

    public static final int REQUEST_ADD_NOTE = 1;
    public static final int REQUEST_VIEW_NOTE = 2;
    public static final int REQUEST_DELETE_NOTE = 3;
    public static final int REQUEST_CODE_SHOW_NOTE = 4;

    public static final String KEY_NOTE = "note";
    public static final String KEY_TO_DELETE = "to_delete_note";

    public static final String TAG = "Notes";

    int noteClickedPosition = -1;

    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    NoteViewModel noteViewModel;

    ImageView imageAddNoteMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        recyclerView = findViewById(R.id.notesRecyclerView);
        setupAddNotesButton();

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter(getApplicationContext());
        recyclerView.setAdapter(notesAdapter);

        initViewModel();
        notesAdapter.setOnItemCLickListener(this);
    }

    void setupAddNotesButton() {
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_ADD_NOTE);
            }
        });
    }

    void initViewModel() {
        noteViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(NoteViewModel.class);

        noteViewModel.getAllContests().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> noteList) {
                Log.d(TAG, "onChanged: ");
                notesAdapter.submitList(noteList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_ADD_NOTE && resultCode == RESULT_OK) {
            Note note = (Note) intent.getSerializableExtra(KEY_NOTE);

            noteViewModel.insert(note);
        } else if (requestCode == REQUEST_VIEW_NOTE && resultCode == RESULT_OK) {
            if (intent.hasExtra(KEY_NOTE)) {
                Note note = (Note) intent.getSerializableExtra(KEY_NOTE);
                if (intent.getBooleanExtra(KEY_TO_DELETE, false)) {

                    noteViewModel.delete(note);
                } else {

                    noteViewModel.update(note);
                }
            }
        } else if (requestCode == REQUEST_DELETE_NOTE && resultCode == RESULT_OK) {
            Note note = (Note) intent.getSerializableExtra(KEY_NOTE);
            noteViewModel.delete(note);
        }

    }

    @Override
    public void onItemClick(Note note) {
        Log.d(TAG, "onItemClick: ");
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra(KEY_NOTE, note);
        startActivityForResult(intent, REQUEST_VIEW_NOTE);
    }
}