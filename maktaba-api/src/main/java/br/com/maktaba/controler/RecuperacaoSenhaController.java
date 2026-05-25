package br.com.maktaba.controler;

import br.com.maktaba.service.EmailService;
import br.com.maktaba.model.TokenRecuperacao;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.TokenRecuperacaoRepository;
import br.com.maktaba.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/recuperar-senha")
public class RecuperacaoSenhaController {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public RecuperacaoSenhaController(UsuarioRepository usuarioRepository,
                                      TokenRecuperacaoRepository tokenRepository,
                                      EmailService emailService,
                                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String paginaSolicitacao() {
        return "recuperar-senha";
    }

    @PostMapping("/solicitar")
    public String solicitar(@RequestParam String email, Model model) {
        var usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("erro", "E-mail não encontrado.");
            return "recuperar-senha";
        }

        Usuario usuario = usuarioOpt.get();
        String token = UUID.randomUUID().toString();

        TokenRecuperacao tokenRecuperacao = new TokenRecuperacao();
        tokenRecuperacao.setToken(token);
        tokenRecuperacao.setExpiracao(LocalDateTime.now().plusMinutes(30));
        tokenRecuperacao.setUsado(false);
        tokenRecuperacao.setUsuario(usuario);

        tokenRepository.save(tokenRecuperacao);
        emailService.enviarEmailRecuperacao(email, token);

        model.addAttribute("sucesso", "E-mail enviado! Verifique sua caixa de entrada.");
        return "recuperar-senha";
    }

    @GetMapping("/redefinir")
    public String paginaRedefinir(@RequestParam String token, Model model) {
        var tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isUsado() ||
                tokenOpt.get().getExpiracao().isBefore(LocalDateTime.now())) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "redefinir-senha";
        }

        model.addAttribute("token", token);
        return "redefinir-senha";
    }

    @PostMapping("/redefinir")
    public String redefinir(@RequestParam String token,
                            @RequestParam String novaSenha,
                            Model model) {
        var tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isUsado() ||
                tokenOpt.get().getExpiracao().isBefore(LocalDateTime.now())) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "redefinir-senha";
        }

        TokenRecuperacao tokenRecuperacao = tokenOpt.get();
        Usuario usuario = tokenRecuperacao.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        tokenRecuperacao.setUsado(true);
        tokenRepository.save(tokenRecuperacao);

        model.addAttribute("sucesso", "Senha redefinida com sucesso!");
        return "redefinir-senha";
    }
}