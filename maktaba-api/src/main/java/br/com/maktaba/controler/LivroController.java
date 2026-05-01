package br.com.maktaba.controler;

import br.com.maktaba.model.Livro;
import br.com.maktaba.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());
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