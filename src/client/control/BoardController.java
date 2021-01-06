package client.control;

import client.model.BoardItem;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

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
        int index = 0;
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
//                temLabel.setId(String.valueOf(index));
//                temLabel.setText(String.valueOf(index));
                this.gridPane.add(temLabel, col, row);
                this.board.add(new BoardItem(temLabel, row, col, index, true));
                index++;
            }
        }
    }

    public ArrayList<BoardItem> getBoard() {
        return board;
    }

}
