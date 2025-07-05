package com.izepon.calculadora_emprestimo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SimulacaoResponse {

    private LocalDate data;
    private BigDecimal valor;

    public SimulacaoResponse(LocalDate data, BigDecimal valor) {
        this.data = data;
        this.valor = valor;
    }
}
