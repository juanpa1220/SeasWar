package client.control;

import client.model.BoardItem;
import client.model.game.Warrior;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BoardController {
    public GridPane gridPane;
    public static BoardController boardController;
    private final ArrayList<BoardItem> board = new ArrayList<>();

    public BoardController() {
        boardController = this;
    }

    public static BoardController getInstance() {
        return boardController;
    }

    public void createBoard() {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 30; col++) {
                Label temLabel = new Label();
                temLabel.setMaxHeight(20);
                temLabel.setMinHeight(20);
                temLabel.setPrefHeight(20);
                temLabel.setMaxWidth(20);
                temLabel.setMinWidth(20);
                temLabel.setPrefWidth(20);
                temLabel.setStyle("-fx-border-color: #4a4f51; -fx-font-size: 8; -fx-alignment: CENTER");
                this.gridPane.add(temLabel, col, row);
                this.board.add(new BoardItem(temLabel, row, col));
            }
        }
    }

    public BoardItem getItem(int x, int y) {
        return this.board.get(x + 30 * y);
    }

    public ArrayList<BoardItem> getQuadrant(int radio, Point position) {
        ArrayList<BoardItem> items = new ArrayList<>();
        BoardItem origen = this.board.get(position.x + 30 * position.y);
        int y = origen.getRow();
        int x = origen.getCol();
        for (int i = y - radio; i <= y + radio; i++) {
            for (int j = x - radio; j <= x + radio; j++) {
                if (i >= 0 && i < 20 && j >= 0 && j < 30) {
                    BoardItem item = this.board.get(j + 30 * i);
                    items.add(item);
                }
            }
        }
        return items;
    }


    public ArrayList<BoardItem> getLine(Point position, int direction, int length) {
        // direction 0: arriba, 1: abajo, 2: izquierda, 3: derecha
        // length must be greater than 0
        ArrayList<BoardItem> items = new ArrayList<>();
        AtomicBoolean flag = new AtomicBoolean(true);
        AtomicInteger current = new AtomicInteger(position.x + 30 * position.y);
        while (flag.get()) {
            if (current.get() >= 0 && current.get() < 600) {
                items.add(this.board.get(current.get()));
                if (--length <= 0) {
                    flag.set(false);
                } else {
                    if (direction == 0) {
                        current.set(current.get() - 30);
                    } else if (direction == 1) {
                        current.set(current.get() + 30);
                    } else if (direction == 2) {
                        current.set(current.get() - 1);
                    } else {
                        current.set(current.get() + 1);
                    }
                }
            } else {
                flag.set(false);
            }
        }

        return items;
    }

    public void printBoard(ArrayList<Warrior> warriors) {
        int totalItem = 0;
        for (int i = 0; i < warriors.size(); i++) {
            int percentage = warriors.get(i).percentage;
            warriors.get(i).colorId = i;
            int numBoardsItems = (int) (600 * percentage * 0.01);
            totalItem += numBoardsItems;

            if (i == warriors.size() - 1) { // por el redondeo no cierra
                boolean flag = true;
                while (flag) {
                    if (totalItem > 600) {
                        numBoardsItems--;
                        totalItem--;
                    } else if (totalItem < 600) {
                        numBoardsItems++;
                        totalItem++;
                    } else {
                        flag = false;
                    }
                }
            }

            while (numBoardsItems > 0) {
                BoardItem boardItem = this.board.get(new Random().nextInt(600));
                if (!boardItem.isPainted()) {
                    warriors.get(i).army.add(boardItem);
                    boardItem.pain(i);
                    boardItem.setPainted(true);
                    numBoardsItems--;
                }
            }

        }

    }

    public void showPercentages() {
        for (BoardItem item : this.board) {
            item.showPercentage();
        }
    }

    public void hidePercentages() {
        for (BoardItem item : this.board) {
            item.hidePercentage();
        }
    }

    public void showAlive() {
        for (BoardItem item : this.board) {
            item.showAlive();
        }
    }

    public void hideAlive() {
        for (BoardItem item : this.board) {
            item.hideAlive();
        }
    }

    public void showUnavailable() {
        for (BoardItem item : this.board) {
            item.showUnavailable();
        }
    }

    public void hideUnavailable() {
        for (BoardItem item : this.board) {
            item.hideUnavailable();
        }
    }

    public String consultBoard(int x, int y) {
        return this.board.get(x + 30 * y).getActivityLog();
    }


    public ArrayList<BoardItem> getBoard() {
        return this.board;
    }
}
