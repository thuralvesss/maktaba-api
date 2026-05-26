package br.com.maktaba.controler;

import br.com.maktaba.model.Livro;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.LivroRepository;
import br.com.maktaba.repository.UsuarioRepository;
import br.com.maktaba.service.LivroService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize; // Importante para a segurança
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository livroRepository;
    private final LivroService livroService;
    private final UsuarioRepository usuarioRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public LivroController(LivroRepository livroRepository, LivroService livroService, UsuarioRepository usuarioRepository) {
        this.livroRepository = livroRepository;
        this.livroService = livroService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model, Principal principal) {
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());

        Usuario usuarioLogado = null;
        if (principal != null) {
            usuarioLogado = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        } else {
            usuarioLogado = usuarioRepository.findAll().stream().findFirst().orElse(null);
        }

        if (usuarioLogado != null) {
            List<Livro> recomendados = livroService.obterRecomendacoes(usuarioLogado);
            model.addAttribute("livrosRecomendados", recomendados);
            model.addAttribute("usuario", usuarioLogado);
        }

        return "catalogo";
    }

    @GetMapping("/{id}")
    public String exibirDetalhes(@PathVariable Long id, Model model) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado: " + id));

        model.addAttribute("livro", livro);
        return "livro-view";
    }

    // Bloqueia o acesso ao formulário de criação para quem não for ADMIN
    @GetMapping("/admin/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "livro-form";
    }

    // Bloqueia a ação de salvar no banco para quem não for ADMIN
    @PostMapping("/admin/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String salvar(@ModelAttribute Livro livro,
                         @RequestParam(value = "arquivo", required = false) MultipartFile arquivo) throws IOException {

        if (arquivo != null && !arquivo.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path filePath = uploadPath.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), filePath);
            livro.setArquivoPdf(nomeArquivo);
        }

        livroRepository.save(livro);
        return "redirect:/livros/catalogo";
    }

    // Bloqueia a ação de deletar para quem não for ADMIN
    @PostMapping("/admin/deletar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletar(@PathVariable Long id) {
        livroRepository.deleteById(id);
        return "redirect:/livros/catalogo";
    }
}