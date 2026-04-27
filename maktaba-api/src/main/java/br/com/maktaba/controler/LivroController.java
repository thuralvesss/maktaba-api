package br.com.maktaba.controler;

import br.com.maktaba.model.Livro;
import br.com.maktaba.repository.LivroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/livros")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        List<Livro>livros =livroRepository.findByDisponivelTrue();
        model.addAttribute("livros", livros);
        return "catalogo";
    }

    @GetMapping("/admin/novo")
    public String novoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "livro-form";
    }

    @PostMapping("/admin/salvar")
    public String salvar(Livro livro) {
        livroRepository.save(livro);
        return "redirect:/livros/catalogo";
    }

    @PostMapping("/admin/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        livroRepository.deleteById(id);
        return "redirect:/livros/catalogo";
    }
}