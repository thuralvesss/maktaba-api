package br.com.maktaba.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailAdmin;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarEmailRecuperacao(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Recuperação de senha - Maktaba");
        message.setText("Olá!\n\nClique no link abaixo para redefinir sua senha:\n\n"
                + "http://localhost:8080/recuperar-senha/redefinir?token=" + token
                + "\n\nEste link expira em 30 minutos.\n\nMaktaba");
        mailSender.send(message);
    }

    public void notificarAdminPagamento(String nomeUsuario, String plano, String formaPagamento, Double valor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailAdmin);
        message.setSubject(" Nova solicitação de pagamento - Maktaba");
        message.setText(
                "Nova solicitação de pagamento recebida!\n\n" +
                        " Usuário: " + nomeUsuario + "\n" +
                        " Plano: " + plano + "\n" +
                        " Valor: R$ " + String.format("%.2f", valor) + "\n" +
                        " Forma de pagamento: " + formaPagamento + "\n\n" +
                        "Acesse o dashboard para mais detalhes: http://localhost:8080/admin/dashboard"
        );
        mailSender.send(message);
    }
}