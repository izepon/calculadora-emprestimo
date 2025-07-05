package com.izepon.calculadora_emprestimo.service.impl;
//
//import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
//import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
//import com.izepon.calculadora_emprestimo.service.CalculadoraEmprestimoService;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//@Service
//public class CalculadoraEmprestimoServiceImpl implements CalculadoraEmprestimoService {
//
//    private static final int BASE_DIAS_ANO = 360;
//
//    @Override
//    public List<SimulacaoResponse> calcular(SimulacaoRequest request) {
//        validarRegras(request);
//        return gerarSimulacao(request);
//    }
//
//    private List<SimulacaoResponse> gerarSimulacao(SimulacaoRequest request) {
//        List<LocalDate> datasDeParcela = gerarDatasDeParcela(request.primeiroPagamento(), request.dataFinal());
//        Set<LocalDate> datasExibicao = gerarDatasParaExibicao(request.dataInicial(), request.dataFinal(), datasDeParcela);
//
//        BigDecimal taxaMensal = converterTaxaMensal(request.taxaJuros());
//        int totalParcelas = datasDeParcela.size();
//        BigDecimal valorParcela = calcularSimulacao(request.valorEmprestimo(), taxaMensal, totalParcelas);
//
//        BigDecimal saldoDevedor = request.valorEmprestimo();
//        BigDecimal jurosAcumulados = BigDecimal.ZERO;
//        int indiceParcela = 0;
//
//        List<SimulacaoResponse> resultados = new ArrayList<>();
//        for (LocalDate dataCompetencia : datasExibicao.stream().sorted().toList()) {
//            boolean isDataDePagamento = datasDeParcela.contains(dataCompetencia);
//
//            BigDecimal valorEmprestimoColuna = dataCompetencia.equals(request.dataInicial())
//                    ? request.valorEmprestimo() : BigDecimal.ZERO;
//
//            BigDecimal jurosDoPeriodo = calcularJurosDoPeriodo(saldoDevedor, taxaMensal);
//            BigDecimal provisaoJuros = isDataDePagamento ? BigDecimal.ZERO : jurosDoPeriodo;
//            jurosAcumulados = isDataDePagamento ? BigDecimal.ZERO : jurosAcumulados.add(provisaoJuros);
//
//            String parcelaConsolidada = "";
//            BigDecimal totalParcela = BigDecimal.ZERO;
//            BigDecimal amortizacao = BigDecimal.ZERO;
//            BigDecimal valorPago = BigDecimal.ZERO;
//
//            if (isDataDePagamento) {
//                indiceParcela++;
//                amortizacao = valorParcela.subtract(jurosDoPeriodo);
//                BigDecimal novoSaldo = saldoDevedor.subtract(amortizacao).setScale(2, RoundingMode.HALF_EVEN);
//                totalParcela = valorParcela;
//                valorPago = valorParcela;
//                parcelaConsolidada = indiceParcela + "/" + totalParcelas;
//                saldoDevedor = novoSaldo;
//            }
//
//            resultados.add(new SimulacaoResponse(
//                    dataCompetencia,
//                    valorEmprestimoColuna,
//                    isDataDePagamento ? saldoDevedor.add(amortizacao) : saldoDevedor,
//                    parcelaConsolidada,
//                    totalParcela,
//                    amortizacao,
//                    saldoDevedor,
//                    provisaoJuros,
//                    jurosAcumulados,
//                    valorPago
//            ));
//        }
//        return resultados;
//    }
//
//    private void validarRegras(SimulacaoRequest request) {
//        if (!request.dataFinal().isAfter(request.dataInicial())) {
//            throw new IllegalArgumentException("A data final deve ser maior que a data inicial.");
//        }
//        if (request.primeiroPagamento().isBefore(request.dataInicial())
//                || request.primeiroPagamento().isAfter(request.dataFinal())) {
//            throw new IllegalArgumentException("O primeiro pagamento deve estar entre a data inicial e a data final.");
//        }
//        if (request.taxaJuros().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("A taxa de juros deve ser maior que zero.");
//        }
//    }
//
//    private BigDecimal converterTaxaMensal(BigDecimal taxaAnual) {
//        return taxaAnual.multiply(BigDecimal.valueOf(30))
//                .divide(BigDecimal.valueOf(BASE_DIAS_ANO), 10, RoundingMode.HALF_EVEN);
//    }
//
//    private BigDecimal calcularJurosDoPeriodo(BigDecimal saldo, BigDecimal taxaMensal) {
//        return saldo.multiply(taxaMensal).setScale(2, RoundingMode.HALF_EVEN);
//    }
//
//    private BigDecimal calcularSimulacao(BigDecimal valorPresente, BigDecimal taxaMensal, int numeroParcelas) {
//        BigDecimal fatorJuros = BigDecimal.ONE.add(taxaMensal);
//        BigDecimal desconto = BigDecimal.ONE.divide(fatorJuros.pow(numeroParcelas), 10, RoundingMode.HALF_EVEN);
//        BigDecimal denominador = BigDecimal.ONE.subtract(desconto);
//        return valorPresente.multiply(taxaMensal).divide(denominador, 2, RoundingMode.HALF_EVEN);
//    }
//
//    private List<LocalDate> gerarDatasDeParcela(LocalDate dataPrimeiroPagamento, LocalDate dataFinal) {
//        int datasConciliacao = YearMonth.from(dataFinal).compareTo(YearMonth.from(dataPrimeiroPagamento));
//        return IntStream.rangeClosed(0, datasConciliacao)
//                .mapToObj(datas -> {
//                    YearMonth referencia = YearMonth.from(dataPrimeiroPagamento).plusMonths(datas);
//                    int diaVencimento = Math.min(dataPrimeiroPagamento.getDayOfMonth(), referencia.lengthOfMonth());
//                    return LocalDate.of(referencia.getYear(), referencia.getMonth(), diaVencimento);
//                })
//                .filter(data -> !data.isAfter(dataFinal))
//                .collect(Collectors.toList());
//    }
//
//    private Set<LocalDate> gerarDatasParaExibicao(LocalDate dataInicial, LocalDate dataFinal, List<LocalDate> datasDeParcela) {
//        Set<LocalDate> datasExibicao = new TreeSet<>();
//        datasExibicao.add(dataInicial);
//        datasExibicao.add(dataFinal);
//
//        int datasConciliacao = YearMonth.from(dataFinal).compareTo(YearMonth.from(dataInicial));
//        IntStream.rangeClosed(0, datasConciliacao)
//                .mapToObj(datas -> dataInicial.plusMonths(datas).withDayOfMonth(dataInicial.plusMonths(datas).lengthOfMonth()))
//                .filter(data -> !data.isAfter(dataFinal))
//                .forEach(datasExibicao::add);
//
//        datasExibicao.addAll(datasDeParcela);
//        return datasExibicao;
//    }
//}


