package com.example.notesapp.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.example.notesapp.data.model.Note;
import com.example.notesapp.listener.NotesListener;
import com.example.notesapp.utils.Constants;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> mNoteList;
    public Context context;
    NotesListener notesListener;

    public NotesAdapter(List<Note> notes, Context context, NotesListener notesListener) {
        this.mNoteList = notes;
        this.context = context;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_container_note, parent, false),
                context
        );
    }

    public void setNoteList(List<Note> noteList) {
        this.mNoteList = noteList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(mNoteList.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.onNoteClick(mNoteList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mNoteList == null) return 0;
        return mNoteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        Context context;
        RoundedImageView imageNote;

        NoteViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            this.context = context;
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note) {
            textTitle.setText(note.getTitle());
            textSubtitle.setText(note.getSubtitle());
            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            }
            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            String selectedNoteColor = note.getColor();
            if (selectedNoteColor != null) {
                gradientDrawable.setColor(context.getResources().getColor(Constants.NOTE_COLOR[Integer.parseInt(selectedNoteColor) - 1]));
            }

            if (note.getImagePath() != null) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            } else {
                imageNote.setVisibility(View.GONE);
            }
        }
    }

}
