# Calculadora de Empréstimo

Este projeto implementa uma API de cálculo de parcelas de empréstimo (backend em Spring Boot) e uma interface web em React (frontend) para consumir essa API.

---

## 📋 Pré-requisitos

* **Java 17** (ou superior)
* **Maven** (incluído via wrapper Maven no projeto)
* **Node.js** v20+
* **npm** (ou **yarn**)
* **Postman** (opcional, para testes de API)

---

## 🔧 Backend

1. **Entrar na pasta do backend**

   ```bash
   cd backend
   ```

2. **Build & Run**

    * Com o Maven instalado globalmente:

      ```bash
      mvn clean spring-boot:run
      ```
    * Ou, usando o wrapper Maven:

      ```bash
      ./mvnw spring-boot:run    # Linux/macOS
      mvnw.cmd spring-boot:run  # Windows PowerShell
      ```

   O servidor sobe em `http://localhost:8080`.

3. **Endpoints principais**

    * **POST /api/calculadora-emprestimo/calcular**
      Recebe um JSON com os campos:

      ```json
      {
        "dataInicial":      "YYYY-MM-DD",
        "dataFinal":        "YYYY-MM-DD",
        "primeiroPagamento":"YYYY-MM-DD",
        "valorEmprestimo":  10000.00,
        "taxaJuros":        0.02
      }
      ```

      Retorna uma lista de parcelas com datas e valores calculados.

---

## ⚛️ Frontend

1. **Entrar na pasta do frontend**

   ```bash
   cd frontend
   ```

2. **Instalar dependências**

   ```bash
   npm install
   # ou
   yarn
   ```

3. **Rodar em modo desenvolvimento**

   ```bash
   npm run dev
   # ou
   yarn dev
   ```

   Abrirá em [http://localhost:3000](http://localhost:3000) por padrão.
   A aplicação espera o backend rodando em `http://localhost:8080`.

4. **Build para produção**

   ```bash
   npm run build
   # ou
   yarn build
   ```

   Os arquivos gerados ficarão em `dist/` e podem ser servidos por qualquer servidor estático.

---

## 🧪 Testes

* **Backend**:

  ```bash
  cd backend
  mvn test
  ```

  – inclui testes unitários para datas de parcelamento e cenários de ano bissexto.

---

## 🚀 Testando com Postman

1. Abra o Postman e importe a collection baixando aqui **[Calculadora Emprestimo API.postman\_collection.json](https://github.com/user-attachments/files/21083972/Calculadora.Emprestimo.API.postman_collection.json)** 
2. Certifique-se de que a URL base (`http://localhost:8080`) esteja correta.
3. Execute a requisição **Calcular Empréstimo - Válido** para ver um exemplo.

---

## 🗂️ Estrutura do Projeto

```
calculadora-emprestimos/
├── backend/                  # Spring Boot
│   ├── src/main/java/com/calculadora-emprestimo/
│   │   ├── config/
│   │   ├── controller/       
│   │   ├── dto/          
│   │   ├── service/                       
│   │   └── CalculadoraEmprestimosApplication.java
│   ├── resources/
│   │   ├── application.properties
│   │   └── ...
│   ├── src/test/java/com/calculadora-emprestimo/
│   │   ├── controller/
│   └── └── service/ 
├── frontend/                 #React + Vite
│   ├── public/
│   ├── src/
│   │   ├── components/       
│   │   ├── hooks/ 
│   │   ├── libs/      
│   │   ├── Pages/     
│   │   ├── services/         
│   │   ├── types/            
│   │   └── App.jsx
├── README.md
└── .gitignore

```

## 🗂️ Funcionalidades do Projeto

–  O design da tela é simples e intuitivo, exibindo os campos de entrada do formulário. Os campos obrigatórios são destacados, e o botão Calcular Empréstimo só fica habilitado quando todos estão preenchidos corretamente.
-  Após o cálculo, os valores da simulação são exibidos na tela e, em seguida, é possível baixar a projeção em PDF.

![image](https://github.com/user-attachments/assets/7ea20db1-007e-47ad-907e-453f5ddfa36e)
![image](https://github.com/user-attachments/assets/7a029265-112d-4873-bb47-f37052782630)


---

## 📖 Recursos

* [Spring Boot Docs](https://spring.io/projects/spring-boot)
* [Vite + React](https://vitejs.dev/guide/)
* [Postman Collections](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/)

---

© [2025] [Jean Carlos Izepon]. Todos os direitos reservados.

Distribuído sob a Licença MIT. Consulte o arquivo LICENSE para mais detalhes.
