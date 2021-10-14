package com.example.notesapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notesapp.data.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Note> noteList);

    @Query("DELETE FROM note_table")
    void deleteAll();

}
