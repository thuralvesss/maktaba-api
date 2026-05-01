package br.com.maktaba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "livros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String autor;

    private String genero;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String imagemUrl;

    private Boolean disponivel = true;

    private Integer totalPaginas;

    private String arquivoPdf; // caminho do arquivo salvo
}