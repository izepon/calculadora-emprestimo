import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Calculator, Calendar, DollarSign, Percent, FileText } from "lucide-react";
import { LoanRequest } from "@/types/emprestimo";
import jsPDF from "jspdf";

interface FormProps {
  onCalculate: (data: LoanRequest) => void;
  isLoading: boolean;
  results?: any[];
}

const Form = ({ onCalculate, isLoading, results = [] }: FormProps) => {
  const [formData, setFormData] = useState({
    dataInicial: "",
    dataFinal: "",
    primeiroPagamento: "",
    valorEmprestimo: "",
    taxaJuros: "",
  });

  const [isValid, setIsValid] = useState(false);

  useEffect(() => {
    const { dataInicial, dataFinal, primeiroPagamento, valorEmprestimo, taxaJuros } = formData;

    const allFieldsFilled = dataInicial && dataFinal && primeiroPagamento && valorEmprestimo && taxaJuros;
    const validNumbers = parseFloat(valorEmprestimo) > 0 && parseFloat(taxaJuros) > 0;
    const validDates =
      new Date(dataInicial) < new Date(dataFinal) &&
      new Date(primeiroPagamento) >= new Date(dataInicial) &&
      new Date(primeiroPagamento) <= new Date(dataFinal);

    setIsValid(Boolean(allFieldsFilled && validNumbers && validDates));
  }, [formData]);

  const handleInputChange = (field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!isValid) return;

    const requestData: LoanRequest = {
      dataInicial: formData.dataInicial,
      dataFinal: formData.dataFinal,
      primeiroPagamento: formData.primeiroPagamento,
      valorEmprestimo: parseFloat(formData.valorEmprestimo),
      taxaJuros: parseFloat(formData.taxaJuros),
    };

    onCalculate(requestData);
  };

  const downloadPDF = () => {
    try {
      const doc = new jsPDF();

      doc.setFontSize(20);
      doc.text("Relatório de Empréstimo", 20, 30);

      doc.setFontSize(12);
      doc.text(`Data Inicial: ${formData.dataInicial}`, 20, 50);
      doc.text(`Data Final: ${formData.dataFinal}`, 20, 60);
      doc.text(`Primeiro Pagamento: ${formData.primeiroPagamento}`, 20, 70);
      doc.text(`Valor do Empréstimo: R$ ${formData.valorEmprestimo}`, 20, 80);
      doc.text(`Taxa de Juros: ${formData.taxaJuros}%`, 20, 90);

      doc.setFontSize(10);
      let yPosition = 110;
      doc.text("Data", 20, yPosition);
      doc.text("Saldo Dev.", 50, yPosition);
      doc.text("Parcela", 80, yPosition);
      doc.text("Total", 110, yPosition);
      doc.text("Amortização", 140, yPosition);
      doc.text("Pago", 170, yPosition);

      results.forEach((result, index) => {
        yPosition += 10;
        if (yPosition > 280) {
          doc.addPage();
          yPosition = 30;
        }

        const date = new Date(result.data + "T00:00:00").toLocaleDateString("pt-BR");
        doc.text(date, 20, yPosition);
        doc.text(`R$ ${result.saldoDevedor.toFixed(2)}`, 50, yPosition);
        doc.text(result.consolidada || "-", 80, yPosition);
        doc.text(`R$ ${result.total.toFixed(2)}`, 110, yPosition);
        doc.text(`R$ ${result.amortizacao.toFixed(2)}`, 140, yPosition);
        doc.text(`R$ ${result.pago.toFixed(2)}`, 170, yPosition);
      });

      const totalPago = results.reduce((sum, r) => sum + r.pago, 0);
      yPosition += 20;
      doc.setFontSize(12);
      doc.text(`Total Pago: R$ ${totalPago.toFixed(2)}`, 20, yPosition);

      doc.save("relatorio-emprestimo.pdf");
    } catch (error) {
      console.error("Erro ao gerar PDF:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="text-center mb-6">
        <h2 className="text-2xl font-semibold text-gray-800 mb-2">Dados do Empréstimo</h2>
        <p className="text-gray-600">Preencha todos os campos para calcular as parcelas</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4">
        <div className="space-y-2">
          <Label htmlFor="dataInicial" className="flex items-center gap-2 text-gray-700">
            <Calendar className="h-4 w-4" />
            Data Inicial *
          </Label>
          <Input
            id="dataInicial"
            type="date"
            value={formData.dataInicial}
            onChange={(e) => handleInputChange("dataInicial", e.target.value)}
            className="transition-all duration-200 focus:scale-105"
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="dataFinal" className="flex items-center gap-2 text-gray-700">
            <Calendar className="h-4 w-4" />
            Data Final *
          </Label>
          <Input
            id="dataFinal"
            type="date"
            value={formData.dataFinal}
            onChange={(e) => handleInputChange("dataFinal", e.target.value)}
            className="transition-all duration-200 focus:scale-105"
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="primeiroPagamento" className="flex items-center gap-2 text-gray-700">
            <Calendar className="h-4 w-4" />
            Primeiro Pagamento *
          </Label>
          <Input
            id="primeiroPagamento"
            type="date"
            value={formData.primeiroPagamento}
            onChange={(e) => handleInputChange("primeiroPagamento", e.target.value)}
            className="transition-all duration-200 focus:scale-105"
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="valorEmprestimo" className="flex items-center gap-2 text-gray-700">
            <DollarSign className="h-4 w-4" />
            Valor do Empréstimo *
          </Label>
          <Input
            id="valorEmprestimo"
            type="number"
            step="0.01"
            min="0"
            placeholder="0,00"
            value={formData.valorEmprestimo}
            onChange={(e) => handleInputChange("valorEmprestimo", e.target.value)}
            className="transition-all duration-200 focus:scale-105"
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="taxaJuros" className="flex items-center gap-2 text-gray-700">
            <Percent className="h-4 w-4" />
            Taxa de Juros (%) *
          </Label>
          <Input
            id="taxaJuros"
            type="number"
            step="0.01"
            min="0"
            placeholder="0,00"
            value={formData.taxaJuros}
            onChange={(e) => handleInputChange("taxaJuros", e.target.value)}
            className="transition-all duration-200 focus:scale-105"
            required
          />
        </div>
      </div>

      <div className="pt-4 flex justify-end items-center">
        <Button
          type="submit"
          disabled={!isValid || isLoading}
          className="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-semibold py-3 px-8 rounded-lg shadow-lg transform transition-all duration-200 hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
        >
          {isLoading ? (
            <>
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
              Calculando...
            </>
          ) : (
            <>
              <Calculator className="h-4 w-4 mr-2" />
              Calcular Empréstimo
            </>
          )}
        </Button>
      </div>

      {/* Botão Download PDF abaixo */}
      {results.length > 0 && (
        <div className="flex justify-end pt-2">
          <Button
            type="button"
            onClick={downloadPDF}
            variant="outline"
            className="flex items-center gap-2"
          >
            <FileText className="h-4 w-4" />
            Download PDF
          </Button>
        </div>
      )}
    </form>
  );
};

export default Form;
