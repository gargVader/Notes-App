package com.girishgarg.notesapp.activity;

import static com.girishgarg.notesapp.activity.MainActivity.KEY_NOTE;
import static com.girishgarg.notesapp.activity.MainActivity.KEY_TO_DELETE;
import static com.girishgarg.notesapp.activity.MainActivity.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.girishgarg.notesapp.R;
import com.girishgarg.notesapp.data.model.Note;
import com.girishgarg.notesapp.utils.Constants;
import com.girishgarg.notesapp.utils.KeyboardUtils;
import com.girishgarg.notesapp.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView imageNote, imageRemoveWebUrl, imageRemoveImage;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    BottomSheetBehavior bottomSheetBehavior;
    private ImageView imageSave, imageBack;
    private int selectedNoteColor = Constants.NOTE_COLOR_DEFAULT;
    private LinearLayout layoutMiscellaneous;

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 10;
    public static final int REQUEST_CODE_SELECT_IMAGE = 20;
    private String selectedImagePath = "";

    private Note noteOld;

    private AlertDialog dialogAddURL, dialogDeleteNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNoteText);
        textDateTime = findViewById(R.id.textDateTime);
        imageSave = findViewById(R.id.imageSave);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        imageBack = findViewById(R.id.imageBack);
        imageRemoveWebUrl = findViewById(R.id.imageRemoveWebUrl);
        imageRemoveImage = findViewById(R.id.imageRemoveImage);
        textDateTime.setText(Utils.getFormattedDate(new Date()));

        initButtons();
        initBottomSheet();

        if (getIntent().hasExtra(KEY_NOTE)) {
            noteOld = (Note) getIntent().getSerializableExtra(KEY_NOTE);
            setOldNote();
        }
    }

    void initButtons() {
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        imageRemoveWebUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWebURL = null;
                layoutWebURL.setVisibility(View.GONE);
            }
        });

        imageRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                imageRemoveImage.setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });


    }

    private void setOldNote() {
        if (noteOld.getTitle() != null)
            inputNoteTitle.setText(noteOld.getTitle());
        if (noteOld.getSubtitle() != null)
            inputNoteSubtitle.setText(noteOld.getSubtitle());
        if (noteOld.getNoteText() != null)
            inputNoteText.setText(noteOld.getNoteText());
        if (noteOld.getDateTime() != null)
            textDateTime.setText(noteOld.getDateTime());

        if (noteOld.getImagePath() != null && !noteOld.getImagePath().trim().isEmpty()) {
            selectedImagePath = noteOld.getImagePath();
            imageNote.setImageBitmap(BitmapFactory.decodeFile(noteOld.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            imageRemoveImage.setVisibility(View.VISIBLE);
        }

        if (noteOld.getWebLink() != null && !noteOld.getWebLink().trim().isEmpty()) {
            textWebURL.setText(noteOld.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }

        if (noteOld.getColor() != null && !noteOld.getColor().isEmpty()) {
            selectedNoteColor = Integer.parseInt(noteOld.getColor());
            updateSelectedColorHelper();
        }
        layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);

    }

    private void initBottomSheet() {
        initOnClickBehaviourForBottomSheet();
        updateSelectedColorHelper();
        initOptions();
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard", "keyboard visible: " + isVisible);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    void initOptions() {
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddURLDialog();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteNoteDialog();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    private void saveNote() {
        String title = inputNoteTitle.getText().toString().trim();
        String subTitle = inputNoteSubtitle.getText().toString().trim();
        String text = inputNoteText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Note title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (subTitle.isEmpty()) {
            Toast.makeText(this, "Note subtitle  cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(title);
        note.setNoteText(text);
        note.setSubtitle(subTitle);
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(String.valueOf(selectedNoteColor));
        note.setImagePath(selectedImagePath);

        if (layoutWebURL.getVisibility() == View.VISIBLE) {
            note.setWebLink(textWebURL.getText().toString().trim());
        }

        if (noteOld != null) {
            note.setId(noteOld.getId());
        }

        if (noteOld != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_NOTE, note);
            setResult(RESULT_OK, intent);
//            noteViewModel.update(note);
        } else {
            Intent intent = new Intent();
            intent.putExtra(KEY_NOTE, note);
            setResult(RESULT_OK, intent);
//            noteViewModel.insert(note);
        }
        Utils.hideKeyBoard(this);
        finish();
    }

    private void initOnClickBehaviourForBottomSheet() {
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        viewSubtitleIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) selectImage();
            else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        imageRemoveImage.setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
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
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddURL.dismiss();
                }
            });

        }
        dialogAddURL.show();
    }

    private void showDeleteNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_note,
                (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
        );
        builder.setView(view);
        dialogDeleteNote = builder.create();


        if (dialogDeleteNote.getWindow() != null) {
            dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        view.findViewById(R.id.textDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(KEY_NOTE, noteOld);
                intent.putExtra(KEY_TO_DELETE, true);
                setResult(RESULT_OK, intent);
                dialogDeleteNote.dismiss();
                finish();
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

    // passed as onClick in layout_miscellaneous
    public void updateSelectedColor(View view) {

        switch (view.getId()) {
            case R.id.viewColor1:
                selectedNoteColor = Constants.NOTE_COLOR_1;
                break;
            case R.id.viewColor2:
                selectedNoteColor = Constants.NOTE_COLOR_2;
                break;
            case R.id.viewColor3:
                selectedNoteColor = Constants.NOTE_COLOR_3;
                break;
            case R.id.viewColor4:
                selectedNoteColor = Constants.NOTE_COLOR_4;
                break;
            case R.id.viewColor5:
                selectedNoteColor = Constants.NOTE_COLOR_5;
                break;
        }
        Log.e(TAG, "Selected Note color=" + selectedNoteColor);
        updateSelectedColorHelper();
    }

    private void updateSelectedColorHelper() {
        // Mark all image views as empty
        // Can be optimised using a variable that keeps track of the last active color
        ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        imageColor1.setVisibility(View.GONE);
        imageColor2.setVisibility(View.GONE);
        imageColor3.setVisibility(View.GONE);
        imageColor4.setVisibility(View.GONE);
        imageColor5.setVisibility(View.GONE);

        switch (selectedNoteColor) {
            case Constants.NOTE_COLOR_1:
                imageColor1.setVisibility(View.VISIBLE);
                break;
            case Constants.NOTE_COLOR_2:
                imageColor2.setVisibility(View.VISIBLE);
                break;
            case Constants.NOTE_COLOR_3:
                imageColor3.setVisibility(View.VISIBLE);
                break;
            case Constants.NOTE_COLOR_4:
                imageColor4.setVisibility(View.VISIBLE);
                break;
            case Constants.NOTE_COLOR_5:
                imageColor5.setVisibility(View.VISIBLE);
                break;
        }
        setViewSubtitleIndicatorColor();
    }

    private void setViewSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(getResources().getColor(Constants.NOTE_COLOR[selectedNoteColor - 1]));
    }

}
