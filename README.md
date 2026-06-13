# English Dictionary App 

## Descrição
Aplicativo mobile nativo desenvolvido para consulta de vocabulário em inglês. O usuário digita uma palavra e o sistema consome uma API pública para retornar informações detalhadas, incluindo a transcrição fonética, a classe gramatical e múltiplas definições. O projeto foca em uma interface moderna utilizando **Material Design** e possui recursos avançados de usabilidade, como suporte bilíngue dinâmico e feedback visual de estado.

## API Utilizada
- **Nome da API:** Free Dictionary API (Projeto open-source)
- **Endpoint utilizado:** `https://api.dictionaryapi.dev/api/v2/entries/en/{word}`
- **Dados extraídos e exibidos:** - Nome da palavra consultada
  - Transcrição fonética (Ex: */wʊd/*)
  - Classe gramatical (*Part of Speech*)
  - Lista com as principais definições da palavra (limitado a 3 resultados estruturados)

## Funcionalidades Implementadas
- **Consumo de API Rest (GET):** Integração assíncrona utilizando a biblioteca Android Volley.
- **Tradução Dinâmica (State Management):** Botão de *toggle* (PT-BR / EN) que altera todo o texto da interface do aplicativo em tempo de execução via código Kotlin, sem necessidade de recarregar a Activity.
- **Feedback de Interface (UX/UI):** - Uso de componentes `TextInputLayout` com dicas flutuantes.
  - Bloqueio de múltiplos cliques no botão durante o tempo de requisição.
  - Alteração visual (cor e texto) do botão principal para indicar carregamento.
- **Tratamento de Exceções:** - Validação de campo de busca vazio com alertas (`Toast`).
  - Captura e tratamento de erros de rede (falta de conexão).
  - Tratamento da resposta HTTP 404 (Palavra não encontrada no dicionário), limpando a tela e exibindo mensagem amigável ao usuário.

## Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **IDE:** Android Studio
- **Layout:** XML (Componentes Google Material Design)
- **Requisições HTTP:** Biblioteca Android Volley (`JsonArrayRequest`)
- **Manipulação de Dados:** JSON (Parse de Arrays e Objects)

## Como executar o projeto
1. Clone este repositório na sua máquina local:
   `git clone https://github.com/IanCorrea19/atividade-api-mobileDicionario-IanVieira.git`
2. Abra o diretório do projeto no **Android Studio**.
3. Aguarde a sincronização automática do *Gradle*.
4. Verifique se o emulador está rodando ou conecte um dispositivo físico.
   > **Dica de Solução de Problemas:** Caso o emulador apresente tela preta persistente ou congelamento, acesse a aba **Device Manager** no menu lateral direito, clique nos três pontos verticais ao lado do seu dispositivo virtual e selecione **Cold Boot Now**.
5. Clique no botão de **Run 'app'** (Play) na barra superior.
6. Teste buscando palavras em inglês como *engineer*, *set* ou *mobile*.



---
**Autor:** Ian Vieira Corrêa -> Matrícula: 2589931


