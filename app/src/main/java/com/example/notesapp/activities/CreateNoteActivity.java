package com.example.notesapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesapp.R;
import com.example.notesapp.database.NotesDatabase;
import com.example.notesapp.database.NotesDatabase_Impl;
import com.example.notesapp.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String LOG_TAG = CreateNoteActivity.class.getSimpleName();

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;

    private ImageView imageSave;
    private String selectedNoteColor;
    private LinearLayout layoutMiscellaneous;


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
        layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm  a");
        String dateTime = simpleDateFormat.format(Calendar.getInstance().getTime());

        textDateTime.setText(
                new SimpleDateFormat("E, dd MMM yyyy hh:mm a", Locale.getDefault()).format(new Date())
        );

        ImageView imageBack = findViewById(R.id.imageBack);
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

        selectedNoteColor = "#333333";

//        initMiscellaneous();
//        setViewSubtitleIndicatorColor();

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

        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    private void initMiscellaneous(){
        onClickBehaviourForMiscellaneous();

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

    }

    private void onClickBehaviourForMiscellaneous() {
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
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

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        // TODO : One function to manage select color functionality

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void setViewSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }


}