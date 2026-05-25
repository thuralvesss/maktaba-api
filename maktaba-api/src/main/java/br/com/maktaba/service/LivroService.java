package br.com.maktaba.service;

import br.com.maktaba.model.Livro;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    /**
     * Busca os livros recomendados com base nos interesses do usuário logado (RF11).
     */
    public List<Livro> obterRecomendacoes(Usuario usuario) {
        // Pegamos o Set de gêneros que o usuário escolheu no cadastro
        Set<String> interesses = usuario.getInteressesLiterarios();

        // Se o usuário por algum motivo não tiver interesses cadastrados,
        // retornamos apenas os livros disponíveis padrão para não quebrar a query
        if (interesses == null || interesses.isEmpty()) {
            return livroRepository.findByDisponivelTrue();
        }

        // Caso tenha interesses, chama a nossa query customizada com o CASE WHEN
        return livroRepository.findRecomendacoes(interesses);
    }

    // Você também pode adicionar os métodos padrão aqui futuramente, como:
    public List<Livro> listarTodosDisponiveis() {
        return livroRepository.findByDisponivelTrue();
    }
}