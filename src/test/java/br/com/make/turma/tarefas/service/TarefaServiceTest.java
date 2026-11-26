package br.com.make.turma.tarefas.service;

import br.com.make.turma.tarefas.exceptions.TarefaNaoEncontradaException;
import br.com.make.turma.tarefas.exceptions.ValidacaoException;
import br.com.make.turma.tarefas.model.App;
import br.com.make.turma.tarefas.model.Tarefa;
import br.com.make.turma.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.com.make.turma.tarefas.model.Ambiente.PRODUCAO;
import static br.com.make.turma.tarefas.model.Status.ABERTA;
import static br.com.make.turma.tarefas.model.Status.CONCLUIDA;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    TarefaRepository repository;
    @Mock
    App app;
    @InjectMocks
    TarefaService service;

    private Tarefa novaTarefa(br.com.make.turma.tarefas.model.Status status) {
        var t = new Tarefa();
        t.setStatus(status);
        return t;
    }

    @Test
    void consultarInexistenteLancaNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TarefaNaoEncontradaException.class, () -> service.consultar(99L));
    }

    @Test
    void concluirJaConcluidaLancaValidacao() {
        when(repository.findById(1L)).thenReturn(Optional.of(novaTarefa(CONCLUIDA)));
        assertThrows(ValidacaoException.class, () -> service.concluir(1L));
    }

    @Test
    void reabrirNaoConcluidaLancaValidacao() {
        when(repository.findById(1L)).thenReturn(Optional.of(novaTarefa(ABERTA)));
        assertThrows(ValidacaoException.class, () -> service.reabrir(1L));
    }

    @Test
    void excluirConcluidaEmProducaoLancaValidacao() {
        when(repository.findById(1L)).thenReturn(Optional.of(novaTarefa(CONCLUIDA)));
        when(app.ambiente()).thenReturn(PRODUCAO);
        assertThrows(ValidacaoException.class, () -> service.excluir(1L));
    }
}