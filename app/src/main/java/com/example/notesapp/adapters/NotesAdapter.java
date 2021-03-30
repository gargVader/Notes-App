package com.example.notesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.entities.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{

    private List<Note> notes;

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_container_note, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        if(notes==null) return 0;
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle, textSubtitle, textDateTime;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            textSubtitle.setText(note.getSubtitle());
            if(note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }
            textDateTime.setText(note.getDateTime());
        }
    }

}
