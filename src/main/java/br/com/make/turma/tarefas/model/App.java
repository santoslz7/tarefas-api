package br.com.make.turma.tarefas.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
public record App(
        String nomeExibicao,
        Versao versao,
        Ambiente ambiente

) {
    public String info() {
        return nomeExibicao + " " + versao.info();
    }
}
