package br.com.maktaba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "progresso_leitura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressoLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer paginaAtual;

    private String status; // NAO_LIDO, LENDO, CONCLUIDO

    private LocalDate dataInicio;

    private LocalDate dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;

    public double getPorcentagem() {
        if (livro == null || livro.getTotalPaginas() == null || livro.getTotalPaginas() == 0) return 0;
        return Math.min(100.0, (paginaAtual * 100.0) / livro.getTotalPaginas());
    }
}