package org.example.demo.service;


import org.example.demo.model.Player;
import org.example.demo.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Rules {

    public static int compareTruco(Card c1, Card c2, Card vira) {

        boolean m1 = c1.isManilha(vira);
        boolean m2 = c2.isManilha(vira);

        int p1 = c1.trucoPower();
        int p2 = c2.trucoPower();

        if (m1 && !m2) return 1;
        if (m2 && !m1) return -1;

        if (p1 > p2) return 1;
        if (p2 > p1) return -1;

        return Integer.compare(c1.naipePower(), c2.naipePower());
    }
    public static Comparator<Card> trucoComparator(Card vira) {
        return (c1, c2) -> compareTruco(c1, c2, vira);
    }

    public static Card winner(List<Card> cardsOnTable, Card vira) {
        return cardsOnTable.stream()
                .max(trucoComparator(vira))
                .orElseThrow();
    }

    public static List<Card> dealer(List<Card> deck, int roundCount, List<Player> players){
        int cardNumber = (players.size() * roundCount) + 1;
        List<Card> dealerCards = new ArrayList<>();
        Collections.shuffle(deck);
        for(int i = 0; i < cardNumber; i++){
            dealerCards.add(deck.remove(0));
        }
        return dealerCards;
    }

    public static Card dealCards(int roundCount, List<Player> players) {

        int index = 0;
        List<Card> truco = Card.getTrucoDeck();
        if (((roundCount * players.size()) + 1 )  < truco.size()) {
            List<Card> cardsOnTable = Rules.dealer(truco, roundCount, players);
            Card vira = cardsOnTable.remove(0);

            for (Player player : players) {
                for (int j = 0; j < roundCount; j++) {
                    player.setHand(cardsOnTable.get(index++));
                }
            }
            return vira;
        }
        return null;
    }



    public static void updateScore(List<Player> players) {
        for (Player player : players) {
            if (player.getRoundScore() == player.getGuess()){
                player.setScore(player.getScore() + player.getGuess() + 10);
            }
            player.setRoundScore(0);
        }
    }




}