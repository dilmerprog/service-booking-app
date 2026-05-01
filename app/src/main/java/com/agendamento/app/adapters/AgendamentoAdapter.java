package com.agendamento.app.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.agendamento.app.R;
import com.agendamento.app.models.Agendamento;

import java.util.Calendar;
import java.util.List;

public class AgendamentoAdapter extends RecyclerView.Adapter<AgendamentoAdapter.ViewHolder> {

    public interface OnCancelar { void onCancelar(Agendamento ag); }
    public interface OnRemarcar { void onRemarcar(Agendamento ag, String novaData, String novaHora); }

    private final List<Agendamento> lista;
    private final boolean isProfissional;
    private final OnCancelar onCancelar;
    private final OnRemarcar onRemarcar;

    public AgendamentoAdapter(List<Agendamento> lista, boolean isProfissional,
                               OnCancelar onCancelar, OnRemarcar onRemarcar) {
        this.lista = lista;
        this.isProfissional = isProfissional;
        this.onCancelar = onCancelar;
        this.onRemarcar = onRemarcar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServico, tvData, tvHora, tvStatus;
        Button btnCancelar, btnRemarcar;
        public ViewHolder(View view) {
            super(view);
            tvServico  = view.findViewById(R.id.tvServico);
            tvData     = view.findViewById(R.id.tvData);
            tvHora     = view.findViewById(R.id.tvHora);
            tvStatus   = view.findViewById(R.id.tvStatus);
            btnCancelar = view.findViewById(R.id.btnCancelar);
            btnRemarcar = view.findViewById(R.id.btnRemarcar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agendamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Agendamento ag = lista.get(position);

        holder.tvServico.setText(ag.getServico());

        // Data formatada
        String[] partes = ag.getData().split("-");
        String dataFormatada = partes.length == 3
                ? partes[2] + "/" + partes[1] + "/" + partes[0]
                : ag.getData();
        holder.tvData.setText(dataFormatada);
        holder.tvHora.setText(ag.getHora());

        // Badge de status colorido
        String status = ag.getStatus();
        String label = status.substring(0, 1).toUpperCase() + status.substring(1);
        holder.tvStatus.setText(label);

        android.content.Context ctx = holder.itemView.getContext();
        switch (status) {
            case "confirmado":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
                holder.tvStatus.setTextColor(Color.parseColor("#15803D"));
                break;
            case "cancelado":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_red);
                holder.tvStatus.setTextColor(Color.parseColor("#B91C1C"));
                break;
            case "remarcado":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
                holder.tvStatus.setTextColor(Color.parseColor("#92400E"));
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
                holder.tvStatus.setTextColor(Color.parseColor("#15803D"));
        }

        boolean cancelado = "cancelado".equals(status);
        holder.btnCancelar.setEnabled(!cancelado);
        holder.btnRemarcar.setEnabled(!cancelado);
        holder.btnCancelar.setAlpha(cancelado ? 0.4f : 1f);
        holder.btnRemarcar.setAlpha(cancelado ? 0.4f : 1f);

        holder.btnCancelar.setOnClickListener(v ->
                new AlertDialog.Builder(ctx)
                        .setTitle("Cancelar Agendamento")
                        .setMessage("Deseja cancelar este agendamento?")
                        .setPositiveButton("Sim", (d, w) -> onCancelar.onCancelar(ag))
                        .setNegativeButton("Não", null)
                        .show());

        holder.btnRemarcar.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(ctx,
                    (view, year, month, day) -> {
                        String novaData = String.format("%04d-%02d-%02d", year, month + 1, day);
                        new TimePickerDialog(ctx,
                                (tv, hour, minute) -> {
                                    String novaHora = String.format("%02d:%02d", hour, minute);
                                    onRemarcar.onRemarcar(ag, novaData, novaHora);
                                },
                                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
                    },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }
}
