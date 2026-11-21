package br.com.make.turma.tarefas.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CriarTarefaDTO (

        @NotBlank(message = "Campo obrigatorio  !")
        @Size(max = 60, message = "O Limite máximo é de 60 caracteres!")
        String nome,

        @NotNull(message = "O campo não pode ser nulo!")
        @FutureOrPresent(message = "A data deve ser igual ou superior a data atual")
        LocalDate dataVencimento
){

}
