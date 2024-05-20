package entities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper {
    private int tileSize = 70;
    private int numRows = 8;
    private int numCols = 8;
    private int boardWidth = numCols * tileSize;
    private int boardHeight = numRows * tileSize;

    private JFrame frame = new JFrame("Minesweeper");
    private JLabel textLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JPanel restartPanel = new JPanel();
    private JPanel topPanel = new JPanel();
    private JPanel mineCountPanel = new JPanel();
    private JButton restartButton = new JButton("Restart 🔄");
    private JTextField mineCountField;

    private int mineCount;
    private MineTile[][] board = new MineTile[numRows][numCols];
    private ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    public Minesweeper() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Ariel", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);

        restartPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        restartPanel.add(restartButton);

        mineCountPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        mineCountField = new JTextField("10");
        mineCountPanel.add(new JLabel("Enter number of mines: "));
        mineCountPanel.add(mineCountField);

        topPanel.setLayout(new BorderLayout());
        topPanel.add(textPanel, BorderLayout.CENTER);
        topPanel.add(restartPanel, BorderLayout.EAST);
        topPanel.add(mineCountPanel, BorderLayout.SOUTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(boardPanel);

        restartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                start();
            }
        });
    }

    public void start() {
        gameOver = false;
        mineCount = Integer.parseInt(mineCountField.getText());
        initializeBoard();
        setMines();
    }


    private void initializeBoard() {
        boardPanel.removeAll();
        tilesClicked = 0;
        textLabel.setText("Minesweeper: " + mineCount);
        for (int r = 0 ; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.setFont(new Font("Ariel Unicode MS", Font.PLAIN, 40));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            if(tile.getText() == "") {
                                if(mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.row, tile.col);
                                }
                            }
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);
        setMines();
    }

    private void setMines() {
        mineList = new ArrayList<>();
        int minesLeft = mineCount;
        while (minesLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                minesLeft--;
            }
        }
    }

    private void revealMines() {
        for (int i = 0; i < mineList.size() ; i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Game Over!");
    }

    private void checkMine(int row, int col) {
        if (row < 0 || col < 0 || row >= numRows || col >= numCols) {
            return;
        }
        MineTile tile = board[row][col];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked++;
        int minesFound = 0;

        minesFound += countMine(row-1, col-1);
        minesFound += countMine(row-1, col);
        minesFound += countMine(row-1, col+1);

        minesFound += countMine(row, col+1);
        minesFound += countMine(row, col-1);

        minesFound += countMine(row+1, col+1);
        minesFound += countMine(row+1, col);
        minesFound += countMine(row+1, col-1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            checkMine(row-1, col-1);
            checkMine(row-1, col);
            checkMine(row-1, col+1);

            checkMine(row, col+1);
            checkMine(row, col-1);

            checkMine(row+1, col+1);
            checkMine(row+1, col);
            checkMine(row+1, col-1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }

    private int countMine(int row, int col) {
        if (row < 0 || col < 0 || row >= numRows || col >= numCols) {
            return 0;
        }
        if (mineList.contains(board[row][col])) {
            return 1;
        }
        return 0;
    }
}
