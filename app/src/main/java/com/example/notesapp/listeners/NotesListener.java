package com.example.notesapp.listeners;

import com.example.notesapp.entities.Note;

public interface NotesListener {

    void onNoteClick(Note note, int position);

}
