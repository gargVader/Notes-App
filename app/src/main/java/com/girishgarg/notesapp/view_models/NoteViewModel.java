package com.girishgarg.notesapp.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.girishgarg.notesapp.data.model.Note;
import com.girishgarg.notesapp.data.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private LiveData<List<Note>> allNotes;
    private NoteRepository noteRepository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        allNotes = noteRepository.getAllNotes();
    }

    public void insert(Note note) {
        noteRepository.insert(note);
    }

    public void update(Note note) {
        noteRepository.update(note);
    }

    public void delete(Note note) {
        noteRepository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }


    public LiveData<List<Note>> getSearchNotes(String searchKeyword) {
        return noteRepository.getSearchNotes(searchKeyword);
    }


}
