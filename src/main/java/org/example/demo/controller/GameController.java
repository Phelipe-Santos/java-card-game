package org.example.demo.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demo.model.Player;
import org.example.demo.service.Rules;
import org.example.demo.model.Card;
import org.example.demo.service.GameEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class GameController {

    private GameEngine engine;
    private boolean humanCanPlay = false;
    private int playIndex = 0;

    private Stage forcaCartaStage;
    @FXML
    private javafx.scene.control.Button confirmarPalpiteBtn;

    @FXML private ChoiceBox<Integer> palpiteChoice;
    @FXML private HBox maoJogador;
    @FXML private GridPane placarGrid;

    @FXML private Label eventoLabel;

    @FXML private VBox maoBotEsquerda;
    @FXML private HBox maoBotCima;
    @FXML private VBox maoBotDireita;

    @FXML private HBox mesaCentral;
    @FXML private HBox viraBox;

    @FXML
    private Button toggleForcaCartaBtn;



    private int indexGuess = 0;
    private Player phelipe;
    private Player bot1;
    private Player bot2;
    private Player bot3;
    public List<Player> tableGuessOrder = new ArrayList<>();
    private final List<CardView> maoBotEsquerdaViews = new ArrayList<>();
    private final List<CardView> maoBotCimaViews = new ArrayList<>();
    private final List<CardView> maoBotDireitaViews = new ArrayList<>();



    @FXML
    public void initialize() {

        confirmarPalpiteBtn.setDisable(true);
        eventoLabel.setWrapText(true);

        phelipe = new Player("Phelipe", true);
        bot1  = new Player("Bot1");
        bot2  = new Player("Bot2");
        bot3  = new Player("Bot3");

        engine = new GameEngine(
                new ArrayList<>(List.of(phelipe, bot1, bot2, bot3))
        );
        tableGuessOrder.addAll(List.of(phelipe, bot1, bot2, bot3));
        engine.startRound();
        Card vira = engine.getVira();

        if (vira == null) {
            endGameMenu();
            return;
        }

        showVira(engine.getVira());
        indexGuess = 0;
        executeGuess();
        updateScore();
        loadAllHands();
    }


    private void startTurn() {
        playIndex = 0;
        executePlay();
    }

    private void executePlay() {

        if (playIndex >= engine.getPlayers().size()) {
            resolverVazada();
            return;
        }

        Player atual = engine.getPlayers().get(playIndex);

        if (atual.isHuman()) {
            humanCanPlay = true;
            eventoLabel.setText("Sua vez");
            return;
        }

        humanCanPlay = false;

        Card card = engine.playIaCard(atual);

        engine.playCard(atual, card);

        removeBotVisualCard(atual);

        playIndex++;
        putCardOnTable(card);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.3));
        pause.setOnFinished(e -> executePlay());
        pause.play();
    }

    private void playHumanCard(CardView view) {

        if (!humanCanPlay) return;

        humanCanPlay = false;

        Card card = (Card) view.getUserData();
        engine.playCard(phelipe, card);

        maoJogador.getChildren().remove(view);
        putCardOnTable(card);

        playIndex++;
        executePlay();
    }

    private void resolverVazada() {

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {

            Player vencedor = engine.resolveMao();
            eventoLabel.setText("Vencedor da mão: " + vencedor.getName());

            updateScore();
            cleanTable();

            if (engine.roundFinished()) {
                Rules.updateScore(engine.getPlayers());


                engine.endRound();

                boolean started = engine.startRound();
                updateScore();

                if (!started || engine.getVira() == null) {
                    endGameMenu();
                    return;
                }
                showVira(engine.getVira());
                loadAllHands();
                indexGuess = 0;
                executeGuess();

                return;
            }


            loadAllHands();
            startTurn();

        });

        pause.play();
    }

    private int sumGuessesSoFar() {
        int sum = 0;
        for (int i = 0; i < indexGuess; i++) {
            Player p = tableGuessOrder.get(i);
            sum += p.getGuess();
        }
        return sum;
    }


    private void loadAllHands() {
        loadHumanHand();
        loadBotHand(bot1, maoBotEsquerda, maoBotEsquerdaViews, -90);
        loadBotHand(bot2, maoBotCima, maoBotCimaViews, 0);
        loadBotHand(bot3, maoBotDireita, maoBotDireitaViews, 90);
    }

    private void loadHumanHand() {
        maoJogador.getChildren().clear();

        for (Card card : phelipe.getHand()) {
            CardView view = new CardView();
            view.showFront(card.getImagePath());
            view.setUserData(card);
            view.setOnMouseClicked(e -> playHumanCard(view));
            maoJogador.getChildren().add(view);
        }
    }

    private void loadBotHand(
            Player bot,
            Pane container,
            List<CardView> views,
            double rotacao
    ) {
        container.getChildren().clear();
        views.clear();

        for (int i = 0; i < bot.getHand().size(); i++) {
            CardView verso = new CardView();
            verso.showBack();
            verso.setRotate(rotacao);
            views.add(verso);
            container.getChildren().add(verso);
        }
    }

    private void putCardOnTable(Card carta) {
        CardView view = new CardView();
        view.showFront(carta.getImagePath());
        mesaCentral.getChildren().add(view);
    }

    private void cleanTable() {
        mesaCentral.getChildren().clear();
    }

    private void showVira(Card vira) {

        if (vira == null) {
            System.err.println("Fim de jogo");
            endGameMenu();
            return;
        }

        viraBox.getChildren().clear();
        CardView view = new CardView();
        view.showFront(vira.getImagePath());
        view.setDisable(true);
        viraBox.getChildren().add(view);
    }

    private void executeGuess() {

        if (indexGuess >= tableGuessOrder.size()) {
            Collections.rotate(tableGuessOrder, -1);
            startTurn();
            return;
        }

        Player atual = tableGuessOrder.get(indexGuess);

        if (atual.isHuman()) {
            startGuessPhase();
            return;
        }


        engine.makeGuesses(atual, indexGuess, sumGuessesSoFar());
        updateScore();

        indexGuess++;
        executeGuess();
    }



    private void updateScore() {

        placarGrid.getChildren().removeIf(
                n -> GridPane.getRowIndex(n) != null
                        && GridPane.getRowIndex(n) > 0
        );

        AtomicInteger row = new AtomicInteger(1);

        engine.getPlayers().stream()
                .sorted((p1, p2) ->
                        Integer.compare(p2.getScore(), p1.getScore())
                )
                .forEach(p -> {
                    placarGrid.add(new Label(p.getName()), 0, row.get());
                    placarGrid.add(new Label(String.valueOf(p.getGuess())), 1, row.get());
                    placarGrid.add(new Label(String.valueOf(p.getRoundScore())), 2, row.get());
                    placarGrid.add(new Label(String.valueOf(p.getScore())), 3, row.get());
                    row.getAndIncrement();
                });
    }




    @FXML
    private void confirmGuess() {

        Integer guess = palpiteChoice.getValue();
        if (guess == null) return;

        if (!guessIsValid(guess)) {
            eventoLabel.setText("❌❌❌ Palpite inválido! Escolha outro.");
            return;
        }

        phelipe.setGuess(guess);

        endGuessPhase();
        updateScore();

        indexGuess++;
        executeGuess();
    }


    private boolean guessIsValid(int guess) {

        if (indexGuess != tableGuessOrder.size() - 1) {
            return true; // só o último tem restrição
        }

        int sum = 0;
        for (Player p : tableGuessOrder) {
            if (p.getGuess() != -1) {
                sum += p.getGuess();
            }
        }

        return sum + guess != phelipe.getHand().size();
    }




    private void prepararPalpiteHumano() {
        palpiteChoice.getItems().clear();
        for (int i = 0; i <= phelipe.getHand().size(); i++) {
            palpiteChoice.getItems().add(i);
        }
        palpiteChoice.setValue(0);
    }
    private void startGuessPhase() {
        confirmarPalpiteBtn.setDisable(false);
        palpiteChoice.setDisable(false);
        prepararPalpiteHumano();
        eventoLabel.setText("Faça seu palpite");
    }

    private void endGuessPhase() {
        confirmarPalpiteBtn.setDisable(true);
        palpiteChoice.setDisable(true);
    }


    private void removeBotVisualCard(Player bot) {

        List<CardView> views;
        Pane container;

        if (bot == bot1) {
            views = maoBotEsquerdaViews;
            container = maoBotEsquerda;
        } else if (bot == bot2) {
            views = maoBotCimaViews;
            container = maoBotCima;
        } else {
            views = maoBotDireitaViews;
            container = maoBotDireita;
        }

        if (!views.isEmpty()) {
            CardView removida = views.remove(0);
            container.getChildren().remove(removida);
        }
    }

    private void endGameMenu() {

        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fim de jogo");
            alert.setHeaderText("Ranking final:");
            alert.setContentText(
                    finalScore() +
                            "O que você deseja fazer?"
            );

            ButtonType btnReiniciar = new ButtonType("🔄 Reiniciar");
            ButtonType btnFechar = new ButtonType("❌ Fechar");

            alert.getButtonTypes().setAll(btnReiniciar, btnFechar);

            Optional<ButtonType> escolha = alert.showAndWait();

            if (escolha.isPresent()) {
                if (escolha.get() == btnReiniciar) {
                    restartGame();
                } else {
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
    }

    private String finalScore() {

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 PLACAR FINAL 🏆\n\n");

        engine.getPlayers().stream()
                .sorted((p1, p2) ->
                        Integer.compare(p2.getScore(), p1.getScore())
                )
                .forEach(p -> {
                    sb.append(p.getName())
                            .append(" - ")
                            .append(p.getScore())
                            .append(" pontos\n");
                });

        return sb.toString();
    }



    private void restartGame() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/hello-view.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) mesaCentral.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }




    private GridPane cardRanking() {

        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(4);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: green;");

        Label lblCartas = new Label("Cartas");
        lblCartas.setTextFill(Color.WHITE);

        Label lblNaipes = new Label("Naipes");
        lblNaipes.setTextFill(Color.WHITE);

        grid.add(lblCartas, 0, 0);
        grid.add(lblNaipes, 1, 0);

        String[] cartas = {"3", "2", "A", "K", "J", "Q", "7", "6", "5", "4"};
        String[] naipes = {"♣", "♥", "♠", "♦"};

        int row = 1;
        for (String c : cartas) {
            grid.add(new Label(c), 0, row++);
        }

        row = 1;
        for (String n : naipes) {
            Label lbl = new Label(n);
            if (n.equals("♥") || n.equals("♦")) {
                lbl.setStyle("-fx-text-fill: red;");
            }
            grid.add(lbl, 1, row++);
        }

        return grid;
    }


    @FXML
    private void toggleForcaCarta() {

        if (forcaCartaStage != null && forcaCartaStage.isShowing()) {
            forcaCartaStage.close();
            forcaCartaStage = null;
            toggleForcaCartaBtn.setText("Força das Cartas");
            return;
        }

        GridPane forcaCarta = cardRanking();

        Scene scene = new Scene(forcaCarta, 140, 260);
        scene.setFill(Color.GREEN);

        forcaCartaStage = new Stage();
        forcaCartaStage.setTitle("Força das Cartas");
        forcaCartaStage.setScene(scene);
        forcaCartaStage.setResizable(false);


        forcaCartaStage.initOwner(toggleForcaCartaBtn.getScene().getWindow());
        forcaCartaStage.initModality(Modality.NONE);

        forcaCartaStage.setOnCloseRequest(e -> {
            toggleForcaCartaBtn.setText("Força das Cartas");
            forcaCartaStage = null;
        });

        forcaCartaStage.show();

        toggleForcaCartaBtn.setText("Ocultar Força");
    }





}
