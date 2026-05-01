package com.agendamento.app.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.agendamento.app.database.DatabaseHelper;
import com.agendamento.app.databinding.ActivityConfiguracoesBinding;
import com.agendamento.app.models.Usuario;

public class ConfiguracoesActivity extends AppCompatActivity {
    private ActivityConfiguracoesBinding binding;
    private DatabaseHelper db;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracoesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.getRoot().findViewById(com.agendamento.app.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("AgendamentoPrefs", MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);
        String nome = prefs.getString("usuario_nome", "");
        String tipo = prefs.getString("usuario_tipo", "");
        binding.tvNomeUsuario.setText(nome);
        binding.tvTipoUsuario.setText("profissional".equals(tipo) ? "Profissional" : "Cliente");
        binding.btnAlterarSenha.setOnClickListener(v -> alterarSenha());
    }

    private void alterarSenha() {
        String senhaAtual = binding.etSenhaAtual.getText().toString().trim();
        String novaSenha  = binding.etNovaSenha.getText().toString().trim();
        String confirmar  = binding.etConfirmarNovaSenha.getText().toString().trim();
        if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmar.isEmpty()) { toast("Preencha todos os campos"); return; }
        if (!novaSenha.equals(confirmar)) { toast("As novas senhas não conferem"); return; }
        if (novaSenha.length() < 6) { toast("A nova senha deve ter no mínimo 6 caracteres"); return; }
        Usuario usuario = db.buscarUsuarioPorId(usuarioId);
        if (usuario == null || !usuario.getSenha().equals(senhaAtual)) { toast("Senha atual incorreta"); return; }
        if (db.atualizarSenha(usuarioId, novaSenha)) {
            toast("Senha alterada com sucesso!");
            binding.etSenhaAtual.setText(""); binding.etNovaSenha.setText(""); binding.etConfirmarNovaSenha.setText("");
        } else { toast("Erro ao alterar senha"); }
    }
    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
