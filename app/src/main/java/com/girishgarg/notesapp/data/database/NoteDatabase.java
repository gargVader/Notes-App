package com.girishgarg.notesapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.girishgarg.notesapp.data.dao.NoteDao;
import com.girishgarg.notesapp.data.model.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;
    public abstract NoteDao getNoteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if (instance ==null){
            instance = Room.databaseBuilder(context, NoteDatabase.class, "notes_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }



}
