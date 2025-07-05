package com.izepon.calculadora_emprestimo.service;

import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import java.util.List;

public interface CalculadoraEmprestimoService {
    List<SimulacaoResponse> calcular(SimulacaoRequest request);
}
