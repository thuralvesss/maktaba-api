package br.com.maktaba.repository;

import br.com.maktaba.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Set;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByDisponivelTrue();

    List<Livro> findByGenero(String genero);

    // Query customizada para o RF11 (Sistema de Recomendações)
    @Query("SELECT l FROM Livro l WHERE l.disponivel = true " +
            "ORDER BY " +
            "CASE WHEN l.genero IN :interesses THEN 2 " +
            "     WHEN l.emDestaque = true THEN 1 " +
            "     ELSE 0 END DESC, l.titulo ASC")
    List<Livro> findRecomendacoes(@Param("interesses") Set<String> interesses);
}