package com.agendamento.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.agendamento.app.adapters.AgendamentoAdapter;
import com.agendamento.app.database.DatabaseHelper;
import com.agendamento.app.databinding.ActivityAgendaBinding;
import com.agendamento.app.models.Agendamento;
import java.util.List;

public class AgendaActivity extends AppCompatActivity {
    private ActivityAgendaBinding binding;
    private DatabaseHelper db;
    private int profissionalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAgendaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.getRoot().findViewById(com.agendamento.app.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("AgendamentoPrefs", MODE_PRIVATE);
        profissionalId = prefs.getInt("usuario_id", -1);
        if (profissionalId == -1) { finish(); return; }
        carregarAgenda();
    }

    private void carregarAgenda() {
        List<Agendamento> lista = db.listarAgendamentosProfissional(profissionalId);
        if (lista.isEmpty()) {
            binding.tvVazio.setVisibility(View.VISIBLE);
            binding.rvAgendamentos.setVisibility(View.GONE);
        } else {
            binding.tvVazio.setVisibility(View.GONE);
            binding.rvAgendamentos.setVisibility(View.VISIBLE);
            binding.rvAgendamentos.setLayoutManager(new LinearLayoutManager(this));
            binding.rvAgendamentos.setAdapter(new AgendamentoAdapter(lista, true,
                    ag -> { db.cancelarAgendamento(ag.getId());
                        Toast.makeText(this, "Agendamento cancelado", Toast.LENGTH_SHORT).show();
                        carregarAgenda(); },
                    (ag, novaData, novaHora) -> { db.remarcarAgendamento(ag.getId(), novaData, novaHora);
                        Toast.makeText(this, "Agendamento remarcado", Toast.LENGTH_SHORT).show();
                        carregarAgenda(); }
            ));
        }
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
