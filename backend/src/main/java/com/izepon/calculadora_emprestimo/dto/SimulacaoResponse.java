package com.izepon.calculadora_emprestimo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoResponse(
        LocalDate data,
        BigDecimal valor
) {}
