package com.agendamento.app.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.agendamento.app.database.DatabaseHelper;
import com.agendamento.app.databinding.ActivityAgendamentoBinding;
import com.agendamento.app.models.Agendamento;
import com.agendamento.app.models.Usuario;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {
    private ActivityAgendamentoBinding binding;
    private DatabaseHelper db;
    private List<Usuario> profissionais = new ArrayList<>();
    private String dataSelecionada = "";
    private String horaSelecionada = "";
    private int profissionalSelecionadoId = -1;
    private int clienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAgendamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.getRoot().findViewById(com.agendamento.app.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("AgendamentoPrefs", MODE_PRIVATE);
        clienteId = prefs.getInt("usuario_id", -1);
        carregarProfissionais();
        configurarCampos();
        binding.btnAgendar.setOnClickListener(v -> realizarAgendamento());
    }

    private void carregarProfissionais() {
        profissionais = db.listarProfissionais();
        if (profissionais.isEmpty()) {
            binding.spinnerProfissional.setVisibility(View.GONE);
            binding.tvSemProfissional.setVisibility(View.VISIBLE);
            binding.btnAgendar.setEnabled(false);
            binding.btnAgendar.setAlpha(0.5f);
            return;
        }
        binding.spinnerProfissional.setVisibility(View.VISIBLE);
        binding.tvSemProfissional.setVisibility(View.GONE);
        binding.btnAgendar.setEnabled(true);
        binding.btnAgendar.setAlpha(1f);
        List<String> nomes = new ArrayList<>();
        for (Usuario u : profissionais) nomes.add(u.getNome());
        binding.spinnerProfissional.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int pos, long id) {
                        profissionalSelecionadoId = profissionais.get(pos).getId();
                    }
                    @Override public void onNothingSelected(android.widget.AdapterView<?> p) { profissionalSelecionadoId = -1; }
                });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerProfissional.setAdapter(adapter);
        profissionalSelecionadoId = profissionais.get(0).getId();
    }

    private void configurarCampos() {
        Calendar cal = Calendar.getInstance();
        binding.etData.setFocusable(false);
        binding.etData.setFocusableInTouchMode(false);
        binding.etHora.setFocusable(false);
        binding.etHora.setFocusableInTouchMode(false);
        binding.etData.setOnClickListener(v ->
                new DatePickerDialog(this, (view, year, month, day) -> {
                    dataSelecionada = String.format("%04d-%02d-%02d", year, month + 1, day);
                    binding.etData.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show());
        binding.etHora.setOnClickListener(v ->
                new TimePickerDialog(this, (view, hour, minute) -> {
                    horaSelecionada = String.format("%02d:%02d", hour, minute);
                    binding.etHora.setText(horaSelecionada);
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show());
    }

    private void realizarAgendamento() {
        String servico = binding.etServico.getText().toString().trim();
        if (servico.isEmpty())               { toast("Informe o serviço"); return; }
        if (dataSelecionada.isEmpty())       { toast("Selecione a data"); return; }
        if (horaSelecionada.isEmpty())       { toast("Selecione o horário"); return; }
        if (profissionalSelecionadoId == -1) { toast("Selecione um profissional"); return; }
        if (db.verificarHorarioOcupado(profissionalSelecionadoId, dataSelecionada, horaSelecionada)) {
            toast("Este horário já está ocupado. Escolha outro."); return;
        }
        Agendamento a = new Agendamento();
        a.setClienteId(clienteId); a.setProfissionalId(profissionalSelecionadoId);
        a.setServico(servico); a.setData(dataSelecionada);
        a.setHora(horaSelecionada); a.setStatus("confirmado");
        if (db.criarAgendamento(a) > 0) { toast("Agendamento realizado com sucesso!"); finish(); }
        else { toast("Erro ao realizar agendamento. Tente novamente."); }
    }
    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
