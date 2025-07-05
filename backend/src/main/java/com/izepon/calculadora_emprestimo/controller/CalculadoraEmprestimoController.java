package com.izepon.calculadora_emprestimo.controller;

import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import com.izepon.calculadora_emprestimo.service.CalculadoraEmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calculadora-emprestimo")
public class CalculadoraEmprestimoController {

    private final CalculadoraEmprestimoService service;

    public CalculadoraEmprestimoController(CalculadoraEmprestimoService service) {
        this.service = service;
    }

    @PostMapping("/calcular")
    public ResponseEntity<List<SimulacaoResponse>> calcular(@Valid @RequestBody SimulacaoRequest request) {
        List<SimulacaoResponse> resultado = service.calcular(request);
        return ResponseEntity.ok(resultado);
    }
}
