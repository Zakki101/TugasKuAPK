package com.proman.tugasku.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

public class IsiEvent extends AppCompatActivity {

    private HelperKalender dbHelper;
    private EditText etJudulKegiatan, etRincianKegiatan, etTglMulai, etTglAkhir;
    private Button btnTambah;
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_event);
        initializeViews();
        setupDateTimePickers();
        setupButtonListener();
    }

    private void initializeViews() {
        dbHelper = new HelperKalender(this);

        etJudulKegiatan = findViewById(R.id.edt_Judul_kegiatan);
        etRincianKegiatan = findViewById(R.id.edt_rincian_kegiatan);
        etTglMulai = findViewById(R.id.edt_acara_mulai);
        etTglAkhir = findViewById(R.id.edt_acara_selesai);

        btnTambah = findViewById(R.id.btn_tambah_event);
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
                                // Gabungkan tanggal & waktu
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
    private void setupButtonListener() {
        btnTambah.setOnClickListener(v -> {
            if (validateInput()) {
                try {
                    // Parse input values
                    String judul = etJudulKegiatan.getText().toString().trim();
                    String rincian = etRincianKegiatan.getText().toString().trim();
                    LocalDateTime tglMulai = LocalDateTime.parse(etTglMulai.getText().toString(),displayFormatter);
                    LocalDateTime tglAkhir = LocalDateTime.parse(etTglAkhir.getText().toString(),displayFormatter);


                    // Create new event
                    Kalender kalender = new Kalender(0, judul, rincian, tglMulai, tglAkhir);

                    // Add to database
                    long result = dbHelper.addEvent(kalender);

                    if (result != -1) {
                        showSuccessAndFinish();
                    } else {
                        Toast.makeText(this, "Gagal menambahkan event", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Format tanggal salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (etJudulKegiatan.getText().toString().trim().isEmpty()) {
            etJudulKegiatan.setError("Judul tidak boleh kosong");
            isValid = false;
        }

        if (etRincianKegiatan.getText().toString().trim().isEmpty()) {
            etRincianKegiatan.setError("Rincian tidak boleh kosong");
            isValid = false;
        }

        if (etTglMulai.getText().toString().isEmpty()) {
            etTglMulai.setError("Waktu mulai acara tidak boleh kosong");
            isValid = false;
        } else {
            try {
                LocalDateTime.parse(etTglMulai.getText().toString(), displayFormatter);
            } catch (Exception e) {
                etTglMulai.setError("Format tanggal salah");
                isValid = false;
            }
        }

        if (etTglAkhir.getText().toString().isEmpty()) {
            etTglAkhir.setError("Waktu selesai acara tidak boleh kosong");
            isValid = false;
        } else {
            try {
                LocalDateTime.parse(etTglAkhir.getText().toString(), displayFormatter);
            } catch (Exception e) {
                etTglAkhir.setError("Format tanggal salah");
                isValid = false;
            }
        }

        return isValid;
    }

    private void showSuccessAndFinish() {
        Toast.makeText(this, "Event berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}