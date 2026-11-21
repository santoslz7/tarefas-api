package br.com.make.turma.tarefas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlterarTarefaDTO (
        @NotBlank(message = "Preencha campo para realizar a alteração.")
        @Size(max = 60, message = "Limite máximo de 60 caracteres")
        String nome,

        @NotNull
        LocalDate dataVencimento
){
}
