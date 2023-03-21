package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.notepad.data.model.Note;
import com.example.notepad.data.repository.INotesRepository;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private SearchView searchView;
    private INotesRepository notesRepo;

    private ArrayList<Note> noteList;
    private ArrayAdapter<Note> noteArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteListView = findViewById(R.id.noteListView);
        TextView takeNoteView = findViewById(R.id.takeNote);

        searchView = findViewById(R.id.searchView);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (noteArrayAdapter != null) {
                    noteArrayAdapter.getFilter().filter(query);
                    noteArrayAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter list based on search query
                if (noteArrayAdapter != null) {
                    noteArrayAdapter.getFilter().filter(newText);
                    noteArrayAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    private void populateNotes(){

        noteList = (ArrayList<Note>) notesRepo.getNotesList();

        noteArrayAdapter = new ArrayAdapter<>(
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