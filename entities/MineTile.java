package entities;

import javax.swing.*;

public class MineTile extends JButton {
    public int row;
    public int col;

    public MineTile(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
