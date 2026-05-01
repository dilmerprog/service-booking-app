package com.agendamento.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agendamento.app.database.DatabaseHelper;
import com.agendamento.app.databinding.ActivityLoginBinding;
import com.agendamento.app.models.Usuario;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("AgendamentoPrefs", MODE_PRIVATE);

        // Já está logado?
        if (prefs.getInt("usuario_id", -1) != -1) {
            irParaMenu();
            return;
        }

        binding.btnEntrar.setOnClickListener(v -> realizarLogin());
        binding.tvCadastrar.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
    }

    private void realizarLogin() {
        String identificador = binding.etIdentificador.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        if (identificador.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario;
        if (identificador.contains("@")) {
            usuario = db.loginPorEmail(identificador, senha);
        } else {
            String cpf = identificador.replaceAll("[.\\-]", "");
            usuario = db.loginPorCpf(cpf, senha);
        }

        if (usuario != null) {
            prefs.edit()
                    .putInt("usuario_id", usuario.getId())
                    .putString("usuario_nome", usuario.getNome())
                    .putString("usuario_tipo", usuario.getTipo())
                    .apply();
            irParaMenu();
        } else {
            Toast.makeText(this, "E-mail/CPF ou senha incorretos", Toast.LENGTH_SHORT).show();
        }
    }

    private void irParaMenu() {
        startActivity(new Intent(this, MenuPrincipalActivity.class));
        finish();
    }
}
