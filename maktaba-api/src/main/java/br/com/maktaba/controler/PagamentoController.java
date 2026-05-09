package br.com.maktaba.controler;

import br.com.maktaba.config.EmailService;
import br.com.maktaba.model.Assinatura;
import br.com.maktaba.model.Pagamento;
import br.com.maktaba.model.Usuario;
import br.com.maktaba.repository.AssinaturaRepository;
import br.com.maktaba.repository.PagamentoRepository;
import br.com.maktaba.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/pagamento")
public class PagamentoController {

    private final PagamentoRepository pagamentoRepository;
    private final AssinaturaRepository assinaturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public PagamentoController(PagamentoRepository pagamentoRepository,
                               AssinaturaRepository assinaturaRepository,
                               UsuarioRepository usuarioRepository,
                               EmailService emailService) {
        this.pagamentoRepository = pagamentoRepository;
        this.assinaturaRepository = assinaturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public String paginaPagamento(@RequestParam String plano, Model model) {
        double valor = switch (plano) {
            case "BASICO" -> 19.90;
            case "STANDARD" -> 34.90;
            case "PREMIUM" -> 49.90;
            default -> 0;
        };
        model.addAttribute("plano", plano);
        model.addAttribute("valor", String.format("%.2f", valor));
        return "pagamento";
    }

    @PostMapping("/confirmar")
    public String confirmarPagamento(@RequestParam String plano,
                                     @RequestParam String formaPagamento,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     Model model) {

        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        double valor = switch (plano) {
            case "BASICO" -> 19.90;
            case "STANDARD" -> 34.90;
            case "PREMIUM" -> 49.90;
            default -> 0;
        };

        // Salva o pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setFormaPagamento(formaPagamento);
        pagamento.setStatus("PENDENTE");
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setPlano(plano);
        pagamento.setValor(valor);
        pagamento.setUsuario(usuario);
        pagamentoRepository.save(pagamento);

        // Cria a assinatura
        assinaturaRepository.findByUsuarioId(usuario.getId()).ifPresent(a -> {
            assinaturaRepository.delete(a);
        });

        Assinatura assinatura = new Assinatura();
        assinatura.setPlano(plano);
        assinatura.setStatus("ATIVA");
        assinatura.setDataInicio(LocalDate.now());
        assinatura.setDataRenovacao(LocalDate.now().plusMonths(1));
        assinatura.setUsuario(usuario);
        assinaturaRepository.save(assinatura);

        // Notifica o admin por email
        try {
            emailService.notificarAdminPagamento(usuario.getNome(), plano, formaPagamento, valor);
        } catch (Exception e) {
            System.out.println("Erro ao notificar admin: " + e.getMessage());
        }

        model.addAttribute("plano", plano);
        model.addAttribute("formaPagamento", formaPagamento);
        model.addAttribute("valor", String.format("%.2f", valor));
        return "pagamento-confirmado";
    }
}
