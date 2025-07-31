package com.proman.tugasku.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.proman.tugasku.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class KalenderActivity extends AppCompatActivity {
    private ImageButton btntugas, btnsetting;
    private CalendarView calendarView;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalender);

        initializeViews();
        setupButtonListeners();
        setupCalendar();
    }

    private void initializeViews() {
        btntugas = findViewById(R.id.btntask);
        btnsetting = findViewById(R.id.btnsetting);
        calendarView = findViewById(R.id.kalenderview);

    }

    private void setupCalendar() {
        calendar = Calendar.getInstance();

        calendarView.setDate(calendar.getTimeInMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = dateFormat.format(calendar.getTime());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });
    }

    private void setupButtonListeners() {
        if (btntugas != null) {
            btntugas.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
            });
        }

        if (btnsetting != null) {
            btnsetting.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingActivity.class));
            });
        }
    }
}