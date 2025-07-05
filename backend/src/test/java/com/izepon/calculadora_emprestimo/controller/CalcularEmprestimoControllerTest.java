package com.izepon.calculadora_emprestimo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import com.izepon.calculadora_emprestimo.service.CalculadoraEmprestimoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculadoraEmprestimoController.class)
public class CalcularEmprestimoControllerTest {

    private static final LocalDate DATA_INICIAL = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATA_FINAL = LocalDate.of(2024, 12, 1);
    private static final LocalDate DATA_PRIMEIRO_PAGAMENTO = LocalDate.of(2024, 1, 31);
    private static final BigDecimal VALOR_EMPRESTIMO = new BigDecimal("10000");
    private static final BigDecimal TAXA_JUROS = new BigDecimal("0.02");
    private static final String VALOR_PRIMEIRO_PAGAMENTO = "927.01";
    private static final String INVALID_BODY_JSON = "{ \"dataInicial\": \"2024-01-01\" }";
    private static final String URL_TESTE = "/api/calculadora-emprestimo/calcular";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalculadoraEmprestimoService service;

    @Test
    public void deveRetornar200ComListaVazia() throws Exception {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                DATA_PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS
        );

        when(service.calcular(Mockito.eq(request))).thenReturn(Collections.emptyList());

        mockMvc.perform(post(URL_TESTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void deveRetornar200ComListaPreenchida() throws Exception {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                DATA_PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS
        );

        List<SimulacaoResponse> responseList = List.of(
                new SimulacaoResponse(DATA_INICIAL, BigDecimal.ZERO),
                new SimulacaoResponse(DATA_PRIMEIRO_PAGAMENTO, new BigDecimal(VALOR_PRIMEIRO_PAGAMENTO))
        );

        when(service.calcular(Mockito.eq(request))).thenReturn(responseList);

        mockMvc.perform(post(URL_TESTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[1].valor").value(VALOR_PRIMEIRO_PAGAMENTO));
    }

    @Test
    public void deveRetornar400QuandoRequisicaoInvalida() throws Exception {
        mockMvc.perform(post(URL_TESTE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_BODY_JSON))
                .andExpect(status().isBadRequest());
    }
}
