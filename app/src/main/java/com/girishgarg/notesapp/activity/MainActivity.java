package com.girishgarg.notesapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesapp.R;
import com.girishgarg.notesapp.adapter.NotesAdapter;
import com.girishgarg.notesapp.data.model.Note;
import com.girishgarg.notesapp.utils.Utils;
import com.girishgarg.notesapp.view_models.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnItemClickListener {

    public static final int REQUEST_ADD_NOTE = 1;
    public static final int REQUEST_VIEW_NOTE = 2;
    public static final int REQUEST_DELETE_NOTE = 3;
    public static final int REQUEST_QUICK_ACTIONS_LINK = 4;
    public static final int REQUEST_QUICK_ACTIONS_IMAGE = 5;

    public static final String KEY_NOTE = "note";
    public static final String KEY_TO_DELETE = "to_delete_note";

    public static final String TAG = "Notes";

    RecyclerView recyclerView;
    NotesAdapter notesAdapter;
    NoteViewModel noteViewModel;
    EditText inputSearch;
    ImageView imageAddImage, imageAddWebLink;

    ImageView imageAddNoteMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        recyclerView = findViewById(R.id.notesRecyclerView);
        inputSearch = findViewById(R.id.inputSearch);
        imageAddImage = findViewById(R.id.imageAddImage);
        imageAddWebLink = findViewById(R.id.imageAddWebLink);
        setupAddNotesButton();

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter(getApplicationContext());
        recyclerView.setAdapter(notesAdapter);

        initViewModel();
        initSearchBox();
        initQuickActions();

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

        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> noteList) {
                Log.d(TAG, "onChanged: ");
                notesAdapter.submitList(noteList);
            }
        });
    }

    void initSearchBox() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchKeyword = "%" + s.toString() + "%";
                noteViewModel.getSearchNotes(searchKeyword).observe(MainActivity.this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        notesAdapter.submitList(notes);
                    }
                });
            }
        });
    }

    void initQuickActions() {
        imageAddWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddURLDialog();
            }
        });

        imageAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    void selectImage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CreateNoteActivity.REQUEST_CODE_STORAGE_PERMISSION
            );
        } else {
            selectImageHelper();
        }
    }

    void selectImageHelper() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CreateNoteActivity.REQUEST_CODE_SELECT_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if ((requestCode == REQUEST_ADD_NOTE || requestCode == REQUEST_QUICK_ACTIONS_LINK || requestCode == REQUEST_QUICK_ACTIONS_IMAGE) && resultCode == RESULT_OK) {
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
        } else if (requestCode == CreateNoteActivity.REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (intent != null) {
                Uri selectedImageUri = intent.getData();
                if (selectedImageUri != null) {
                    Note note = new Note();
                    note.setImagePath(Utils.getPathFromUri(intent.getData(), this));

                    Intent intentNewNote = new Intent(MainActivity.this, CreateNoteActivity.class);
                    intentNewNote.putExtra(KEY_NOTE, note);

                    startActivityForResult(intentNewNote, REQUEST_QUICK_ACTIONS_IMAGE);
                }
            }
        }

    }

    @Override
    public void onItemClick(Note note) {
        Log.d(TAG, "onItemClick: ");
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra(KEY_NOTE, note);
        startActivityForResult(intent, REQUEST_VIEW_NOTE);
    }

    @Override
    public void onItemLongCLick(Note note) {
        showDeleteNoteDialog(note);
    }

    private void showDeleteNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_note,
                (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
        );
        builder.setView(view);
        AlertDialog dialogDeleteNote = builder.create();


        if (dialogDeleteNote.getWindow() != null) {
            dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        view.findViewById(R.id.textDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteViewModel.delete(note);
                dialogDeleteNote.dismiss();
            }
        });

        view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteNote.dismiss();
            }
        });
        dialogDeleteNote.show();
    }

    private void showAddURLDialog() {
        AlertDialog dialogAddURL;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_add_url,
                (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
        );
        builder.setView(view);
        dialogAddURL = builder.create();


        if (dialogAddURL.getWindow() != null) {
            dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        EditText inputURL = view.findViewById(R.id.inputURL);
        inputURL.requestFocus();

        view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputURL.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                    Toast.makeText(MainActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                } else {
                    String text = inputURL.getText().toString();
                    Note note = new Note();
                    note.setWebLink(text);

                    Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                    intent.putExtra(KEY_NOTE, note);

                    dialogAddURL.dismiss();
                    startActivityForResult(intent, REQUEST_QUICK_ACTIONS_LINK);

//                    textWebURL.setText(inputURL.getText().toString());
//                    layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        });

        view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddURL.dismiss();
            }
        });
        dialogAddURL.show();
    }


}