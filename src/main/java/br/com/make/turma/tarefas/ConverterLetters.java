package br.com.make.turma.tarefas;
import org.springframework.stereotype.Service;

@Service
public class ConverterLetters {

    public String converter(String nome) {
        return nome.toUpperCase();
    }
}
