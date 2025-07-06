
import CalcularEmprestimo from "@/components/CalcularEmprestimo";

const index = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50">
      <div className="container mx-auto px-4 py-8">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Calculadora de Empréstimos
          </h1>
          <p className="text-lg text-gray-600">
            Calcule as parcelas e acompanhe a evolução do seu empréstimo
          </p>
        </div>
        <CalcularEmprestimo />
      </div>
    </div>
  );
};

export default Index;
