package com.example.notesapp.listener;

import com.example.notesapp.data.model.Note;

public interface NotesListener {

    void onNoteClick(Note note, int position);

}
