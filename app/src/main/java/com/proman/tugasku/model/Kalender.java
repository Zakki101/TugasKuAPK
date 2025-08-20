package com.proman.tugasku.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Kalender implements Serializable {
    private int id_acara;
    private String judul_acara, rincian_acara;
    private LocalDateTime waktuMulaiAcara;
    private LocalDateTime waktuSelesaiAcara;

    public Kalender() {
    }

    public Kalender(int id_acara, String judul_acara, String rincian_acara, LocalDateTime waktuMulaiAcara, LocalDateTime waktuSelesaiAcara) {
        this.id_acara = id_acara;
        this.judul_acara = judul_acara;
        this.rincian_acara = rincian_acara;
        this.waktuMulaiAcara = waktuMulaiAcara;
        this.waktuSelesaiAcara = waktuSelesaiAcara;
    }

    public int getId_acara() {
        return id_acara;
    }

    public void setId_acara(int id_acara) {
        this.id_acara = id_acara;
    }

    public String getJudul_acara() {
        return judul_acara;
    }

    public void setJudul_acara(String judul_acara) {
        this.judul_acara = judul_acara;
    }

    public String getRincian_acara() {
        return rincian_acara;
    }

    public void setRincian_acara(String rincian_acara) {
        this.rincian_acara = rincian_acara;
    }

    public LocalDateTime getWaktuMulaiAcara() {
        return waktuMulaiAcara;
    }

    public void setWaktuMulaiAcara(LocalDateTime waktuMulaiAcara) {
        this.waktuMulaiAcara = waktuMulaiAcara;
    }

    public LocalDateTime getWaktuSelesaiAcara() {
        return waktuSelesaiAcara;
    }

    public void setWaktuSelesaiAcara(LocalDateTime waktuSelesaiAcara) {
        this.waktuSelesaiAcara = waktuSelesaiAcara;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id_acara=" + id_acara +
                ", judul_acara='" + judul_acara + '\'' +
                ", waktu_mulai_acara='" + waktuMulaiAcara.toString() + '\'' +
                ", waktu_selesai_acara=" + waktuSelesaiAcara.toString() +
                '}';
    }
}
