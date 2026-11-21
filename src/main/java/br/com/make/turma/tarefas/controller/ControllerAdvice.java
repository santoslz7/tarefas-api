package br.com.make.turma.tarefas.controller;

import br.com.make.turma.tarefas.dto.ErrorDTO;
import br.com.make.turma.tarefas.exceptions.TarefaNaoEncontradaException;
import br.com.make.turma.tarefas.exceptions.ValidacaoException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> tratarValidacao(MethodArgumentNotValidException e) {
        Map<String, String> campos = new HashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            campos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        var erro = new ErrorDTO(400,"Dados inválidos",campos);
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErrorDTO> tratarNaoEncontrada(TarefaNaoEncontradaException e) {
        var erro = new ErrorDTO(404, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErrorDTO> tratarRegras(ValidacaoException e) {
        var erro = new ErrorDTO(400, e.getMessage(), null);
        return ResponseEntity.badRequest().body(erro);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> tratarConstraint(ConstraintViolationException e) {
            Map<String, String> campos = new HashMap<>();
            for (var violation : e.getConstraintViolations()) {
                String caminho = violation.getPropertyPath().toString();
                String campo = caminho.substring(caminho.lastIndexOf('.') + 1);
                campos.put(campo,violation.getMessage());
            }
            var erro = new ErrorDTO(404,"Parâmetros inválidos", campos);
        return ResponseEntity.badRequest().body(erro);
    }


}