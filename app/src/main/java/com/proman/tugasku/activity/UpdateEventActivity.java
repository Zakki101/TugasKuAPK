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
        // Ambil input
        String judul = etJudulKegiatan.getText().toString().trim();
        String rincian = etRincianKegiatan.getText().toString().trim();
        String sMulai = etTglMulai.getText().toString().trim();
        String sAkhir = etTglAkhir.getText().toString().trim();

        // Validasi judul
        if (judul.isEmpty()) {
            etJudulKegiatan.setError("Judul tidak boleh kosong");
            return;
        }

        // Validasi rincian
        if (rincian.isEmpty()) {
            etRincianKegiatan.setError("Rincian tidak boleh kosong");
            return;
        }

        LocalDateTime tglMulai;
        LocalDateTime tglAkhir;

        // Validasi parsing tanggal
        try {
            if (sMulai.isEmpty()) {
                etTglMulai.setError("Tanggal mulai harus diisi");
                Toast.makeText(this, "Isi Tanggal Mulai Acara", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tglMulai = LocalDateTime.parse(sMulai, displayFormatter);
            }

            if (sAkhir.isEmpty()) {
                etTglAkhir.setError("Tanggal akhir harus diisi");
                Toast.makeText(this, "Isi Tanggal Selesai Acara", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tglAkhir = LocalDateTime.parse(sAkhir, displayFormatter);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Format tanggal salah (dd/MM/yyyy HH:mm)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi urutan tanggal
        if (!tglMulai.isBefore(tglAkhir)) {
            Toast.makeText(this, "Terdapat Kesalahan Pada Penganggalan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update task object
        kalender.setJudul_acara(judul);
        kalender.setRincian_acara(rincian);
        kalender.setWaktuMulaiAcara(tglMulai);
        kalender.setWaktuSelesaiAcara(tglAkhir);

        // Update ke database
        DbHelper.updateEvent(kalender);
        Toast.makeText(this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteTask() {
        DbHelper.deleteEvent(kalender.getId_acara());
        Toast.makeText(this, "Event berhasil dihapus", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}


