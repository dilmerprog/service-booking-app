package com.agendamento.app.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.agendamento.app.database.DatabaseHelper;
import com.agendamento.app.databinding.ActivityCadastroBinding;
import com.agendamento.app.models.Usuario;

public class CadastroActivity extends AppCompatActivity {
    private ActivityCadastroBinding binding;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.getRoot().findViewById(com.agendamento.app.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        binding.btnCadastrar.setOnClickListener(v -> realizarCadastro());
    }

    private void realizarCadastro() {
        String nome     = binding.etNome.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();
        String cpf      = binding.etCpf.getText().toString().trim().replaceAll("[.\\-]", "");
        String telefone = binding.etTelefone.getText().toString().trim();
        String senha    = binding.etSenha.getText().toString().trim();
        String confirmar= binding.etConfirmarSenha.getText().toString().trim();
        String tipo     = binding.rbProfissional.isChecked() ? "profissional" : "cliente";
        if (nome.isEmpty())                         { toast("Informe o nome"); return; }
        if (email.isEmpty() && cpf.isEmpty())       { toast("Informe e-mail ou CPF"); return; }
        if (senha.isEmpty())                        { toast("Informe a senha"); return; }
        if (!senha.equals(confirmar))               { toast("As senhas não conferem"); return; }
        if (senha.length() < 6)                     { toast("A senha deve ter no mínimo 6 caracteres"); return; }
        if (!cpf.isEmpty() && cpf.length() != 11)  { toast("CPF inválido"); return; }
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email.isEmpty() ? null : email);
        u.setCpf(cpf.isEmpty() ? null : cpf);
        u.setTelefone(telefone.isEmpty() ? null : telefone);
        u.setSenha(senha);
        u.setTipo(tipo);
        if (db.cadastrarUsuario(u) > 0) { toast("Cadastro realizado com sucesso!"); finish(); }
        else { toast("Erro ao realizar cadastro"); }
    }
    private void toast(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
