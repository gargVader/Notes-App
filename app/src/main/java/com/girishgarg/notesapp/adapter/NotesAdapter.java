package com.girishgarg.notesapp.adapter;

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
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.R;
import com.girishgarg.notesapp.data.model.Note;
import com.girishgarg.notesapp.utils.Constants;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends ListAdapter<Note, NotesAdapter.NoteViewHolder> {

    OnItemClickListener onItemClickListener;
    public Context context;
    Timer timer;
    List<Note> notesSource;

    public NotesAdapter(Context context) {
        super(diffCallback);
        this.context = context;
    }

    public static final DiffUtil.ItemCallback<Note> diffCallback = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false);
        return new NoteViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(getItem(position));
    }

    protected class NoteViewHolder extends RecyclerView.ViewHolder {

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION)
                        onItemClickListener.onItemClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION)
                        onItemClickListener.onItemLongCLick(getItem(position));
                    return true;
                }
            });


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

    public interface OnItemClickListener {
        public void onItemClick(Note note);

        public void onItemLongCLick(Note note);

    }

    public void setOnItemCLickListener(OnItemClickListener onItemCLickListener) {
        this.onItemClickListener = onItemCLickListener;
    }

    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {

                } else {

                }
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) timer.cancel();
    }


}
