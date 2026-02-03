# 🃏 Java Card Game

Jogo de cartas desenvolvido em **Java**, com interface gráfica e lógica de jogo orientada a objetos.  
O projeto implementa regras completas de jogo, sistema de palpites, controle de rodadas e suporte a jogadores humanos e bots.

Projeto criado com foco em praticar **lógica de programação**, **programação orientada a objetos**, **manipulação de listas**, uso de **Collections**, **lambdas**, **Comparator**, resolução de problemas e desenvolvimento de **interface gráfica**.  
Inclui também lógica para que os bots analisem o estado atual da mesa e escolham quais cartas jogar de forma estratégica.

---

## 📜 Regras do Jogo

O jogo utiliza o sistema de **força das cartas do Truco** (jogo popular em São Paulo e no sul do Brasil), com algumas adaptações para torná-lo único.

- A cada rodada, os jogadores recebem cartas em quantidade crescente:
    - Rodada 1: 1 carta
    - Rodada 2: 2 cartas
    - E assim por diante
- Após receberem as cartas, os jogadores fazem **palpites** de quantas vazadas (mãos) acreditam que irão ganhar.
- Se o jogador ganhar **exatamente** o número de vazadas que palpitou:
    - Recebe **10 pontos + número de vazadas vencidas**
- Caso não cumpra o palpite:
    - Não recebe pontos
- O jogo termina quando não há cartas suficientes para iniciar uma nova rodada.
- Vence o jogador com **maior pontuação final**.

---

## 🎮 Funcionalidades

- Sistema de jogo por rodadas
- Jogador humano e jogadores bots
- Fase de palpites com validações
- Controle de turnos
- Sistema de pontuação
- Interface gráfica
- Lógica de jogo desacoplada da UI

---

## 🛠️ Tecnologias Utilizadas

- Java
- JavaFX
- Programação Orientada a Objetos (POO)
- MVC (separação entre lógica e interface)
- Git & GitHub

---

## 📂 Estrutura do Projeto

```text
src/
 ├── controller/   # Controllers da interface
 ├── model/        # Entidades do jogo (Player, Card, etc.)
 ├── service/      # Engine e regras do jogo
 └── view/         # Arquivos de interface (FXML)
