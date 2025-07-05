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

    @Override
    public List<SimulacaoResponse> calcular(SimulacaoRequest request) {
        return calcularParcelas(request);
    }

    private static List<SimulacaoResponse> calcularParcelas(SimulacaoRequest request) {
        List<LocalDate> datasParcelas = gerarDatasParcelas(request.getPrimeiroPagamento(), request.getDataFinal());
        Set<LocalDate> datasRelevantes = gerarDatasParaExibicao (request.getDataInicial(), request.getDataFinal(), datasParcelas);
        BigDecimal valorParcela = calcularValorParcela(
                request.getValorEmprestimo(),
                request.getTaxaJuros(),
                datasParcelas.size()
        );

        return datasRelevantes.stream()
                .map(data -> new SimulacaoResponse(data, datasParcelas.contains(data) ? valorParcela : BigDecimal.ZERO))
                .sorted(Comparator.comparing(SimulacaoResponse::getData))
                .collect(Collectors.toList());
    }

    private static Set<LocalDate> gerarDatasParaExibicao (LocalDate dataInicial, LocalDate dataFinal, List<LocalDate> datasParcelas) {
        Set<LocalDate> datas = new TreeSet<>();
        datas.add(dataInicial);
        datas.add(dataFinal);

        int totalDeMeses = YearMonth.from(dataFinal).compareTo(YearMonth.from(dataInicial));
        datas.addAll(
                IntStream.rangeClosed(0, totalDeMeses)
                        .mapToObj(i -> dataInicial.plusMonths(i).withDayOfMonth(dataInicial.plusMonths(i).lengthOfMonth()))
                        .filter(data -> !data.isAfter(dataFinal))
                        .collect(Collectors.toSet())
        );

        datas.addAll(datasParcelas);
        return datas;
    }

    private static List<LocalDate> gerarDatasParcelas(LocalDate primeiroPagamento, LocalDate dataFinal) {
        int totalDeMeses = YearMonth.from(dataFinal).compareTo(YearMonth.from(primeiroPagamento));
        return IntStream.rangeClosed(0, totalDeMeses)
                .mapToObj(i -> {
                    YearMonth mes = YearMonth.from(primeiroPagamento).plusMonths(i);
                    int dia = Math.min(primeiroPagamento.getDayOfMonth(), mes.lengthOfMonth());
                    return LocalDate.of(mes.getYear(), mes.getMonth(), dia);
                })
                .filter(data -> !data.isAfter(dataFinal))
                .collect(Collectors.toList());
    }

    private static BigDecimal calcularValorParcela(BigDecimal valor, BigDecimal taxaMensal, int parcelas) {
        if (parcelas == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal juros = BigDecimal.ONE.add(taxaMensal);
        BigDecimal potencia = BigDecimal.ONE.divide(juros.pow(parcelas), 10, RoundingMode.HALF_EVEN);
        BigDecimal divisor = BigDecimal.ONE.subtract(potencia);

        return valor.multiply(taxaMensal).divide(divisor, 2, RoundingMode.HALF_EVEN);
    }
}
