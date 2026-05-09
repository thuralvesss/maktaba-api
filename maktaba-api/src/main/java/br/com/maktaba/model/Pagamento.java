package br.com.maktaba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String formaPagamento; // CARTAO, PIX, BOLETO

    private String status; // PENDENTE, CONFIRMADO

    private LocalDateTime dataPagamento;

    private String plano;

    private Double valor;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}