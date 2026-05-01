package com.agendamento.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.agendamento.app.models.Agendamento;
import com.agendamento.app.models.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "agendamento.db";
    private static final int DATABASE_VERSION = 2;

    // Tabela usuários
    public static final String TABLE_USUARIOS    = "usuarios";
    public static final String COL_ID            = "id";
    public static final String COL_NOME          = "nome";
    public static final String COL_EMAIL         = "email";
    public static final String COL_CPF           = "cpf";
    public static final String COL_TELEFONE      = "telefone";
    public static final String COL_SENHA         = "senha";
    public static final String COL_TIPO          = "tipo";

    // Tabela agendamentos
    public static final String TABLE_AGENDAMENTOS    = "agendamentos";
    public static final String COL_AG_ID             = "id";
    public static final String COL_AG_CLIENTE_ID     = "cliente_id";
    public static final String COL_AG_PROFISSIONAL_ID = "profissional_id";
    public static final String COL_AG_SERVICO        = "servico";
    public static final String COL_AG_DATA           = "data";
    public static final String COL_AG_HORA           = "hora";
    public static final String COL_AG_STATUS         = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME     + " TEXT NOT NULL, " +
                COL_EMAIL    + " TEXT, " +
                COL_CPF      + " TEXT, " +
                COL_TELEFONE + " TEXT, " +
                COL_SENHA    + " TEXT NOT NULL, " +
                COL_TIPO     + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_AGENDAMENTOS + " (" +
                COL_AG_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AG_CLIENTE_ID      + " INTEGER NOT NULL, " +
                COL_AG_PROFISSIONAL_ID + " INTEGER NOT NULL, " +
                COL_AG_SERVICO         + " TEXT NOT NULL, " +
                COL_AG_DATA            + " TEXT NOT NULL, " +
                COL_AG_HORA            + " TEXT NOT NULL, " +
                COL_AG_STATUS          + " TEXT NOT NULL DEFAULT 'confirmado')");

        // Profissionais pré-cadastrados para o app funcionar de imediato
        inserirProfissionaisIniciais(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENDAMENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    /** Insere profissionais de exemplo na primeira instalação. */
    private void inserirProfissionaisIniciais(SQLiteDatabase db) {
        String[][] dados = {
                {"Ana Silva",     "ana@agendafacil.com",     "11111111101", "(11) 91111-1101", "123456"},
                {"Carlos Souza",  "carlos@agendafacil.com",  "11111111102", "(11) 91111-1102", "123456"},
                {"Mariana Lima",  "mariana@agendafacil.com", "11111111103", "(11) 91111-1103", "123456"},
        };
        for (String[] p : dados) {
            ContentValues cv = new ContentValues();
            cv.put(COL_NOME,     p[0]);
            cv.put(COL_EMAIL,    p[1]);
            cv.put(COL_CPF,      p[2]);
            cv.put(COL_TELEFONE, p[3]);
            cv.put(COL_SENHA,    p[4]);
            cv.put(COL_TIPO,     "profissional");
            db.insert(TABLE_USUARIOS, null, cv);
        }
    }

    // ─── Usuários ──────────────────────────────────────────────────

    public long cadastrarUsuario(Usuario u) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOME,     u.getNome());
        cv.put(COL_EMAIL,    u.getEmail());
        cv.put(COL_CPF,      u.getCpf());
        cv.put(COL_TELEFONE, u.getTelefone());
        cv.put(COL_SENHA,    u.getSenha());
        cv.put(COL_TIPO,     u.getTipo());
        return db.insert(TABLE_USUARIOS, null, cv);
    }

    public Usuario loginPorEmail(String email, String senha) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USUARIOS, null,
                COL_EMAIL + "=? AND " + COL_SENHA + "=?",
                new String[]{email, senha}, null, null, null);
        return cursorToUsuario(c);
    }

    public Usuario loginPorCpf(String cpf, String senha) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USUARIOS, null,
                COL_CPF + "=? AND " + COL_SENHA + "=?",
                new String[]{cpf, senha}, null, null, null);
        return cursorToUsuario(c);
    }

    public Usuario buscarUsuarioPorId(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USUARIOS, null,
                COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        return cursorToUsuario(c);
    }

    public boolean atualizarSenha(int usuarioId, String novaSenha) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SENHA, novaSenha);
        return db.update(TABLE_USUARIOS, cv,
                COL_ID + "=?", new String[]{String.valueOf(usuarioId)}) > 0;
    }

    public List<Usuario> listarProfissionais() {
        List<Usuario> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USUARIOS, null,
                COL_TIPO + "=?", new String[]{"profissional"},
                null, null, COL_NOME + " ASC");
        if (c.moveToFirst()) {
            do { lista.add(buildUsuario(c)); } while (c.moveToNext());
        }
        c.close();
        return lista;
    }

    private Usuario cursorToUsuario(Cursor c) {
        if (c.moveToFirst()) {
            Usuario u = buildUsuario(c);
            c.close();
            return u;
        }
        c.close();
        return null;
    }

    private Usuario buildUsuario(Cursor c) {
        return new Usuario(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NOME)),
                c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_CPF)),
                c.getString(c.getColumnIndexOrThrow(COL_TELEFONE)),
                c.getString(c.getColumnIndexOrThrow(COL_SENHA)),
                c.getString(c.getColumnIndexOrThrow(COL_TIPO))
        );
    }

    // ─── Agendamentos ──────────────────────────────────────────────

    public long criarAgendamento(Agendamento a) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_AG_CLIENTE_ID,      a.getClienteId());
        cv.put(COL_AG_PROFISSIONAL_ID, a.getProfissionalId());
        cv.put(COL_AG_SERVICO,         a.getServico());
        cv.put(COL_AG_DATA,            a.getData());
        cv.put(COL_AG_HORA,            a.getHora());
        cv.put(COL_AG_STATUS,          a.getStatus());
        return db.insert(TABLE_AGENDAMENTOS, null, cv);
    }

    public List<Agendamento> listarAgendamentosCliente(int clienteId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_AGENDAMENTOS, null,
                COL_AG_CLIENTE_ID + "=?", new String[]{String.valueOf(clienteId)},
                null, null, COL_AG_DATA + " ASC, " + COL_AG_HORA + " ASC");
        return cursorToAgendamentos(c);
    }

    public List<Agendamento> listarAgendamentosProfissional(int profissionalId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_AGENDAMENTOS, null,
                COL_AG_PROFISSIONAL_ID + "=?", new String[]{String.valueOf(profissionalId)},
                null, null, COL_AG_DATA + " ASC, " + COL_AG_HORA + " ASC");
        return cursorToAgendamentos(c);
    }

    public boolean cancelarAgendamento(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_AG_STATUS, "cancelado");
        return db.update(TABLE_AGENDAMENTOS, cv,
                COL_AG_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean remarcarAgendamento(int id, String novaData, String novaHora) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_AG_DATA,   novaData);
        cv.put(COL_AG_HORA,   novaHora);
        cv.put(COL_AG_STATUS, "remarcado");
        return db.update(TABLE_AGENDAMENTOS, cv,
                COL_AG_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean verificarHorarioOcupado(int profissionalId, String data, String hora) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_AGENDAMENTOS, null,
                COL_AG_PROFISSIONAL_ID + "=? AND " + COL_AG_DATA + "=? AND " +
                        COL_AG_HORA + "=? AND " + COL_AG_STATUS + "!=?",
                new String[]{String.valueOf(profissionalId), data, hora, "cancelado"},
                null, null, null);
        boolean ocupado = c.getCount() > 0;
        c.close();
        return ocupado;
    }

    private List<Agendamento> cursorToAgendamentos(Cursor c) {
        List<Agendamento> lista = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                lista.add(new Agendamento(
                        c.getInt(c.getColumnIndexOrThrow(COL_AG_ID)),
                        c.getInt(c.getColumnIndexOrThrow(COL_AG_CLIENTE_ID)),
                        c.getInt(c.getColumnIndexOrThrow(COL_AG_PROFISSIONAL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_AG_SERVICO)),
                        c.getString(c.getColumnIndexOrThrow(COL_AG_DATA)),
                        c.getString(c.getColumnIndexOrThrow(COL_AG_HORA)),
                        c.getString(c.getColumnIndexOrThrow(COL_AG_STATUS))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return lista;
    }
}
