package com.comp3710.assignment3;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText input;
    Button add, search, clear;
    ScrollView scrollNotes;
    LinearLayout notesView;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    ArrayList<String> notes;
    int noteCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteCount = 0;
        notes = new ArrayList<>();
        input = (EditText) findViewById(R.id.inputEditText);
        add = (Button) findViewById(R.id.addButton);
        search = (Button) findViewById(R.id.searchButton);
        clear = (Button) findViewById(R.id.clearButton);
        scrollNotes = (ScrollView) findViewById(R.id.scrollView);
        notesView = (LinearLayout) findViewById(R.id.notesLayout);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        restoreNotes();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.getText().clear();
                showAllNotes();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (missingText()) {
                    Toast.makeText(MainActivity.this, "Error: Blank note!", Toast.LENGTH_SHORT).show();
                }

                else {
                    final View view = View.inflate(MainActivity.this, R.layout.notes, null);
                    final EditText note = view.findViewById(R.id.noteEditText);
                    note.setText(input.getText().toString().trim());
                    deleteNote(view, note); // Delete onClick Listener

                    notesView.addView(view);
                    notes.add(note.getText().toString());
                    noteCount = notes.size();

                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (missingText()) {
                    showAllNotes();
                }

                else {
                    notesView.removeAllViews();
                    for (String s : notes) {
                        if (s.toUpperCase().contains(input.getText().toString().trim().toUpperCase())) {
                            final View view = View.inflate(MainActivity.this, R.layout.notes, null);
                            final EditText note = view.findViewById(R.id.noteEditText);
                            note.setText(s);
                            deleteNote(view, note);
                            notesView.addView(view);
                        }
                    }
                }
            }
        });


    }

    // Saves all notes stored in array list into unique strings in shared preferences. Updates and saves count of notes so notes aren't overwritten.
    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < notes.size(); i++) {
            editor.putString("Note" + i, notes.get(i));
        }
        editor.putInt("Note Count", noteCount);
        editor.apply();
    }

    // Shows all notes when input field is blank or cleared
    private void showAllNotes() {
        notesView.removeAllViews();
        for (String n : notes) {
            final View view = View.inflate(MainActivity.this, R.layout.notes, null);
            final EditText note = view.findViewById(R.id.noteEditText);
            note.setText(n);
            deleteNote(view, note);
            notesView.addView(view);
        }
    }

    // Method for reloading saved notes upon app relaunch
    private void restoreNotes() {
        noteCount = preferences.getInt("Note Count", 0);
        for (int i = 0; i < noteCount; i++) {
            final View view = View.inflate(MainActivity.this, R.layout.notes, null);
            final EditText note = view.findViewById(R.id.noteEditText);
            note.setText(preferences.getString("Note" + i, "Default"));
            deleteNote(view, note); // Delete onClick Listener

            notesView.addView(view);
            notes.add(note.getText().toString());
        }
        //Toast.makeText(MainActivity.this, noteCount + "", Toast.LENGTH_SHORT).show();

    }

    // Check for missing text in the new note/search field
    private boolean missingText() {
        return input.getText().toString().trim().isEmpty();
    }

    // Adds delete button to each new note created. Updates list of available notes and shared preferences for reloading notes
    private void deleteNote(final View view, final EditText note) {
        Button delete = (Button) view.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesView.removeView(view);
                for (String n : notes) {
                    if (n.contains(note.getText().toString())) {
                        notes.remove(n);
                        noteCount = notes.size();
                        editor.putInt("Note Count", noteCount);
                        return; // exits loop once note is found and deleted
                    }
                }
            }
        });
    }
}
