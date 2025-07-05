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
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CalculadoraEmprestimoServiceImpl.class})
public class CalcularEmprestimoServiceTest {

    private static final LocalDate DATA_INICIAL = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATA_FINAL = LocalDate.of(2024, 12, 31);
    private static final LocalDate PRIMEIRO_PAGAMENTO = LocalDate.of(2024, 1, 31);
    private static final BigDecimal VALOR_EMPRESTIMO = new BigDecimal("10000");
    private static final BigDecimal TAXA_JUROS = new BigDecimal("2");

    @Autowired
    private CalculadoraEmprestimoService service;

    @Test
    void deveCalcularParcelasCorretamenteComEntradaValida() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);

        Assertions.assertFalse(resultado.isEmpty(), "A lista de parcelas não deve ser vazia.");
        Assertions.assertTrue(resultado.stream().anyMatch(r -> r.total().compareTo(BigDecimal.ZERO) > 0),
                "Deve haver parcelas com valor maior que zero.");
    }

    @Test
    void deveLancarExcecaoSeDataFinalAntesDaInicial() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_INICIAL.minusDays(1),
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoSePrimeiroPagamentoForaDoIntervaloAntes() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                DATA_INICIAL.minusDays(1),
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoSePrimeiroPagamentoForaDoIntervaloDepois() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                DATA_FINAL.plusDays(1),
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoSeTaxaJurosZero() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                BigDecimal.ZERO);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoSeTaxaJurosNegativa() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                new BigDecimal("-1"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoSeValorEmprestimoZero() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                BigDecimal.ZERO,
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveLancarExcecaoSeValorEmprestimoNegativo() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                new BigDecimal("-100"),
                TAXA_JUROS);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.calcular(request));
    }

    @Test
    void deveCalcularCorretamenteQuandoPrimeiroPagamentoNaDataInicial() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                DATA_INICIAL,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);
        Assertions.assertFalse(resultado.isEmpty());
        Assertions.assertEquals(DATA_INICIAL, resultado.stream()
                .filter(response -> !response.consolidada().isEmpty())
                .findFirst().get().data());
    }

    @Test
    void deveGerarNumeroCorretoDeParcelas() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);

        int expectedParcelas = (int) YearMonth.from(PRIMEIRO_PAGAMENTO)
                .until(YearMonth.from(DATA_FINAL), java.time.temporal.ChronoUnit.MONTHS) + 1;
        long parcelasGeradas = resultado.stream().filter(response -> !response.consolidada().isEmpty()).count();

        Assertions.assertEquals(expectedParcelas, parcelasGeradas);
    }

    @Test
    void deveCalcularCorretamenteParaDatasDificeisIncluindoFevereiro() {
        LocalDate dataInicial = LocalDate.of(2024, 1, 1);
        LocalDate dataFinal = LocalDate.of(2024, 2, 29);
        LocalDate primeiroPagamento = LocalDate.of(2024, 1, 31);

        SimulacaoRequest request = new SimulacaoRequest(
                dataInicial,
                dataFinal,
                primeiroPagamento,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);

        List<LocalDate> datasParcelas = resultado.stream()
                .filter(response -> !response.consolidada().isEmpty())
                .map(SimulacaoResponse::data)
                .toList();

        Assertions.assertTrue(datasParcelas.contains(LocalDate.of(2024, 1, 31)));
        Assertions.assertTrue(datasParcelas.contains(LocalDate.of(2024, 2, 29)));
    }

    @Test
    void deveSomarAmortizacaoCorretamente() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);

        BigDecimal somaAmortizacao = resultado.stream()
                .map(SimulacaoResponse::amortizacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertTrue(
                somaAmortizacao.subtract(VALOR_EMPRESTIMO).abs().compareTo(new BigDecimal("0.1")) < 0,
                "A soma das amortizações deve ser igual ao valor do empréstimo.");
    }

    @Test
    void deveGarantirSaldoDevedorFinalZeroOuProximo() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);

        SimulacaoResponse ultimaLinha = resultado.get(resultado.size() - 1);
        // Pode não ser exatamente zero, mas deve ser muito próximo por conta do arredondamento
        Assertions.assertTrue(
                ultimaLinha.saldoDevedor().abs().compareTo(new BigDecimal("0.1")) < 0,
                "O saldo devedor final deve ser zero ou muito próximo de zero."
        );
    }

    @Test
    void deveGarantirTodasDatasDeExibicaoPresentes() {
        SimulacaoRequest request = new SimulacaoRequest(
                DATA_INICIAL,
                DATA_FINAL,
                PRIMEIRO_PAGAMENTO,
                VALOR_EMPRESTIMO,
                TAXA_JUROS);

        List<SimulacaoResponse> resultado = service.calcular(request);
        Set<LocalDate> datasRespostas = resultado.stream()
                .map(SimulacaoResponse::data)
                .collect(Collectors.toSet());

        Set<LocalDate> datasEsperadas = new java.util.TreeSet<>();
        datasEsperadas.add(DATA_INICIAL);
        datasEsperadas.add(DATA_FINAL);

        LocalDate dataParcela = PRIMEIRO_PAGAMENTO;
        while (!dataParcela.isAfter(DATA_FINAL)) {
            datasEsperadas.add(dataParcela);
            YearMonth ym = YearMonth.from(dataParcela).plusMonths(1);
            int diaVenc = Math.min(PRIMEIRO_PAGAMENTO.getDayOfMonth(), ym.lengthOfMonth());
            dataParcela = LocalDate.of(ym.getYear(), ym.getMonth(), diaVenc);
        }

        LocalDate iter = DATA_INICIAL;
        while (!iter.isAfter(DATA_FINAL)) {
            datasEsperadas.add(iter.withDayOfMonth(iter.lengthOfMonth()));
            iter = iter.plusMonths(1);
        }

        for (LocalDate data : datasEsperadas) {
            Assertions.assertTrue(datasRespostas.contains(data),
                    "Data de exibição esperada não encontrada: " + data);
        }
    }
}