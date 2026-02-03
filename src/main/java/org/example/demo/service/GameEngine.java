package org.example.demo.service;
import org.example.demo.model.Card;
import org.example.demo.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {

    private final List<Player> players;
    private final List<Card> cardsOnTable = new ArrayList<>();
    private final List<Player> playersOnTable = new ArrayList<>();
    private Card vira;
    private int roundCount = 1;
    private int indexGuess=0;

    public GameEngine(List<Player> players) {
        this.players = players;
    }


    public boolean startRound() {

        for (Player p : players) {
            p.getHand().clear();
            p.setRoundScore(0);
            p.resetGuess();
        }

        vira = Rules.dealCards(roundCount, players);
        return vira != null;
    }

    public void endRound() {
        roundCount++;
        indexGuess = (indexGuess + 1) % players.size();
    }



    public void playCard(Player player, Card card) {
        player.getHand().remove(card);
        cardsOnTable.add(card);
        playersOnTable.add(player);
    }

    public Player resolveMao() {

        Card vencedora = Rules.winner(cardsOnTable, vira);
        int index = cardsOnTable.indexOf(vencedora);
        Player vencedor = playersOnTable.get(index);

        vencedor.setRoundScore(
                vencedor.getRoundScore() + 1
        );

        Collections.rotate(players, -players.indexOf(vencedor));

        cardsOnTable.clear();
        playersOnTable.clear();

        return vencedor;
    }

    public boolean roundFinished() {
        return players.stream().allMatch(p -> p.getHand().isEmpty());
    }

    public Card getVira() {
        return vira;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public void makeGuesses(Player bot, int positionTable, int somaPalpites) {

        int totalCartas = roundCount;

        int palpite = calculateAIGuess(bot, totalCartas, somaPalpites);

        if (positionTable == players.size() - 1 &&
                somaPalpites + palpite == totalCartas) {

            if (palpite > 0) palpite--;
            else palpite++;
        }

        bot.setGuess(palpite);
    }


    public int calculateAIGuess(Player bot, int round, int sumGuess) {

        int guess = 0;

        for (Card card : bot.getHand()) {

            if (card.isManilha(vira)) {
                guess++;
            } else if (round < 6) {

                if (sumGuess < round && card.trucoPower() > 6) {
                    guess++;
                }
                if (sumGuess > round && card.trucoPower() > 8) {
                    guess++;
                }

            } else {

                if (sumGuess < round && card.trucoPower() > 7) {
                    guess++;
                }
                if (sumGuess > round && card.trucoPower() > 8) {
                    guess++;
                }
            }
        }

        return guess;
    }


    public Card playIaCard(Player bot) {
        Card bestIaCard = bot.getBestCard(vira);
        Card weakIaCard = bot.getWeakestCard(vira);
        Card bestTableCard = getBestTableCard();
        //Se a IA precisa fazer ponto joga a melhor carta
        if (bestTableCard == null){
            return weakIaCard;
        }
        if (bot.getGuess() < bot.getRoundScore()){
            if (bestIaCard.trucoPower() > bestTableCard.trucoPower()) {
                return bestIaCard;
            }
            else return weakIaCard;
        }//se a IA não precisa fazer ponto ela vai tentar descartar a maior carta da mão, se não for possível joga a mais fraca
        else {
            if (bestIaCard.trucoPower() < bestTableCard.trucoPower()) {
                return bestIaCard;
            }
            else return weakIaCard;
        }
    }

    public Card getBestTableCard() {
        if (cardsOnTable.isEmpty()) {
            return null;
        }

        return cardsOnTable.stream()
                .max(Rules.trucoComparator(vira))
                .orElse(null);
    }

}
