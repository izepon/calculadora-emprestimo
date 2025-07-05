package com.izepon.calculadora_emprestimo.service;

import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import com.izepon.calculadora_emprestimo.service.impl.CalculadoraEmprestimoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CalculadoraEmprestimoServiceImpl.class})
public class CalcularEmprestimoServiceTest {

    private static final LocalDate DATA_INICIAL          = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATA_FINAL            = LocalDate.of(2024, 12, 31); // precisa ser data de parcela
    private static final LocalDate PRIMEIRO_PAGAMENTO    = LocalDate.of(2024, 1, 31);
    private static final BigDecimal VALOR_EMPRESTIMO     = new BigDecimal("10000");
    private static final BigDecimal TAXA_JUROS           = new BigDecimal("0.02");

    @Autowired
    private CalculadoraEmprestimoService service;

    @Test
    void deveCalcularParcelasCorretamente() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);

        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertTrue(resultado.stream().anyMatch(p -> p.valor().compareTo(BigDecimal.ZERO) > 0));
    }

    @Test
    void deveLancarExcecaoQuandoPrimeiroPagamentoForaDoIntervalo() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                DATA_FINAL.plusDays(1),
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoQuandoDataFinalAntesDaInicial() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_INICIAL.minusDays(1),
                DATA_INICIAL,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoQuandoTaxaJurosZero() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                BigDecimal.ZERO);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveRetornarParcelasZeroQuandoValorEmprestimoZero() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                BigDecimal.ZERO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);
        Assertions.assertTrue(resultado.stream().allMatch(p -> p.valor().compareTo(BigDecimal.ZERO) == 0));
    }

    @Test
    void deveLancarExcecaoSeDataFinalNaoEhDiaDeParcela() {
        LocalDate dataFinalNaoParcela = LocalDate.of(2024, 12, 15);
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                dataFinalNaoParcela,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }
}