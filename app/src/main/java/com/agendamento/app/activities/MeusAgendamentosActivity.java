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
import com.agendamento.app.databinding.ActivityMeusAgendamentosBinding;
import com.agendamento.app.models.Agendamento;
import java.util.List;

public class MeusAgendamentosActivity extends AppCompatActivity {
    private ActivityMeusAgendamentosBinding binding;
    private DatabaseHelper db;
    private int clienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMeusAgendamentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.getRoot().findViewById(com.agendamento.app.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("AgendamentoPrefs", MODE_PRIVATE);
        clienteId = prefs.getInt("usuario_id", -1);
        if (clienteId == -1) { finish(); return; }
        carregarAgendamentos();
    }

    private void carregarAgendamentos() {
        List<Agendamento> lista = db.listarAgendamentosCliente(clienteId);
        if (lista.isEmpty()) {
            binding.tvVazio.setVisibility(View.VISIBLE);
            binding.rvAgendamentos.setVisibility(View.GONE);
        } else {
            binding.tvVazio.setVisibility(View.GONE);
            binding.rvAgendamentos.setVisibility(View.VISIBLE);
            binding.rvAgendamentos.setLayoutManager(new LinearLayoutManager(this));
            binding.rvAgendamentos.setAdapter(new AgendamentoAdapter(lista, false,
                    ag -> { db.cancelarAgendamento(ag.getId());
                        Toast.makeText(this, "Agendamento cancelado", Toast.LENGTH_SHORT).show();
                        carregarAgendamentos(); },
                    (ag, novaData, novaHora) -> { db.remarcarAgendamento(ag.getId(), novaData, novaHora);
                        Toast.makeText(this, "Agendamento remarcado", Toast.LENGTH_SHORT).show();
                        carregarAgendamentos(); }
            ));
        }
    }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
