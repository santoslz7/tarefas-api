package br.com.make.turma.tarefas.controller;

import br.com.make.turma.tarefas.ConverterLetters;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/saudacao")
public class SaudacaoController {

    private final ConverterLetters converterLetters;

    public SaudacaoController(ConverterLetters convert) {
        this.converterLetters = convert;
    }

    @GetMapping("/com-path-variable/{nome}")
    public String olaCompathVariable(@PathVariable String nome){
        return "Ola, " + converterLetters.converter(nome);
    }

    @GetMapping("/com-request-param")
    public String olaComRequestParam(@RequestParam String nome) {
        return "Ola, " + converterLetters.converter(nome);

    }

}

