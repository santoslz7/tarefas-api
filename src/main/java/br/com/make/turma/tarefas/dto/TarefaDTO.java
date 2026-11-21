package br.com.make.turma.tarefas.dto;

import br.com.make.turma.tarefas.model.Status;
import br.com.make.turma.tarefas.model.Tarefa;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TarefaDTO (
        Long id,
        String nome,
        Status status,
        LocalDate dataVencimento,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
){
    public static TarefaDTO from (Tarefa tarefa){
        return new TarefaDTO(
                tarefa.getId(),
                tarefa.getNome(),
                tarefa.getStatus(),
                tarefa.getDataVencimento(),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao()
        );
    }
}
