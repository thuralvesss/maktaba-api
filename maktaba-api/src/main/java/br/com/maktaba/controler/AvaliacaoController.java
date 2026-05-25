package br.com.maktaba.controler;

import br.com.maktaba.model.Avaliacao;
import br.com.maktaba.model.Livro;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.AvaliacaoRepository;
import br.com.maktaba.repository.LivroRepository;
import br.com.maktaba.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoRepository avaliacaoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public AvaliacaoController(AvaliacaoRepository avaliacaoRepository,
                               LivroRepository livroRepository,
                               UsuarioRepository usuarioRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/livro/{livroId}")
    public String verAvaliacoes(@PathVariable Long livroId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        Livro livro = livroRepository.findById(livroId).orElseThrow();
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByLivroIdAndRemovidoFalse(livroId);
        Double media = avaliacaoRepository.calcularMediaNota(livroId);

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        boolean jaAvaliou = avaliacaoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livroId).isPresent();

        model.addAttribute("livro", livro);
        model.addAttribute("avaliacoes", avaliacoes);
        model.addAttribute("media", media != null ? String.format("%.1f", media) : "0.0");
        model.addAttribute("jaAvaliou", jaAvaliou);
        return "avaliacoes";
    }

    @PostMapping("/avaliar")
    public String avaliar(@RequestParam Long livroId,
                          @RequestParam Integer nota,
                          @RequestParam String comentario,
                          @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        avaliacaoRepository.findByUsuarioIdAndLivroId(usuario.getId(), livroId).ifPresentOrElse(
                a -> {
                    a.setNota(nota);
                    a.setComentario(comentario);
                    a.setDataAvaliacao(LocalDateTime.now());
                    avaliacaoRepository.save(a);
                },
                () -> {
                    Avaliacao avaliacao = new Avaliacao();
                    avaliacao.setNota(nota);
                    avaliacao.setComentario(comentario);
                    avaliacao.setDataAvaliacao(LocalDateTime.now());
                    avaliacao.setRemovido(false);
                    avaliacao.setUsuario(usuario);
                    avaliacao.setLivro(livroRepository.findById(livroId).orElseThrow());
                    avaliacaoRepository.save(avaliacao);
                }
        );

        return "redirect:/avaliacoes/livro/" + livroId;
    }

    @PostMapping("/remover/{id}")
    public String remover(@PathVariable Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id).orElseThrow();
        avaliacao.setRemovido(true);
        avaliacaoRepository.save(avaliacao);
        return "redirect:/admin/avaliacoes";
    }

    @GetMapping("/admin")
    public String adminAvaliacoes(Model model) {
        model.addAttribute("avaliacoes", avaliacaoRepository.findByRemovidoFalse());
        return "admin-avaliacoes";
    }
}