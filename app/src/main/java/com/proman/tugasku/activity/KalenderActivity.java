package com.proman.tugasku.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proman.tugasku.R;
import com.proman.tugasku.adapter.KalenderAdapter;
import com.proman.tugasku.adapter.TugasAdapter;
import com.proman.tugasku.db.HelperKalender;
import com.proman.tugasku.db.HelperTugas;
import com.proman.tugasku.model.Kalender;
import com.proman.tugasku.model.Tugas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class KalenderActivity extends AppCompatActivity {
    private ImageButton btntugas, btnsetting, btntambahevent;
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private KalenderAdapter Kadapter;
    private ArrayList<Kalender> EventArrayList;
    private HelperKalender dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalender);

        initializeViews();
        setupButtonListeners();
        setupCalendar();
        setupRecyclerView();
    }

    private void initializeViews() {
        btntambahevent = findViewById(R.id.btnaddkegiatan);
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
    private void setupRecyclerView() {
        Kadapter = new KalenderAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(Kadapter);
    }
    private void loadDataEvent() {
        dbHelper = new HelperKalender(this);
        EventArrayList = dbHelper.getAllEvent();
        Kadapter.setListEvent(EventArrayList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        EventArrayList = dbHelper.getAllEvent();
        Kadapter.setListEvent(EventArrayList);
    }

    private void setupButtonListeners() {
        btntambahevent.setOnClickListener(v -> {
            Intent tmbhevent = new Intent(KalenderActivity.this, IsiTugas.class);
            startActivity(tmbhevent);
        });

        btntugas.setOnClickListener(v -> {
            Intent tugasact = new Intent(KalenderActivity.this, MainActivity.class);
            startActivity(tugasact);
        });

        btnsetting.setOnClickListener(v -> {
            Intent settingact = new Intent(KalenderActivity.this, SettingActivity.class);
            startActivity(settingact);
        });
    }
}