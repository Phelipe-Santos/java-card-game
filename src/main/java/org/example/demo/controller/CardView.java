package org.example.demo.controller;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CardView extends StackPane {

    public static final double WIDTH = 55;
    public static final double HEIGHT = 75;

    private final ImageView imageView;

    private static final String BACK_IMAGE = "/cards/back.png";

    public CardView() {

        setPrefSize(WIDTH, HEIGHT);
        setMinSize(WIDTH, HEIGHT);
        setMaxSize(WIDTH, HEIGHT);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(6);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.45));

        setEffect(shadow);

        Rectangle fundo = new Rectangle(WIDTH, HEIGHT);
        fundo.setArcWidth(8);
        fundo.setArcHeight(8);
        fundo.setStyle("-fx-fill: white;");

        imageView = new ImageView();
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        imageView.setPreserveRatio(false);

        getChildren().addAll(fundo, imageView);

        // começa invisível (slot da mesa)
        setVisible(false);
    }

    public void showFront(String path) {
        setImageView(path);
        setVisible(true);
    }

    public void showBack() {
        setImageView(BACK_IMAGE);
        setVisible(true);
    }

    private void setImageView(String path) {
        var url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("Imagem não encontrada: " + path);
        }
        imageView.setImage(new Image(url.toExternalForm()));
    }

}
