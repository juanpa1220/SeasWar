package client.model;

import javafx.scene.control.Label;

public class BoardItem {
    private final Label label;
    private final int row;
    private final int col;
    private boolean painted;
    private int life;
    private int id;
    private String activityLog;
    private int volcanoRadio;
    private int swirlRadio;
    private boolean isVolcano;
    private boolean isSwirl;
    private boolean percentageFlag;
    private boolean isGarbage;
    private int garbageValue;


    public BoardItem(Label label, int row, int col) {
        this.label = label;
        this.row = row;  // y
        this.col = col;  // x
        this.painted = false;
        this.life = 100;
        this.id = -1;
        this.activityLog = "";
        this.isVolcano = false;
        this.isSwirl = false;
        this.isGarbage = false;
        this.percentageFlag = false;
        this.volcanoRadio = 0;
        this.volcanoRadio = 0;
        this.garbageValue = 0;
    }

    public void addActivityReport(String activityReport) {
        this.activityLog += "\n\t- " + activityReport;
    }

    public String getActivityLog() {
        String report = "Reporte de celda (" + (this.getCol() + 1) + ", " + (this.getRow() + 1) + "):\n";
        report += "\tVida: " + this.life + "%\n";
        report += "\tOcupado por volcán: " + (this.isVolcano ? "Sí\n" : "No\n");
        report += "\tOcupado por remolino: " + (this.isSwirl ? "Sí\n" : "No\n");
        report += (this.activityLog.equals("") ? "\tSin ataques registrados" : "\tRegistro de ataques:" + this.activityLog);

        return report;
    }

    public void pain(int i) {
        if (i == 0) {
            this.id = i;
            this.label.setStyle(
                    "-fx-background-color: #b5622f; -fx-border-color: #4a4f51; -fx-font-size: 8;-fx-text-fill: black; -fx-alignment: CENTER");
        } else if (i == 1) {
            this.id = i;
            this.label.setStyle(
                    "-fx-background-color: #3074c1; -fx-border-color: #4a4f51; -fx-font-size: 8;-fx-text-fill: black; -fx-alignment: CENTER");
        } else {
            this.id = i;
            this.label.setStyle(
                    "-fx-background-color: #2fc36a; -fx-border-color: #4a4f51; -fx-font-size: 8;-fx-text-fill: black; -fx-alignment: CENTER");
        }
    }

    public void attack(int value) {
        this.life -= value;
        if (this.life <= 0) {
            if (this.id == 0) {
                this.label.setStyle(
                        "-fx-background-color: #5a3017; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-text-fill: black; -fx-alignment: CENTER");
            } else if (this.id == 1) {
                this.label.setStyle(
                        "-fx-background-color: #17385d; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-text-fill: black;-fx-alignment: CENTER");
            } else {
                this.label.setStyle(
                        "-fx-background-color: #176034; -fx-border-color: #4a4f51; -fx-font-size: 8;-fx-text-fill: black; -fx-alignment: CENTER");
            }
            this.label.setText("X");
            this.life = 0;
        }
        if (this.percentageFlag) {
            this.showPercentage();
        }
    }

    public void showPercentage() {
        this.percentageFlag = true;
        this.label.setText(String.valueOf(this.life));
    }

    public void hidePercentage() {
        this.percentageFlag = false;
        if (this.life <= 0) {
            this.label.setText("X");
        } else {
            this.label.setText("");
        }
    }

    public void showAlive() {
        if (this.life <= 0) {
            this.label.setStyle(
                    "-fx-background-color: #ff0000; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-alignment: CENTER");
        } else {
            this.label.setStyle(
                    "-fx-background-color: #4eff00; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-alignment: CENTER");
        }
    }

    public void hideAlive() {
        if (this.life <= 0) {
            this.attack(100);
        } else {
            this.pain(this.id);
        }
    }

    public void showUnavailable() {
        if (this.isVolcano) {
            this.label.setStyle(
                    "-fx-background-color: #7bb5f3; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-alignment: CENTER");
            this.label.setText("🌋");
        } else if (this.isSwirl) {
            this.label.setStyle(
                    "-fx-background-color: #f37979; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-alignment: CENTER");
            this.label.setText("🌪");
        } else {
            this.label.setStyle(
                    "-fx-background-color: #9ef179; -fx-border-color: #4a4f51; -fx-font-size: 8; -fx-alignment: CENTER");
        }
    }

    public void hideUnavailable() {
        if (this.life <= 0) {
            this.attack(100);
            this.label.setText("X");
        } else {
            this.pain(this.id);
            this.label.setText("");
        }
    }

    public void cure(int sanidad) {
        this.life += sanidad;
        if (this.life > 100) {
            this.life = 100;
        }
        if (this.percentageFlag) {
            this.showPercentage();
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isPainted() {
        return painted;
    }

    public int getLife() {
        return life;
    }

    public void setPainted(boolean painted) {
        this.painted = painted;
    }

    public void setVolcano(boolean volcano) {
        isVolcano = volcano;
    }

    public void setSwirl(boolean swirl) {
        isSwirl = swirl;
    }

    public int getId() {
        return id;
    }

    public void setVolcanoRadio(int size) {
        this.volcanoRadio = size;
    }

    public int getVolcanoRadio() {
        return volcanoRadio;
    }

    public boolean isVolcano() {
        return isVolcano;
    }


    public void setSwirlRadio(int radio) {
        this.swirlRadio = radio;
    }

    public int getSwirlRadio() {
        return swirlRadio;
    }

    public boolean isSwirl() {
        return this.isSwirl;
    }

    public void setGarbage(boolean b) {
        this.isGarbage = b;
    }

    public boolean isGarbage() {
        return isGarbage;
    }

    public void setGarbageValue(int numGarbage) {
        this.garbageValue = numGarbage;
    }

    public int getGarbageValue() {
        return garbageValue;
    }
}
