package com.example.notepad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.data.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private Context context;
    private NoteFilter filter;
    public NoteFilter getFilter() {
        return filter;
    }

    public NoteListAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        filter = new NoteFilter(noteList, this);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_view, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteTextView.setText(note.getTitle() + "\n" + note.getNote());
        // Set any other views that display data from the Note object
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView noteTextView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.note);
            // Set any other views that display data from the Note object
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Note note = noteList.get(position);
            Intent intent = new Intent(context, NotesActivity.class);
            intent.putExtra("noteId",note.getId());
            context.startActivity(intent);
        }
    }

    public void setNotes(List<Note> notes) {
        this.noteList = notes;
        notifyDataSetChanged();
    }


    private class NoteFilter extends Filter {
        private final List<Note> originalNotes;
        private final List<Note> filteredNotes;
        private final NoteListAdapter adapter;

        private NoteFilter(List<Note> notes, NoteListAdapter adapter) {
            originalNotes = new ArrayList<>(notes);
            filteredNotes = new ArrayList<>();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredNotes.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredNotes.addAll(originalNotes);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final Note note : originalNotes) {
                    if (note.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredNotes.add(note);
                    }
                }
            }

            results.values = filteredNotes;
            results.count = filteredNotes.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.setNotes((List<Note>) results.values);
        }
    }

}
