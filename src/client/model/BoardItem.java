package client.model;

import javafx.scene.control.Label;

public class BoardItem {
    private final Label label;
    private final int row;
    private final int col;
    private int index;
    private boolean isAvailable;

    public BoardItem(Label label, int row, int col, int index, boolean isAvailable) {
        this.label = label;
        this.row = row;  // y
        this.col = col;  // x
        this.index = index;
        this.isAvailable = isAvailable;
    }

    public Label getLabel() {
        return label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
