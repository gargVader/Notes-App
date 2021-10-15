package com.girishgarg.notesapp.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.girishgarg.notesapp.data.dao.NoteDao;
import com.girishgarg.notesapp.data.database.NoteDatabase;
import com.girishgarg.notesapp.data.model.Note;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        noteDao = NoteDatabase.getInstance(application).getNoteDao();
        allNotes = noteDao.getAllNotes();
    }

    public void insert(Note note) {
        new InsertAsyncTask().execute(note);
    }

    public void update(Note note) {
        new UpdateAsyncTask().execute(note);
    }

    public void delete(Note note) {
        new DeleteAsyncTask().execute(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getSearchNotes(String searchKeyword) {
        return noteDao.getSearchNotes(searchKeyword);
    }



    private class InsertAsyncTask extends AsyncTask<Note, Void, Void> {
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private class DeleteAsyncTask extends AsyncTask<Note, Void, Void> {
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }


}
