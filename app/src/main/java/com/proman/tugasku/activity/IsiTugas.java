package com.proman.tugasku.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.proman.tugasku.R;
import com.proman.tugasku.db.HelperTugas;
import com.proman.tugasku.model.Tugas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

public class IsiTugas extends AppCompatActivity {

    private HelperTugas dbHelper;
    private EditText etJudul, etRincian, etTglAkhir;
    private RadioGroup rgStatus;
    private RadioButton rbBelumSelesai;
    private Button btnTambah;

    private static final DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_tugas);
        initializeViews();
        setupDateTimePickers();
        setupButtonListener();
    }

    private void initializeViews() {
        dbHelper = new HelperTugas(this);

        etJudul = findViewById(R.id.edt_Judul);
        etRincian = findViewById(R.id.edt_rincian);
        etTglAkhir = findViewById(R.id.edt_deadline);

        rgStatus = findViewById(R.id.radio_status);
        rbBelumSelesai = findViewById(R.id.radio_belum);

        btnTambah = findViewById(R.id.btn_tambah);

        // Set default status
        rbBelumSelesai.setChecked(true);
    }

    private void setupDateTimePickers() {
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

    //Setup button di isi tugas
    private void setupButtonListener() {
        btnTambah.setOnClickListener(v -> {
            if (validateInput()) {
                try {
                    String judul = etJudul.getText().toString().trim();
                    String rincian = etRincian.getText().toString().trim();
                    LocalDateTime tglAkhir = LocalDateTime.parse(
                            etTglAkhir.getText().toString(), displayFormatter
                    );
                    boolean isSelesai = rgStatus.getCheckedRadioButtonId() == R.id.radio_selesai;

                    Tugas tugas = new Tugas(judul, rincian, tglAkhir);
                    tugas.setSelesai(isSelesai);

                    long result = dbHelper.addTugas(tugas);

                    if (result != -1) {
                        showSuccessAndFinish();
                    } else {
                        Toast.makeText(this, "Gagal menambahkan tugas", Toast.LENGTH_SHORT).show();
                    }
                } catch (DateTimeParseException e) {
                    Toast.makeText(this, "Format tanggal salah (gunakan dd/MM/yyyy HH:mm)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Validasi Input
    private boolean validateInput() {
        boolean isValid = true;

        if (etJudul.getText().toString().trim().isEmpty()) {
            etJudul.setError("Judul tidak boleh kosong");
            isValid = false;
        }

        if (etTglAkhir.getText().toString().isEmpty()) {
            etTglAkhir.setError("Deadline tidak boleh kosong");
            isValid = false;
        } else {
            try {
                LocalDateTime.parse(etTglAkhir.getText().toString(), displayFormatter);
            } catch (DateTimeParseException e) {
                etTglAkhir.setError("Format tanggal salah (dd/MM/yyyy HH:mm)");
                isValid = false;
            }
        }

        return isValid;
    }

    private void showSuccessAndFinish() {
        Toast.makeText(this, "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
