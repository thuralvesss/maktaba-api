package br.com.maktaba.controler;

import br.com.maktaba.model.Livro;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.LivroRepository;
import br.com.maktaba.repository.UsuarioRepository; // Importante para o RF11
import br.com.maktaba.service.LivroService; // Nosso novo serviço
import org.springframework.beans.factory.annotation.Value;
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
    private final LivroService livroService; // Adicionado
    private final UsuarioRepository usuarioRepository; // Adicionado para carregar os interesses

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Atualizado o construtor para receber as novas dependências
    public LivroController(LivroRepository livroRepository, LivroService livroService, UsuarioRepository usuarioRepository) {
        this.livroRepository = livroRepository;
        this.livroService = livroService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model, Principal principal) {
        // 1. Mantém a sua listagem padrão de todos os livros
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());

        // 2. Lógica do RF11: Buscar recomendações personalizadas
        Usuario usuarioLogado = null;
        if (principal != null) {
            usuarioLogado = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        } else {
            // Fallback de teste: pega o primeiro usuário do banco se não houver ninguém autenticado ainda
            usuarioLogado = usuarioRepository.findAll().stream().findFirst().orElse(null);
        }

        if (usuarioLogado != null) {
            List<Livro> recomendados = livroService.obterRecomendacoes(usuarioLogado);
            model.addAttribute("livrosRecomendados", recomendados);
            model.addAttribute("usuario", usuarioLogado);
        }

        return "catalogo";
    }

    @GetMapping("/admin/novo")
    public String novoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "livro-form";
    }

    @PostMapping("/admin/salvar")
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

    @PostMapping("/admin/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        livroRepository.deleteById(id);
        return "redirect:/livros/catalogo";
    }
}