package br.com.make.turma.tarefas.exceptions;

public class TarefaNaoEncontradaException extends RuntimeException{

    public TarefaNaoEncontradaException(String recurso){
        super(recurso +" não encontrada" ) ;

    }


}
