package com.example.notepad;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notepad.data.model.Note;
import com.example.notepad.data.repository.INotesRepository;

import java.util.ArrayList;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {

    private EditText titleView;
    private EditText noteView;
    private Button saveButton;
    private Button deleteButton;
    private INotesRepository notesRepo;
    private String noteId;

    private SpeechRecognizer speechRecognizer;
    private boolean isSpeechButtonClicked;
    private static final String TAG = "NotesActivity";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private ImageView speechButtonView;



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

        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        speechButtonView = findViewById(R.id.mic_icon);

        saveButton.setText(R.string.add_note);
        deleteButton.setText("Delete");

        // Update Button
        if(noteId!=null){
            saveButton.setText(R.string.update_note);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
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

        speechButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAudioPermissions();
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

        // Create a SpeechRecognizer object
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.d(TAG, "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // RMS dB value has changed
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Speech input buffer has been received
            }

            @Override
            public void onEndOfSpeech() {
                // Speech input has ended
                speechRecognizer.stopListening();
            }

            @Override
            public void onError(int error) {
                // Speech recognition error occurred
            }

            @Override
            public void onResults(Bundle results) {
                // Speech recognition results have been received
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println("Matches: " + matches.size());

                // Get the position of the cursor within the note text
                int cursorPosition = noteView.getSelectionStart();
                System.out.println("Cursor: " + cursorPosition);

                // Get the current text of the note
                Editable noteText = noteView.getText();

                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0);
                    System.out.println("Spoken Text: " + spokenText);

                    // Insert the speech text at the cursor position
                    noteText.insert(cursorPosition, spokenText);

                    // Set the modified note text in the EditText view
                    noteView.setText(noteText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Partial speech recognition results have been received
            }

            @Override
            public void onSegmentResults(@NonNull Bundle segmentResults) {
                RecognitionListener.super.onSegmentResults(segmentResults);
            }

            @Override
            public void onEndOfSegmentedSession() {
                RecognitionListener.super.onEndOfSegmentedSession();
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Speech recognizer event occurred
            }

        });

        // Retrieve the extra boolean value from the intent
        isSpeechButtonClicked = getIntent().getBooleanExtra("isSpeechButtonClicked", false);

        if (isSpeechButtonClicked) {
            requestAudioPermissions();
        }

    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            startSpeechRecognition();
        }
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, start the speech recognition
                    startSpeechRecognition();
                } else {
                    // Permission denied, show a message to the user and disable speech recognition
                    Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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

    private void startSpeechRecognition() {
        // Create an intent to start speech recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

        // Start listening for the user's speech
        speechRecognizer.startListening(intent);
    }
}