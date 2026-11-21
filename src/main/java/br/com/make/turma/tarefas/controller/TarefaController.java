package br.com.make.turma.tarefas.controller;

import br.com.make.turma.tarefas.dto.AlterarTarefaDTO;
import br.com.make.turma.tarefas.dto.CriarTarefaDTO;
import br.com.make.turma.tarefas.dto.TarefaDTO;
import br.com.make.turma.tarefas.service.TarefaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tarefa")
@Validated
public class TarefaController {

    private final TarefaService tarefaService;

    TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> consultar(@PathVariable @Positive Long id) {
            var tarefa = tarefaService.consultar(id);
            var dto = TarefaDTO.from(tarefa);
            return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody CriarTarefaDTO criarTarefaDTO) {
            var tarefa = tarefaService.criar(criarTarefaDTO);
            var dto = TarefaDTO.from(tarefa);
            return ResponseEntity.created(URI.create("/tarefa/" + dto.id())).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterar(@PathVariable @Positive Long id,@Valid @RequestBody AlterarTarefaDTO alterarTarefaDTO) {
            var tarefa = tarefaService.alterar(id, alterarTarefaDTO);
            var dto = TarefaDTO.from(tarefa);
            return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable @Positive  Long id) {
            tarefaService.excluir(id);
            return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<TarefaDTO> consultarTodas(@RequestParam(required = false) String q, @PageableDefault Pageable pageable) {
        return tarefaService.consultarTodas(q, pageable)
                .stream()
                .map(TarefaDTO::from)
                .toList();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<?> concluir(@PathVariable @Positive Long id) {
            var tarefa = tarefaService.concluir(id);
            var dto = TarefaDTO.from(tarefa);
            return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<?> reabrir(@PathVariable @Positive Long id) {
            var tarefa = tarefaService.reabrir(id);
            var dto = TarefaDTO.from(tarefa);
            return ResponseEntity.ok(dto);
    }
}