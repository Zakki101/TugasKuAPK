package com.proman.tugasku.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.proman.tugasku.R;

public class SettingActivity extends AppCompatActivity {
    private ImageButton btntugas, btnkalender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initializeViews();
        setupButtonListeners();
    }
    private void initializeViews() {
        btntugas = findViewById(R.id.btntask);
        btnkalender = findViewById(R.id.btnkalender);
    }
    private void setupButtonListeners() {

        btntugas.setOnClickListener(v -> {
            Intent kalender = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(kalender);
            finish();
        });

        btnkalender.setOnClickListener(v -> {
            Intent prof = new Intent(SettingActivity.this, KalenderActivity.class);
            startActivity(prof);
            finish();
        });
    }
}