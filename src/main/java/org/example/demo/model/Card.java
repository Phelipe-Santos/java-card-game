package org.example.demo.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record Card(Suit suit, String face, int rank) {

    public enum Suit {
        CLUB, DIAMOND, HEART, SPADE;


        public char getImage() {
            return (new char[]{9827, 9830, 9829, 9824})[this.ordinal()];
        }
    }

    public int trucoPower() {
        return switch (face) {
            case "4" -> 0;
            case "5" -> 1;
            case "6" -> 2;
            case "7" -> 3;
            case "Q" -> 4;
            case "J" -> 5;
            case "K" -> 6;
            case "A" -> 7;
            case "2" -> 8;
            case "3" -> 9;
            default -> -1;
        };
    }
    public int naipePower() {
        return switch (suit){
            case DIAMOND -> 0;
            case SPADE -> 1;
            case HEART -> 2;
            case CLUB -> 3;
        };
    }
    public boolean isManilha(Card vira){
        return this.trucoPower() == ((vira.trucoPower() + 1) % 10);
    }

    @Override
    public String toString() {

        int index = face.equals("10") ? 2 : 1;
        String faceString = face.substring(0, index);
        return "%s%c".formatted(faceString, suit.getImage());

    }

    public static Card getNumericCard(Suit suit, int cardNumber) {

        if (cardNumber > 1 && cardNumber < 11) {
            return new Card(suit, String.valueOf(cardNumber), cardNumber - 2);
        }
        System.out.println("Carta invalida");
        return null;
    }

    public static Card getFaceCard(Suit suit, char abbrev) {

        int charIndex = "JQKA".indexOf(abbrev);
        if (charIndex > -1) {
            return new Card(suit, "" + abbrev, charIndex + 9);
        }
        System.out.println("Carta invalida");
        return null;
    }

    public static List<Card> getStandardDeck() {

        List<Card> deck = new ArrayList<>(52);
        for (Suit suit : Suit.values()) {
            for (int i = 2; i <= 10; i++) {
                deck.add(getNumericCard(suit, i));
            }
            for (char c : new char[]{'J', 'Q', 'K', 'A'}) {
                deck.add(getFaceCard(suit, c));
            }
        }
        return deck;
    }

    public static List<Card> getTrucoDeck() {
        List<Card> truco = new ArrayList<>(Card.getStandardDeck());
        truco.removeIf(card -> card.rank() > 5 && card.rank() < 9);

        // ordena pela força do TRUCO: 4 fraca → 3 forte
        truco.sort(Comparator.comparingInt(Card::trucoPower));
        return truco;
    }

    public String imageFace() {
        return switch (face) {
            case "A" -> "ace";
            case "K" -> "king";
            case "Q" -> "queen";
            case "J" -> "jack";
            default -> face;
        };
    }
    public String getImagePath() {
        return "/cards/"
                + imageFace()
                + "_of_"
                + suit.toString().toLowerCase()
                + "s.png";
    }

}
