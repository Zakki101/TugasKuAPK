package com.proman.tugasku.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.EventLog;

import androidx.annotation.Nullable;

import com.proman.tugasku.model.Kalender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HelperKalender extends SQLiteOpenHelper{
        public static final String DATABASE_NAME = "dbkalender";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_EVENT = "event";

        // Column names
        private static final String KEY_ID_EVENT = "id_event";
        private static final String KEY_JUDUL_EVENT = "judul_event";
        private static final String KEY_RINCIAN_EVENT = "rincian";
        private static final String KEY_WAKTU_MULAI = "tanggal_event_mulai";
        private static final String KEY_WAKTU_SELESAI = "tanggal_event_selesai";
        // Date format for database storage
        private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
        private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        // SQL to create table
        private static final String CREATE_TABLE_EVENT = "CREATE TABLE "
                + TABLE_EVENT + "("
                + KEY_ID_EVENT + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_JUDUL_EVENT + " TEXT NOT NULL,"
                + KEY_RINCIAN_EVENT + " TEXT,"
                + KEY_WAKTU_MULAI + " TEXT NOT NULL,"
                + KEY_WAKTU_SELESAI + " TEXT NOT NULL)";

        public HelperKalender(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

    public HelperKalender(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_EVENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
            onCreate(db);
        }

        // Add a new task
        public long addEvent(Kalender event) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_JUDUL_EVENT, event.getJudul_acara());
            values.put(KEY_RINCIAN_EVENT, event.getRincian_acara());
            values.put(KEY_WAKTU_MULAI, dateFormat.format(event.getWaktuMulaiAcara()));
            values.put(KEY_WAKTU_SELESAI, dateFormat.format(event.getWaktuSelesaiAcara()));

            long id = db.insert(TABLE_EVENT, null, values);
            db.close();

            return id;
        }

        // Get all event
        @SuppressLint("Range")
        public ArrayList<Kalender> getAllEvent() {
            ArrayList<Kalender> eventList = new ArrayList<>();
            String selectQuery = "SELECT * FROM " + TABLE_EVENT + " ORDER BY " + KEY_WAKTU_MULAI + " ASC";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Kalender event = new Kalender();
                    event.setId_acara(cursor.getInt(cursor.getColumnIndex(KEY_ID_EVENT)));
                    event.setJudul_acara(cursor.getString(cursor.getColumnIndex(KEY_JUDUL_EVENT)));
                    event.setRincian_acara(cursor.getString(cursor.getColumnIndex(KEY_RINCIAN_EVENT)));

                    try {
                        String tglmulaievent = cursor.getString(cursor.getColumnIndex(KEY_WAKTU_MULAI));
                        LocalDateTime tglmulai = LocalDateTime.parse(tglmulaievent);
                        event.setWaktuMulaiAcara(tglmulai);

                        String tglselesaievent = cursor.getString(cursor.getColumnIndex(KEY_WAKTU_SELESAI));
                        LocalDateTime tglselesai = LocalDateTime.parse(tglselesaievent);
                        event.setWaktuSelesaiAcara(tglselesai);
                        eventList.add(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return eventList;
        }
        /*
        // Update a task
        public int updateKalender(Kalender kalender) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_JUDUL_EVENT, event.getJudul());
            values.put(KEY_RINCIAN_EVENT, event.getRincian());
            values.put(KEY_WAKTU_MULAI, LocalDateTime.format(tugas.getTanggalAkhir()));
            values.put(KEY_WAKTU_SELESAI, tugas.isSelesai() ? 1 : 0);

            int rowsAffected = db.update(TABLE_EVENT, values, KEY_ID_EVENT + " = ?",
                    new String[]{String.valueOf(tugas.getId())});
            db.close();

            return rowsAffected;
        }
         */

        /*
        // Delete a task
        public void deleteTugas(long id) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_EVENT, KEY_ID_EVENT + " = ?",
                    new String[]{String.valueOf(id)});
            db.close();
        }
       */

        // Get a single task by ID
            public Kalender getEvent(long id) {
                db = this.getReadableDatabase();
                cursor = null;
                Kalender kalender = null;

                try {
                    cursor = db.query(TABLE_EVENT,
                            new String[]{KEY_ID_EVENT, KEY_JUDUL_EVENT, KEY_RINCIAN_EVENT, KEY_WAKTU_MULAI, KEY_WAKTU_SELESAI},
                            KEY_ID_EVENT + "=?",
                            new String[]{String.valueOf(id)}, null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        kalender = new Kalender(); // Buat objek baru
                        kalender.setId_acara(cursor.getInt(cursor.getColumnIndex(KEY_ID_EVENT)));
                        kalender.setJudul_acara(cursor.getString(cursor.getColumnIndex(KEY_JUDUL_EVENT)));
                        kalender.setRincian_acara(cursor.getString(cursor.getColumnIndex(KEY_RINCIAN_EVENT)));

                        try {
                            String tglMulaiStr = cursor.getString(cursor.getColumnIndex(KEY_WAKTU_MULAI));
                            if (tglMulaiStr != null) {
                                kalender.setWaktuMulaiAcara(LocalDateTime.parse(tglMulaiStr));
                            }

                            String tglSelesaiStr = cursor.getString(cursor.getColumnIndex(KEY_WAKTU_SELESAI));
                            if (tglSelesaiStr != null) {
                                kalender.setWaktuSelesaiAcara(LocalDateTime.parse(tglSelesaiStr));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (db != null) {
                        db.close();
                    }
                }
                return kalender;
            }
    }
}
