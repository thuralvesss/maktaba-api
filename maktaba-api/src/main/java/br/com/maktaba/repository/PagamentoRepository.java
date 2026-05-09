package br.com.maktaba.repository;

import br.com.maktaba.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByUsuarioId(Long usuarioId);
    List<Pagamento> findByStatus(String status);
}