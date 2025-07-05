package com.izepon.calculadora_emprestimo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SimulacaoRequest {
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private LocalDate primeiroPagamento;
    private BigDecimal valorEmprestimo;
    private BigDecimal taxaJuros;

    public SimulacaoRequest(LocalDate dataInicial, LocalDate dataFinal, LocalDate primeiroPagamento, BigDecimal valorEmprestimo, BigDecimal taxaJuros) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.primeiroPagamento = primeiroPagamento;
        this.valorEmprestimo = valorEmprestimo;
        this.taxaJuros = taxaJuros;
    }
}
