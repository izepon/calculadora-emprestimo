package com.izepon.calculadora_emprestimo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoResponse(
        LocalDate data,
        BigDecimal valorEmprestimo,
        BigDecimal saldoDevedor,
        String consolidada,
        BigDecimal total,
        BigDecimal amortizacao,
        BigDecimal saldo,
        BigDecimal provisao,
        BigDecimal acumulado,
        BigDecimal pago
) {}
