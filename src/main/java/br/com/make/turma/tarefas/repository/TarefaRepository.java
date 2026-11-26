package br.com.make.turma.tarefas.repository;

import br.com.make.turma.tarefas.model.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    Page<Tarefa> findByNomeContainingIgnoreCase(String q, Pageable pageable);
}