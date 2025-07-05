
import { EmprestimoRequest, EmprestimoResult } from "@/types/emprestimo";

const API_BASE_URL = "http://localhost:8080";

export const calculate = async (request: EmprestimoRequest): Promise<EmprestimoResult[]> => {
  console.log("Enviando requisição para API:", request);
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/calculadora-emprestimo/calcular`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    });

    console.log("Status da resposta:", response.status);

    if (!response.ok) {
      throw new Error(`Erro na API: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    console.log("Dados recebidos da API:", data);
    
    return data;
  } catch (error) {
    console.error("Erro ao chamar API:", error);
    throw error;
  }
};