import com.izepon.calculadora_emprestimo.dto.SimulacaoRequest;
import com.izepon.calculadora_emprestimo.dto.SimulacaoResponse;
import com.izepon.calculadora_emprestimo.service.CalculadoraEmprestimoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class CalculadoraEmprestimoServiceImpl implements CalculadoraEmprestimoService {

    private static final int BASE_DIAS_ANO = 360;

    @Override
    public List<SimulacaoResponse> calcular(SimulacaoRequest request) {
        validarRegras(request);
        return gerarSimulacao(request);
    }

    private void validarRegras(SimulacaoRequest request) {
        if (!request.dataFinal().isAfter(request.dataInicial())) {
            throw new IllegalArgumentException("A data final deve ser maior que a data inicial.");
        }
        if (request.primeiroPagamento().isBefore(request.dataInicial())
                || request.primeiroPagamento().isAfter(request.dataFinal())) {
            throw new IllegalArgumentException("O primeiro pagamento deve estar entre a data inicial e a data final (inclusive).");
        }
        if (request.taxaJuros().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A taxa de juros deve ser maior que zero.");
        }
        if (request.valorEmprestimo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do empréstimo deve ser maior que zero.");
        }
    }

    private List<SimulacaoResponse> gerarSimulacao(SimulacaoRequest request) {
        List<LocalDate> datasDeParcela = gerarDatasDeParcela(request.primeiroPagamento(), request.dataFinal());
        Set<LocalDate> datasExibicaoSet = gerarDatasParaExibicao(request.dataInicial(), request.dataFinal(), datasDeParcela);
        List<LocalDate> datasExibicao = datasExibicaoSet.stream().sorted().toList();

        BigDecimal taxaJurosAnual = request.taxaJuros().divide(BigDecimal.valueOf(100), 20, RoundingMode.HALF_EVEN);

        int totalParcelas = datasDeParcela.size();

        BigDecimal valorAmortizacaoConstante = request.valorEmprestimo()
                .divide(BigDecimal.valueOf(totalParcelas), 2, RoundingMode.HALF_EVEN);

        BigDecimal saldoDevedor = request.valorEmprestimo();
        BigDecimal jurosAcumulados = BigDecimal.ZERO;
        LocalDate dataAnterior = request.dataInicial();
        int indiceParcela = 0;

        List<SimulacaoResponse> resultados = new ArrayList<>();

        for (LocalDate dataCompetencia : datasExibicao) {
            BigDecimal valorEmprestimoColuna = dataCompetencia.equals(request.dataInicial())
                    ? request.valorEmprestimo() : BigDecimal.ZERO;

            BigDecimal jurosDoPeriodoAtual;
            if (dataCompetencia.equals(request.dataInicial())) {
                jurosDoPeriodoAtual = BigDecimal.ZERO;
            } else {
                long diasNoPeriodo = ChronoUnit.DAYS.between(dataAnterior, dataCompetencia);

                BigDecimal fatorJurosExponencial = BigDecimal.valueOf(Math.pow(
                        (BigDecimal.ONE.add(taxaJurosAnual)).doubleValue(),
                        (double) diasNoPeriodo / BASE_DIAS_ANO
                ));

                jurosDoPeriodoAtual = fatorJurosExponencial.subtract(BigDecimal.ONE)
                        .multiply(saldoDevedor)
                        .setScale(2, RoundingMode.HALF_EVEN);
            }

            jurosAcumulados = jurosAcumulados.add(jurosDoPeriodoAtual);

            BigDecimal amortizacao = BigDecimal.ZERO;
            BigDecimal totalParcela = BigDecimal.ZERO;
            BigDecimal valorPago = BigDecimal.ZERO;
            String parcelaConsolidada = "";

            if (datasDeParcela.contains(dataCompetencia)) {
                indiceParcela++;
                parcelaConsolidada = indiceParcela + "/" + totalParcelas;
                amortizacao = valorAmortizacaoConstante;
                totalParcela = amortizacao.add(jurosAcumulados).setScale(2, RoundingMode.HALF_EVEN);
                valorPago = totalParcela;
                saldoDevedor = saldoDevedor.subtract(amortizacao);
                jurosAcumulados = BigDecimal.ZERO;
            }

            resultados.add(new SimulacaoResponse(
                    dataCompetencia,
                    valorEmprestimoColuna,
                    saldoDevedor.add(jurosAcumulados),
                    parcelaConsolidada,
                    totalParcela,
                    amortizacao,
                    saldoDevedor,
                    jurosDoPeriodoAtual,
                    jurosAcumulados,
                    valorPago
            ));

            dataAnterior = dataCompetencia;
        }
        return resultados;
    }

    private List<LocalDate> gerarDatasDeParcela(LocalDate dataPrimeiroPagamento, LocalDate dataFinal) {
        int mesesIntervalo = (int) YearMonth.from(dataPrimeiroPagamento)
                .until(YearMonth.from(dataFinal), ChronoUnit.MONTHS);

        List<LocalDate> datas = new ArrayList<>();

        for (int i = 0; i <= mesesIntervalo; i++) {
            YearMonth ym = YearMonth.from(dataPrimeiroPagamento).plusMonths(i);
            int diaVenc = Math.min(dataPrimeiroPagamento.getDayOfMonth(), ym.lengthOfMonth());
            LocalDate parcela = LocalDate.of(ym.getYear(), ym.getMonth(), diaVenc);
            if (!parcela.isAfter(dataFinal)) {
                datas.add(parcela);
            }
        }

        if (datas.isEmpty() || !datas.get(datas.size()-1).equals(dataFinal)) {
            datas.add(dataFinal);
        }

        return datas;
    }

    private Set<LocalDate> gerarDatasParaExibicao(LocalDate dataInicial, LocalDate dataFinal, List<LocalDate> datasDeParcela) {
        Set<LocalDate> datasExibicao = new TreeSet<>();
        datasExibicao.add(dataInicial);

        LocalDate dataIteracao = dataInicial;
        while (!dataIteracao.isAfter(dataFinal)) {
            datasExibicao.add(dataIteracao.withDayOfMonth(dataIteracao.lengthOfMonth()));
            dataIteracao = dataIteracao.plusMonths(1);
        }

        datasExibicao.add(dataFinal);

        datasExibicao.addAll(datasDeParcela);

        return datasExibicao.stream()
                .filter(data -> !data.isBefore(dataInicial) && !data.isAfter(dataFinal))
                .collect(Collectors.toCollection(TreeSet::new));
    }
}