package com.agendamento.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.agendamento.app.databinding.ActivityMenuPrincipalBinding;

public class MenuPrincipalActivity extends AppCompatActivity {

    private ActivityMenuPrincipalBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("AgendamentoPrefs", MODE_PRIVATE);

        String nome = prefs.getString("usuario_nome", "Usuário");
        String tipo = prefs.getString("usuario_tipo", "cliente");

        binding.tvBemVindo.setText("Olá, " + nome + "!");

        // Agenda só para profissional
        boolean isProfissional = "profissional".equals(tipo);
        binding.btnAgenda.setEnabled(isProfissional);
        binding.btnAgenda.setAlpha(isProfissional ? 1f : 0.4f);

        binding.btnAgenda.setOnClickListener(v ->
                startActivity(new Intent(this, AgendaActivity.class)));

        binding.btnAgendar.setOnClickListener(v ->
                startActivity(new Intent(this, AgendamentoActivity.class)));

        binding.btnMeusAgendamentos.setOnClickListener(v ->
                startActivity(new Intent(this, MeusAgendamentosActivity.class)));

        binding.btnConfiguracoes.setOnClickListener(v ->
                startActivity(new Intent(this, ConfiguracoesActivity.class)));

        binding.btnSair.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
