package com.izepon.calculadora_emprestimo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoRequest(
        @NotNull LocalDate dataInicial,
        @NotNull LocalDate dataFinal,
        @NotNull LocalDate primeiroPagamento,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal valorEmprestimo,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal taxaJuros
) {}
