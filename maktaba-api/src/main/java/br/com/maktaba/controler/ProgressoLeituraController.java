package br.com.maktaba.controler;

import br.com.maktaba.model.Livro;
import br.com.maktaba.model.ProgressoLeitura;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.LivroRepository;
import br.com.maktaba.repository.ProgressoLeituraRepository;
import br.com.maktaba.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/leitura")
public class ProgressoLeituraController {

    private final ProgressoLeituraRepository progressoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public ProgressoLeituraController(ProgressoLeituraRepository progressoRepository,
                                      LivroRepository livroRepository,
                                      UsuarioRepository usuarioRepository) {
        this.progressoRepository = progressoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String minhasLeituras(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        List<ProgressoLeitura> progressos = progressoRepository.findByUsuarioId(usuario.getId());
        List<Livro> todosLivros = livroRepository.findByDisponivelTrue();

        model.addAttribute("progressos", progressos);
        model.addAttribute("todosLivros", todosLivros);
        return "leitura";
    }

    @PostMapping("/iniciar")
    public String iniciar(@RequestParam Long livroId,
                          @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        progressoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livroId).ifPresentOrElse(
                p -> {},
                () -> {
                    Livro livro = livroRepository.findById(livroId).orElseThrow();
                    ProgressoLeitura progresso = new ProgressoLeitura();
                    progresso.setUsuario(usuario);
                    progresso.setLivro(livro);
                    progresso.setPaginaAtual(0);
                    progresso.setStatus("NAO_LIDO");
                    progresso.setDataInicio(LocalDate.now());
                    progresso.setDataAtualizacao(LocalDate.now());
                    progressoRepository.save(progresso);
                }
        );
        return "redirect:/leitura";
    }

    @PostMapping("/atualizar")
    public String atualizar(@RequestParam Long progressoId,
                            @RequestParam Integer paginaAtual) {
        ProgressoLeitura progresso = progressoRepository.findById(progressoId).orElseThrow();
        progresso.setPaginaAtual(paginaAtual);
        progresso.setDataAtualizacao(LocalDate.now());

        if (paginaAtual == 0) {
            progresso.setStatus("NAO_LIDO");
        } else if (progresso.getLivro().getTotalPaginas() != null &&
                paginaAtual >= progresso.getLivro().getTotalPaginas()) {
            progresso.setStatus("CONCLUIDO");
        } else {
            progresso.setStatus("LENDO");
        }

        progressoRepository.save(progresso);
        return "redirect:/leitura";
    }

    @GetMapping("/ler/{livroId}")
    public String lerLivro(@PathVariable Long livroId, Model model) {
        Livro livro = livroRepository.findById(livroId).orElseThrow();
        model.addAttribute("livro", livro);
        return "ler-livro";
    }
}