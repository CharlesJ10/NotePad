package com.example.notepad.data.repository;

import com.example.notepad.data.model.Note;

import java.util.List;

public interface INotesRepository {
    List<Note> getNotesList();
    void addNote(Note note);
    void updateNote(Note note);
    Note getNoteById(String id);
    boolean deleteNoteById(String id);
}
