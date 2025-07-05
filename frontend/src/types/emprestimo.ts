
export interface EmprestimoRequest {
  dataInicial: string;
  dataFinal: string;
  primeiroPagamento: string;
  valorEmprestimo: number;
  taxaJuros: number;
}

export interface EmprestimoResult {
  data: string;
  valorEmprestimo: number;
  saldoDevedor: number;
  consolidada: string;
  total: number;
  amortizacao: number;
  saldo: number;
  provisao: number;
  acumulado: number;
  pago: number;
}
