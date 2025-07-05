package com.izepon.calculadora_emprestimo.service.impl;

import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import com.izepon.calculadora_emprestimo.service.CalculadoraEmprestimoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CalculadoraEmprestimoServiceImpl implements CalculadoraEmprestimoService {

    private static final int BASE_DIAS_ANO = 360;

    @Override
    public List<SimulacaoResponse> calcular(SimulacaoRequest request) {
        validarRegras(request);
        return montarGrade(request);
    }

    private void validarRegras(SimulacaoRequest req) {
        if (!req.dataFinal().isAfter(req.dataInicial())) {
            throw new IllegalArgumentException("A data final deve ser maior que a data inicial.");
        }
        if (req.primeiroPagamento().isBefore(req.dataInicial()) || req.primeiroPagamento().isAfter(req.dataFinal())) {
            throw new IllegalArgumentException("O primeiro pagamento deve estar entre a data inicial e a data final.");
        }
        if (req.taxaJuros().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A taxa de juros deve ser maior que zero.");
        }
    }

    private List<SimulacaoResponse> montarGrade(SimulacaoRequest req) {
        List<LocalDate> parcelas = gerarDatasDeParcela(req.primeiroPagamento(), req.dataFinal());
        if (!parcelas.contains(req.dataFinal())) {
            throw new IllegalArgumentException("A data final precisa coincidir com um vencimento de parcela.");
        }
        Set<LocalDate> datasParaExibicao = gerarDatasParaExibicao(req.dataInicial(), req.dataFinal(), parcelas);
        BigDecimal valorParcela = calcularValorParcela(req.valorEmprestimo(), req.taxaJuros(), parcelas.size());

        return datasParaExibicao.stream()
                .map(data -> new SimulacaoResponse(data, parcelas.contains(data) ? valorParcela : BigDecimal.ZERO))
                .sorted(Comparator.comparing(SimulacaoResponse::data))
                .collect(Collectors.toList());
    }

    private Set<LocalDate> gerarDatasParaExibicao(LocalDate inicio, LocalDate fim, List<LocalDate> parcelas) {
        Set<LocalDate> datas = new TreeSet<>();
        datas.add(inicio);
        datas.add(fim);

        int meses = YearMonth.from(fim).compareTo(YearMonth.from(inicio));
        datas.addAll(IntStream.rangeClosed(0, meses)
                .mapToObj(mesIndice -> inicio.plusMonths(mesIndice).withDayOfMonth(inicio.plusMonths(mesIndice).lengthOfMonth()))
                .filter(data -> !data.isAfter(fim))
                .collect(Collectors.toSet()));

        datas.addAll(parcelas);
        return datas;
    }

    private List<LocalDate> gerarDatasDeParcela(LocalDate primeiroPagamento, LocalDate fim) {
        int mesesEntre = YearMonth.from(fim).compareTo(YearMonth.from(primeiroPagamento));
        return IntStream.rangeClosed(0, mesesEntre)
                .mapToObj(mesIndice -> {
                    YearMonth referencia = YearMonth.from(primeiroPagamento).plusMonths(mesIndice);
                    int diaVencimento = Math.min(primeiroPagamento.getDayOfMonth(), referencia.lengthOfMonth());
                    return LocalDate.of(referencia.getYear(), referencia.getMonth(), diaVencimento);
                })
                .filter(data -> !data.isAfter(fim))
                .collect(Collectors.toList());
    }

    private BigDecimal calcularValorParcela(BigDecimal principal, BigDecimal taxaMensal, int quantidadeParcelas) {

        BigDecimal fatorJuros = BigDecimal.ONE.add(taxaMensal);
        BigDecimal fatorDesconto = BigDecimal.ONE.divide(fatorJuros.pow(quantidadeParcelas), 10, RoundingMode.HALF_EVEN);
        BigDecimal denominador = BigDecimal.ONE.subtract(fatorDesconto);

        return principal.multiply(taxaMensal).divide(denominador, 2, RoundingMode.HALF_EVEN);
    }
}
