package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.notepad.data.model.Note;
import com.example.notepad.data.repository.INotesRepository;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private INotesRepository notesRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteListView = findViewById(R.id.noteListView);
        TextView takeNoteView = findViewById(R.id.takeNote);

        // Add audio note functionality to the app
        // ImageView takeAudioNoteView = findViewById(R.id.takeAudioNoteView);

        notesRepo = ((App)getApplication()).getNotesRepository();

        takeNoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTakeNoteActivity();
            }
        });

        populateNotes();
    }

    private void populateNotes(){

        final ArrayList<Note> noteList = (ArrayList<Note>) notesRepo.getNotesList();

        final ArrayAdapter<Note> noteArrayAdapter = new ArrayAdapter<>(
                this, R.layout.note_card_view, R.id.note, noteList);

        noteArrayAdapter.notifyDataSetChanged();

        noteListView.setAdapter(noteArrayAdapter);
        noteListView.setPadding(8,8,8,8);
        noteListView.setDividerHeight(8);

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = (Note)parent.getAdapter().getItem(position);
                startTakeNoteActivity(note);
            }
        });
    }

    private void startTakeNoteActivity(){
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }

    private void startTakeNoteActivity(Note note) {
        Intent intent = new Intent(this, NotesActivity.class);
        intent.putExtra("noteId",note.getId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateNotes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((App)getApplication()).saveNotes();
    }
}