package br.com.make.turma.tarefas.repository;

import br.com.make.turma.tarefas.model.Tarefa;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TarefaRepository {
    private final List<Tarefa> tarefas = new ArrayList<>();

    public Tarefa findById(Long id) {
        return tarefas.stream().filter(tarefa -> tarefa.getId().equals(id)).findFirst().orElse(null);
    }

    public Tarefa save(Tarefa tarefa) {
        tarefa.setId(tarefas.size() + 1L);
        tarefas.add(tarefa);
        return tarefa;
    }

    public void deleteById(Long id) {
        tarefas.removeIf(t -> t.getId().equals(id));
    }

    public List<Tarefa> findAll(Pageable pageable) {
        return paginar(tarefas, pageable);
    }

    public List<Tarefa> findByNomeContainingIgnoreCase(String q, Pageable pageable) {
        var filtradas = tarefas.stream()
                .filter(t -> t.getNome().toLowerCase().contains(q.toLowerCase()))
                .toList();

        return paginar(filtradas, pageable);
    }

    private List<Tarefa> paginar(List<Tarefa> tarefas, Pageable pageable) {
        long offset = pageable.getOffset();
        int tamanhoPagina = pageable.getPageSize();

        if (offset < 0 || tamanhoPagina <= 0 || offset >= tarefas.size()) {
            return List.of();
        }

        int inicio = (int) offset;
        int fim = inicio + Math.min(tamanhoPagina, tarefas.size() - inicio);
        return tarefas.subList(inicio, fim);
    }
}
