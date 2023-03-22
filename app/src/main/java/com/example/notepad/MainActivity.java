package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.notepad.data.model.Note;
import com.example.notepad.data.repository.INotesRepository;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView noteListView;
    private SearchView searchView;
    private ImageView takeAudioNoteView;
    private TextView takeNoteView;

    private INotesRepository notesRepo;
    private ArrayList<Note> noteList;
    private NoteListAdapter noteListAdapter;

    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private ToggleButton viewSwitchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteListView = findViewById(R.id.noteListView);
        noteListView.setLayoutManager(new LinearLayoutManager(this)); // set default layout manager to linear

        takeNoteView = findViewById(R.id.takeNote);

        takeNoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTakeNoteActivity(false);
            }
        });

        // Add audio note functionality to the app
        takeAudioNoteView = findViewById(R.id.takeAudioNoteView);
        takeAudioNoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("I have been clicked.");
                startTakeNoteActivity(true);
            }
        });

        notesRepo = ((App)getApplication()).getNotesRepository();
        populateNotes();

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (noteListAdapter != null) {
                    Filter filter = noteListAdapter.getFilter();
                    filter.filter(query);
                    noteListAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter list based on search query
                if (noteListAdapter != null) {
                    Filter filter = noteListAdapter.getFilter();
                    filter.filter(newText);
                    noteListAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });


        viewSwitchButton = findViewById(R.id.toggleButton);
        viewSwitchButton.setBackgroundResource(R.drawable.ic_grid);

        // Initialize LinearLayoutManager and GridLayoutManager
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2); // change the 2 to adjust the number of columns

        // Set the default layout
        noteListView.setLayoutManager(linearLayoutManager);

        // Set the onClickListener for the view switch button
        viewSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle between the two layouts
                if (noteListView.getLayoutManager() == linearLayoutManager) {
                    noteListView.setLayoutManager(gridLayoutManager);
                    viewSwitchButton.setBackgroundResource(R.drawable.ic_list);
                } else {
                    noteListView.setLayoutManager(linearLayoutManager);
                    viewSwitchButton.setBackgroundResource(R.drawable.ic_grid);
                }
            }
        });

    }

    private void populateNotes(){

        noteList = (ArrayList<Note>) notesRepo.getNotesList();

        // Initialize NoteAdapter with the note list and the current layout manager
        noteListAdapter = new NoteListAdapter(this, noteList);

        noteListView.setAdapter(noteListAdapter);
        noteListAdapter.notifyDataSetChanged();
        noteListView.setPadding(8,8,8,8);
    }

    private void startTakeNoteActivity(boolean isSpeechButtonClicked){
        Intent intent = new Intent(this, NotesActivity.class);
        intent.putExtra("isSpeechButtonClicked", isSpeechButtonClicked);
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