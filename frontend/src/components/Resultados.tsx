
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { emprestimoResult } from "@/types/emprestimo";

interface ResultsTableProps {
  results: EmprestimoResult[];
}

const ResultsTable = ({ results }: ResultsTableProps) => {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(value);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString + "T00:00:00").toLocaleDateString("pt-BR");
  };

  const isParcela = (consolidada: string) => consolidada && consolidada.trim() !== "";

  return (
    <div className="space-y-4">
      <div className="text-center">
        <h3 className="text-2xl font-semibold text-gray-800 mb-2">
          Resultados do Cálculo
        </h3>
        <p className="text-gray-600">
          Evolução do empréstimo ao longo do período
        </p>
      </div>

      <div className="overflow-x-auto rounded-lg border border-gray-200">
        <Table>
          <TableHeader className="bg-gradient-to-r from-blue-50 to-indigo-50">
            <TableRow>
              <TableHead className="font-semibold text-gray-700">Data</TableHead>
              <TableHead className="font-semibold text-gray-700">Valor Empréstimo</TableHead>
              <TableHead className="font-semibold text-gray-700">Saldo Devedor</TableHead>
              <TableHead className="font-semibold text-gray-700">Parcela</TableHead>
              <TableHead className="font-semibold text-gray-700">Total</TableHead>
              <TableHead className="font-semibold text-gray-700">Amortização</TableHead>
              <TableHead className="font-semibold text-gray-700">Saldo</TableHead>
              <TableHead className="font-semibold text-gray-700">Provisão</TableHead>
              <TableHead className="font-semibold text-gray-700">Acumulado</TableHead>
              <TableHead className="font-semibold text-gray-700">Pago</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {results.map((result, index) => (
              <TableRow
                key={index}
                className={`
                  transition-colors duration-200 hover:bg-gray-50
                  ${isParcela(result.consolidada) ? "bg-blue-50/50 border-l-4 border-l-blue-500" : ""}
                `}
              >
                <TableCell className="font-medium">
                  {formatDate(result.data)}
                </TableCell>
                <TableCell>
                  {result.valorEmprestimo > 0 ? (
                    <span className="font-semibold text-green-600">
                      {formatCurrency(result.valorEmprestimo)}
                    </span>
                  ) : (
                    "-"
                  )}
                </TableCell>
                <TableCell>
                  <span className={result.saldoDevedor > 0 ? "text-red-600" : "text-green-600"}>
                    {formatCurrency(result.saldoDevedor)}
                  </span>
                </TableCell>
                <TableCell>
                  {isParcela(result.consolidada) ? (
                    <Badge variant="secondary" className="bg-blue-100 text-blue-800">
                      {result.consolidada}
                    </Badge>
                  ) : (
                    "-"
                  )}
                </TableCell>
                <TableCell>
                  {result.total > 0 ? (
                    <span className="font-semibold text-blue-600">
                      {formatCurrency(result.total)}
                    </span>
                  ) : (
                    "-"
                  )}
                </TableCell>
                <TableCell>
                  {result.amortizacao > 0 ? formatCurrency(result.amortizacao) : "-"}
                </TableCell>
                <TableCell>
                  {formatCurrency(result.saldo)}
                </TableCell>
                <TableCell>
                  {result.provisao > 0 ? (
                    <span className="text-orange-600">
                      {formatCurrency(result.provisao)}
                    </span>
                  ) : (
                    "-"
                  )}
                </TableCell>
                <TableCell>
                  {result.acumulado > 0 ? (
                    <span className="text-orange-600">
                      {formatCurrency(result.acumulado)}
                    </span>
                  ) : (
                    "-"
                  )}
                </TableCell>
                <TableCell>
                  {result.pago > 0 ? (
                    <span className="font-semibold text-green-600">
                      {formatCurrency(result.pago)}
                    </span>
                  ) : (
                    "-"
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <div className="mt-4 p-4 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-center">
          <div>
            <p className="text-sm text-gray-600">Total de Registros</p>
            <p className="text-xl font-bold text-gray-800">{results.length}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Parcelas</p>
            <p className="text-xl font-bold text-blue-600">
              {results.filter(r => isParcela(r.consolidada)).length}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Total Pago</p>
            <p className="text-xl font-bold text-green-600">
              {formatCurrency(results.reduce((sum, r) => sum + r.pago, 0))}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResultsTable;
