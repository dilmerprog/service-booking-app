package com.agendamento.app.models;

public class Agendamento {
    private int id;
    private int clienteId;
    private int profissionalId;
    private String servico;
    private String data;
    private String hora;
    private String status; // "confirmado", "cancelado", "remarcado"

    public Agendamento() {}

    public Agendamento(int id, int clienteId, int profissionalId,
                       String servico, String data, String hora, String status) {
        this.id = id;
        this.clienteId = clienteId;
        this.profissionalId = profissionalId;
        this.servico = servico;
        this.data = data;
        this.hora = hora;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getProfissionalId() { return profissionalId; }
    public void setProfissionalId(int profissionalId) { this.profissionalId = profissionalId; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
