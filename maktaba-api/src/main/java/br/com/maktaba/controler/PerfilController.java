package br.com.maktaba.controler;

import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.AssinaturaRepository;
import br.com.maktaba.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final AssinaturaRepository assinaturaRepository;

    public PerfilController(UsuarioRepository usuarioRepository,
                            AssinaturaRepository assinaturaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.assinaturaRepository = assinaturaRepository;
    }

    @GetMapping
    public String perfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        model.addAttribute("usuario", usuario);

        assinaturaRepository.findByUsuarioId(usuario.getId())
                .ifPresent(a -> model.addAttribute("assinatura", a));

        return "perfil";
    }

    @GetMapping("/generos")
    public String generos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        List<String> todosGeneros = List.of(
                "Fantasia", "Romance", "Distopia", "Literatura Brasileira",
                "Terror", "Aventura", "Ficção Científica", "Mistério",
                "Biografia", "História", "Autoajuda", "Policial"
        );

        List<String> generosEscolhidos = new ArrayList<>();
        // CORREÇÃO: Verifica o Set diretamente sem usar .split()
        if (usuario.getInteressesLiterarios() != null && !usuario.getInteressesLiterarios().isEmpty()) {
            generosEscolhidos = new ArrayList<>(usuario.getInteressesLiterarios());
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("todosGeneros", todosGeneros);
        model.addAttribute("generosEscolhidos", generosEscolhidos);
        return "generos";
    }

    @PostMapping("/generos")
    public String salvarGeneros(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(value = "generos", required = false) List<String> generos,
                                Model model) {

        // Validação da regra de negócio (Mínimo 1, Máximo 8 - RF05)
        if (generos == null || generos.isEmpty() || generos.size() > 8) {
            model.addAttribute("erro", "Escolha entre 1 e 8 gêneros literários.");
            return "redirect:/perfil/generos";
        }

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // CORREÇÃO: Transforma a List do formulário em um Set<String> e salva direto
        usuario.setInteressesLiterarios(new HashSet<>(generos));
        usuarioRepository.save(usuario);

        return "redirect:/perfil";
    }
}