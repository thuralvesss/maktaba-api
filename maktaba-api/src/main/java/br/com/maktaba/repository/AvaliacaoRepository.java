package br.com.maktaba.repository;

import br.com.maktaba.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByLivroIdAndRemovidoFalse(Long livroId);
    Optional<Avaliacao> findByUsuarioIdAndLivroId(Long usuarioId, Long livroId);
    List<Avaliacao> findByRemovidoFalse();

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.livro.id = :livroId AND a.removido = false")
    Double calcularMediaNota(@Param("livroId") Long livroId);
}