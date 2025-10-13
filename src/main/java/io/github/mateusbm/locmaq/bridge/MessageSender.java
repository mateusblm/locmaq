package io.github.mateusbm.locmaq.bridge;

public interface MessageSender {
    //Define o contrato para enviar uma mensagem. 
    void send(String recipient, String subject, String body);
}