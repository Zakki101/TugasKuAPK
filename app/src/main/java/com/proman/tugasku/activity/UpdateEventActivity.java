package com.proman.tugasku.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proman.tugasku.R;
import com.proman.tugasku.db.HelperKalender;
import com.proman.tugasku.db.HelperTugas;
import com.proman.tugasku.model.Kalender;
import com.proman.tugasku.model.Tugas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateEventActivity extends AppCompatActivity {
    private HelperKalender DbHelper;
    private EditText etJudulKegiatan, etRincianKegiatan, etTglMulai, etTglAkhir;
    private Button btnUpdate, btnHapus;
    private Kalender kalender;
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);
        DbHelper = new HelperKalender(this);
        initializeViews();
        loadTaskData();
        setupDateTimePickers();
        setupButtonListeners();

    }
    private void initializeViews() {
        etJudulKegiatan = findViewById(R.id.edt_Judul_kegiatan);
        etRincianKegiatan = findViewById(R.id.edt_rincian_kegiatan);
        etTglMulai = findViewById(R.id.edt_acara_mulai);
        etTglAkhir = findViewById(R.id.edt_acara_selesai);
        btnUpdate = findViewById(R.id.btn_update_event);
        btnHapus = findViewById(R.id.btn_delete_event);
    }

    private void loadTaskData() {
        kalender = (Kalender) getIntent().getSerializableExtra("event");
        if (kalender != null) {
            etJudulKegiatan.setText(kalender.getJudul_acara());
            etRincianKegiatan.setText(kalender.getRincian_acara());
            if (kalender.getWaktuMulaiAcara() != null) {
                etTglMulai.setText(kalender.getWaktuMulaiAcara().format(displayFormatter));
            }

            if (kalender.getWaktuSelesaiAcara() != null) {
                etTglAkhir.setText(kalender.getWaktuSelesaiAcara().format(displayFormatter));
            }
        }
    }

    private void setupButtonListeners() {
        btnUpdate.setOnClickListener(v -> updateTask());

        btnHapus.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Hapus Event")
                .setMessage("Yakin ingin menghapus Event ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteTask())
                .setNegativeButton("Tidak", null)
                .show());
    }
    private void setupDateTimePickers() {
        // Date picker for end date
        etTglMulai.setOnClickListener(v -> showDateTimePicker(etTglMulai));
        etTglAkhir.setOnClickListener(v -> showDateTimePicker(etTglAkhir));
    }

    private void showDateTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, selectedHour, selectedMinute) -> {
                                LocalDateTime ldt = LocalDateTime.of(
                                        selectedYear, selectedMonth + 1, selectedDay,
                                        selectedHour, selectedMinute);
                                editText.setText(ldt.format(displayFormatter));
                            },
                            hour, minute, true
                    );
                    timePickerDialog.show();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void updateTask() {
        // Validate inputs
        if (etJudulKegiatan.getText().toString().trim().isEmpty()) {
            etJudulKegiatan.setError("Judul tidak boleh kosong");
            return;
        }

        try {
            LocalDateTime tglMulai = LocalDateTime.parse(etTglMulai.getText().toString(),displayFormatter);
            LocalDateTime tglAkhir = LocalDateTime.parse(etTglAkhir.getText().toString(),displayFormatter);

            // Update task object
            kalender.setJudul_acara(etJudulKegiatan.getText().toString());
            kalender.setRincian_acara(etRincianKegiatan.getText().toString());
            kalender.setWaktuMulaiAcara(tglMulai);
            kalender.setWaktuSelesaiAcara(tglAkhir);

            // Update in database
            DbHelper.updateEvent(kalender);
            Toast.makeText(this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            if (etTglMulai.getText().toString().isEmpty()) {
                etTglMulai.setError("Tanggal akhir harus diisi");
            } else {
                etTglMulai.setError("Format tanggal salah (dd/MM/yyyy)");
            }

            if (etTglAkhir.getText().toString().isEmpty()) {
                etTglAkhir.setError("Tanggal akhir harus diisi");
            } else {
                etTglAkhir.setError("Format tanggal salah (dd/MM/yyyy)");
            }
        }
    }

    private void deleteTask() {
        DbHelper.deleteEvent(kalender.getId_acara());
        Toast.makeText(this, "Event berhasil dihapus", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}


