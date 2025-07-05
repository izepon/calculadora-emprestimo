
import { useState } from "react";
import { Card } from "@/components/ui/card";
import Form from "./Form";
import ResultsTable from "./Resultados";
import { EmprestimoRequest, EmprestimoResult } from "@/types/emprestimo";
import { calculate } from "@/services/emprestimoService";
import { useToast } from "@/hooks/use-toast";

const CalcularEmprestimo = () => {
  const [results, setResults] = useState<EmprestimoResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();

  const handleCalculate = async (formData: EmprestimoRequest) => {
    setIsLoading(true);
    console.log("Iniciando cálculo com dados:", formData);
    
    try {
      const calculationResults = await calculate(formData);
      setResults(calculationResults);
      
      toast({
        title: "Cálculo realizado com sucesso!",
        description: `${calculationResults.length} registros calculados`,
      });
    } catch (error) {
      console.error("Erro no cálculo:", error);
      toast({
        title: "Erro no cálculo",
        description: "Não foi possível calcular o empréstimo. Verifique os dados e tente novamente.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      <Card className="p-6 shadow-lg border-0 bg-white/80 backdrop-blur-sm">
        <Form onCalculate={handleCalculate} isLoading={isLoading} results={results} />
      </Card>
      
      {results.length > 0 && (
        <Card className="p-6 shadow-lg border-0 bg-white/80 backdrop-blur-sm">
          <ResultsTable results={results} />
        </Card>
      )}
    </div>
  );
};

export default CalcularEmprestimo;
