package com.example.timerstopwatch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private Button startButton, stopButton, resetButton;
    private long pauseOffset;
    private boolean running;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = findViewById(R.id.chronometer);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);

        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometer();

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopChronometer();
                saveOccurrence("Stopwatch");
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChronometer();
            }
        });

        ListView listView = findViewById(R.id.listView);
        cursorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[]{DatabaseHelper.COLUMN_TYPE, DatabaseHelper.COLUMN_TIME},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );
        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text2) {
                    long timeInMillis = cursor.getLong(columnIndex);
                    String formattedTime = formatChronometerTime(timeInMillis);
                    ((TextView) view).setText(formattedTime);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(cursorAdapter);

        loadOccurrences();
    }

    private void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    private void stopChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    private void resetChronometer() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        if (!running) {
            chronometer.stop();
        }
    }

    private void saveOccurrence(String type) {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TYPE, type);
        values.put(DatabaseHelper.COLUMN_TIME, elapsedMillis);
        database.insert(DatabaseHelper.TABLE_NAME, null, values);

        loadOccurrences();
    }

    private void loadOccurrences() {
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_TIMESTAMP + " DESC"
        );
        cursorAdapter.swapCursor(cursor);
    }

    private String formatChronometerTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis % 3600000) / 60000;
        int seconds = (int) ((elapsedMillis % 3600000) % 60000) / 1000;
        int milliseconds = (int) ((elapsedMillis % 3600000) % 60000) % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
