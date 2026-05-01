package br.com.maktaba.repository;

import br.com.maktaba.model.ProgressoLeitura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProgressoLeituraRepository extends JpaRepository<ProgressoLeitura, Long> {
    Optional<ProgressoLeitura> findByUsuarioIdAndLivroId(Long usuarioId, Long livroId);
    List<ProgressoLeitura> findByUsuarioId(Long usuarioId);
}