package br.com.fiap.project.controller;

import br.com.fiap.project.exception.NotificacaoNaoEncontradaException;
import br.com.fiap.project.exception.ResourceNotFoundException;
import br.com.fiap.project.model.Notificacao;
import br.com.fiap.project.service.NotificacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacao")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private Environment environment;

    @PatchMapping("/{id_notificacao}/tipostatus")
    public void atualizaStatusNotificacao(@PathVariable @NotNull Long id_notificacao) throws NotificacaoNaoEncontradaException{
        notificacaoService.atualizaStatusNotificacao(id_notificacao);
    }

    @GetMapping("/porta")
    public ResponseEntity<String> exibirPorta() {
        String porta = environment.getProperty("local.server.port");
        String mensagem = String.format("PORTA UTILIZADA NA REQUISIÇÃO: %s", porta);
        return ResponseEntity.ok(mensagem);
    }

    @GetMapping
    public ResponseEntity<List<Notificacao>> getAllNotificacoes() {
        List<Notificacao> notificacoes = notificacaoService.getAllNotificacoes();
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/{id_notificacao}")
    public ResponseEntity<Notificacao> getNotificacaoById(@PathVariable Long id_notificacao) {
        Notificacao notificacao = notificacaoService.getNotificacaoById(id_notificacao);
        if (notificacao == null) {
            throw new ResourceNotFoundException("Nenhuma notificação encontrada com o id: " + id_notificacao);
        }
        return ResponseEntity.ok(notificacao);
    }

    @PostMapping
    public ResponseEntity<?> createNotificacao(@Valid @RequestBody Notificacao notificacao, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Notificacao createdNotificacao = notificacaoService.createNotificacao(notificacao);
        return ResponseEntity.ok(createdNotificacao);
    }

    @PutMapping("/{id_notificacao}")
    public ResponseEntity<?> updateNotificacao(@PathVariable Long id_notificacao, @Valid @RequestBody Notificacao notificacao, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Notificacao updatedNotificacao = notificacaoService.updateNotificacao(id_notificacao, notificacao);
        if (updatedNotificacao == null) {
            throw new ResourceNotFoundException("Nenhuma notificação encontrada com o id: " + id_notificacao);
        }
        return ResponseEntity.ok(updatedNotificacao);
    }

    @DeleteMapping("/{id_notificacao}")
    public ResponseEntity<String> deleteNotificacao(@PathVariable Long id_notificacao) {
        if (notificacaoService.existsById(id_notificacao)) {
            notificacaoService.deleteNotificacao(id_notificacao);
            return ResponseEntity.ok("Notificação deletada com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificação com o ID " + id_notificacao + " não foi encontrada.");
        }
    }
}
