package com.proman.tugasku.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proman.tugasku.R;
import com.proman.tugasku.activity.UpdateTaskActivity;
import com.proman.tugasku.model.Kalender;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KalenderAdapter extends RecyclerView.Adapter<KalenderAdapter.EventViewHolder> {
    private final List<Kalender> listEvent = new ArrayList<>();
    private final Activity activity;

    public KalenderAdapter(Activity activity) {
        this.activity = activity;
    }

        public void setListEvent(List<Kalender> listEvent) {
        this.listEvent.clear();
        this.listEvent.addAll(listEvent);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Mendapatkan data acara pada posisi tertentu
        Kalender kalender = listEvent.get(position);

        // Mengatur data ke dalam komponen View
        holder.tv_title_event.setText(kalender.getJudul_acara());

        // Memformat dan menampilkan tanggal serta durasi acara
        if (kalender.getWaktuMulaiAcara() != null && kalender.getWaktuSelesaiAcara() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String tglMulai = kalender.getWaktuMulaiAcara().format(formatter);
            String tglSelesai = kalender.getWaktuSelesaiAcara().format(formatter);
            holder.tv_acara_mulai.setText(String.format("%s - %s", tglMulai, tglSelesai));

            // Menghitung durasi dalam hari
            long daysBetween = Duration.between(kalender.getWaktuMulaiAcara().toLocalDate().atStartOfDay(), kalender.getWaktuSelesaiAcara().toLocalDate().atStartOfDay()).toDays();
            holder.tv_lamaacara.setText(String.format("%d Hari", daysBetween + 1));
        } else {
            // Jika data tanggal tidak ada, kosongkan view
            holder.tv_acara_mulai.setText("");
            holder.tv_lamaacara.setText("");
        }

        // Menambahkan OnClickListener untuk setiap item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, UpdateTaskActivity.class);
            intent.putExtra("event", kalender);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listEvent.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Deklarasikan semua View dari event.xml
        final TextView tv_title_event;
        final TextView tv_acara_mulai, tv_acara_selesai;
        final TextView tv_lamaacara;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title_event = itemView.findViewById(R.id.tv_title_event);
            tv_acara_mulai = itemView.findViewById(R.id.tv_awal_acara);
            tv_acara_selesai = itemView.findViewById(R.id.tv_akhir_acara);
            tv_lamaacara = itemView.findViewById(R.id.tv_lamaacara);
        }
    }
}
