package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.notepad.data.model.Note;
import com.example.notepad.data.repository.INotesRepository;

public class NotesActivity extends AppCompatActivity {

    private EditText titleView;
    private EditText noteView;
    private Button imageButton;
    private Button deleteButton;
    private INotesRepository notesRepo;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            noteId = bundle.getString("noteId");
        }

        notesRepo = ((App)getApplication()).getNotesRepository();

        titleView = findViewById(R.id.noteTitle);
        noteView = findViewById(R.id.note);

        imageButton = findViewById(R.id.imageButton);
        deleteButton = findViewById(R.id.deleteButton);

        imageButton.setText(R.string.add_note);
        deleteButton.setText("Delete");

        // Update Button
        if(noteId!=null){
            imageButton.setText(R.string.update_note);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
                startMainActivity();
                return;
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();
                startMainActivity();
                return;
            }
        });

        // Update Note Info in View
        if(noteId!=null){
            Note note = notesRepo.getNoteById(noteId);
            titleView.setText(note.getTitle());
            noteView.setText(note.getNote());
            return;
        }

      noteView.requestFocus();

    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addNote(){
        if(noteId!=null){
            updateNote();
            return;
        }
        String titleValue = titleView.getText().toString();
        String notesValue = noteView.getText().toString();

        if(titleValue.isEmpty() && notesValue.isEmpty()) return;

        Note note = new Note(titleValue,notesValue);
        notesRepo.addNote(note);
    }

    private void updateNote(){
        String titleValue = titleView.getText().toString();
        String notesValue = noteView.getText().toString();
        Note note = notesRepo.getNoteById(noteId);
        note.update(titleValue,notesValue);
    }

    private void deleteNote() {
        if(noteId == null){
            return;
        }
        String titleValue = titleView.getText().toString();
        String notesValue = noteView.getText().toString();

        if(titleValue.isEmpty() && notesValue.isEmpty()) return;

        if (notesRepo.deleteNoteById(noteId)) {
            System.out.println("Was deleted");
        } else {
            System.out.println("Not deleted");
        }
    }

    @Override
    public void onBackPressed() {
        addNote();
        super.onBackPressed();
    }
}