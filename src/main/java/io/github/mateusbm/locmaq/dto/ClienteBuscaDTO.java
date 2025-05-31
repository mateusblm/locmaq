package io.github.mateusbm.locmaq.dto;

public class ClienteBuscaDTO {
    private Long id;
    private String nome;
    private String email;
    private String tipo;

    public ClienteBuscaDTO(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = "cliente";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}

