package org.example.demo.model;

import org.example.demo.service.Rules;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> hand = new ArrayList<>();
    public static final int GUESS_NOT_MADE = -1;

    private final String name;
    private int score;
    private int guess = GUESS_NOT_MADE;
    private int roundScore;
    private boolean isHuman;

    public Player(String playerName) {
        this.name = playerName;
        this.score = 0;
        this.isHuman = false;
    }
    public Player(String playerName, boolean isHuman) {
        this.name = playerName;
        this.score = 0;
        this.isHuman = isHuman;
    }

    public List<Card> getHand() {
        return hand;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setHand(Card card) {
        this.hand.add(card);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore = roundScore;
    }

    public void resetGuess() {
        this.guess = GUESS_NOT_MADE;
    }

    public int getGuess() {
        return guess;
    }

    public Card getWeakestCard(Card vira) {
            return this.hand.stream()
                    .min(Rules.trucoComparator(vira))
                    .orElseThrow();
    }

    public Card getBestCard(Card vira) {
        return this.hand.stream()
                .max(Rules.trucoComparator(vira))
                .orElseThrow();
    }


    @Override
    public String toString() {
        return "Jogador: " + name + '\'' +
                " Pontuação: " + score;
    }

    public void setGuess(int guess) {
        this.guess = guess;
    }
}