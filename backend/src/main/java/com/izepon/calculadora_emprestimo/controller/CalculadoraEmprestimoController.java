package com.izepon.calculadora_emprestimo.controller;

import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import com.izepon.calculadora_emprestimo.service.CalculadoraEmprestimoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calcular")
public class CalculadoraEmprestimoController {

    private final CalculadoraEmprestimoService service;

    public CalculadoraEmprestimoController(CalculadoraEmprestimoService service) {
        this.service = service;
    }

    @PostMapping
    public List<SimulacaoResponse> calcular(@RequestBody SimulacaoRequest request) {
        return service.calcular(request);
    }
}
