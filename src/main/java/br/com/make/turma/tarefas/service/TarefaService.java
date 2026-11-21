package br.com.make.turma.tarefas.service;

import br.com.make.turma.tarefas.dto.AlterarTarefaDTO;
import br.com.make.turma.tarefas.dto.CriarTarefaDTO;
import br.com.make.turma.tarefas.exceptions.TarefaNaoEncontradaException;
import br.com.make.turma.tarefas.exceptions.ValidacaoException;
import br.com.make.turma.tarefas.model.App;
import br.com.make.turma.tarefas.model.Status;
import br.com.make.turma.tarefas.repository.TarefaRepository;
import br.com.make.turma.tarefas.model.Tarefa;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.make.turma.tarefas.model.Ambiente.PRODUCAO;
import static br.com.make.turma.tarefas.model.Status.ABERTA;
import static br.com.make.turma.tarefas.model.Status.CONCLUIDA;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final App app;

    public TarefaService(TarefaRepository tarefaRepository, App app) {
        this.tarefaRepository = tarefaRepository;
        this.app = app;
    }

    public Tarefa consultar(Long id) {

        var tarefa = tarefaRepository.findById(id);
        if (tarefa == null) {
            throw new TarefaNaoEncontradaException("Tarefa");
        }

        return tarefa;
    }

    public Tarefa criar(CriarTarefaDTO criarTarefaDTO) {

        var tarefa = new Tarefa();
        tarefa.setNome(criarTarefaDTO.nome());
        tarefa.setDataVencimento(criarTarefaDTO.dataVencimento());
        tarefa.setStatus(Status.ABERTA);
        tarefa.setDataCriacao(LocalDateTime.now());
        tarefa.setDataAtualizacao(null);
        return tarefaRepository.save(tarefa);
    }

    public Tarefa alterar(Long id, AlterarTarefaDTO alterarTarefaDTO) {

        var tarefa = tarefaRepository.findById(id);
        if (tarefa == null) {
            throw new TarefaNaoEncontradaException("Tarefa");
        }

        if (alterarTarefaDTO.dataVencimento().isBefore(tarefa.getDataVencimento())) {
            throw new ValidacaoException("Data de vencimento deve ser igual ou maior que a data de vencimento atual da Tarefa");
        }

        tarefa.setNome(alterarTarefaDTO.nome());
        tarefa.setDataVencimento(alterarTarefaDTO.dataVencimento());
        tarefa.setDataAtualizacao(LocalDateTime.now());
        return tarefa;
    }

    public void excluir(Long id) {

        var tarefa = tarefaRepository.findById(id);
        if (tarefa == null) {
            throw new TarefaNaoEncontradaException("Tarefa");
        }

        if (tarefa.getStatus() == CONCLUIDA && app.ambiente() == PRODUCAO) {
            throw new ValidacaoException("Tarefa concluída não pode ser excluída");
        }

        tarefaRepository.deleteById(id);
    }

    public List<Tarefa> consultarTodas(String q, Pageable pageable) {
        if (StringUtils.isBlank(q)) {
            return tarefaRepository.findAll(pageable);
        }
        return tarefaRepository.findByNomeContainingIgnoreCase(q, pageable);
    }

    public Tarefa concluir(Long id) {

        var tarefa = tarefaRepository.findById(id);
        if (tarefa == null) {
            throw new TarefaNaoEncontradaException("Tarefa");
        }

        if (tarefa.getStatus() == CONCLUIDA) {
            throw new ValidacaoException("Tarefa já está concluída");
        }

        tarefa.setStatus(CONCLUIDA);
        tarefa.setDataAtualizacao(LocalDateTime.now());
        return tarefa;
    }

    public Tarefa reabrir(Long id) {

        var tarefa = tarefaRepository.findById(id);
        if (tarefa == null) {
            throw new TarefaNaoEncontradaException("Tarefa");
        }

        if (tarefa.getStatus() != CONCLUIDA) {
            throw new ValidacaoException("Tarefa não está concluída");
        }

        tarefa.setStatus(ABERTA);
        tarefa.setDataAtualizacao(LocalDateTime.now());
        return tarefa;
    }
}