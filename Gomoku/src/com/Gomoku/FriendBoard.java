package com.Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class FriendBoard extends JFrame {
    private int N;
    private int M;

    private String winner = "";
    private static Boolean isEnd;
    private static final String X_WIN = "X win";
    private static final String O_WIN = "O win";

    private int CELL_WIDTH ;
    private int CELL_HEIGHT;

    private String currentPlayer;

    private String room;
    private Boolean isHost;

    private JDialog winDialog;


    private JPanel mainPanel;
    private JPanel gamePanel;
    private JPanel textPanel;
    private JLabel currentPlayerStatus;

    private JLabel winLabel;

    // Mang quan li
    private Cell[][] matrix;

    public FriendBoard(int sideLength, int boardSize)
    {
        this.CELL_HEIGHT = sideLength / boardSize;
        this.CELL_WIDTH = sideLength / boardSize;

        this.N = boardSize;
        this.M = boardSize;

        matrix = new Cell[N][M];

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(N, M));
        isEnd = false;
        InitMatrix();
        currentPlayer = Cell.X_VALUE;
        winner = Cell.EMPTY_VALUE;

        textPanel = new JPanel();
        currentPlayerStatus = new JLabel("X turn");
        currentPlayerStatus.setFont(new Font("Arial", Font.BOLD, 20));
        currentPlayerStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(currentPlayerStatus);

        // Win dialog
        winDialog = new JDialog();
        winDialog.setLayout(new GridLayout(2, 1));
        winDialog.setSize(200, 100);
        winDialog.setLocationRelativeTo(this);

        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                winDialog.setVisible(false);
            }
        });

        winLabel = new JLabel(winner + " win!");
        winLabel.setFont(new Font("Arial", Font.BOLD, 20));
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        winDialog.add(winLabel);

        winDialog.add(restartButton);

        // Add mouse listener
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int x = e.getX();
                int y = e.getY();

                if (setCell(x, y, currentPlayer)) {
                    currentPlayer = currentPlayer.equals(Cell.X_VALUE) ? Cell.O_VALUE : Cell.X_VALUE;
                }

                checkWin(Cell.X_VALUE);
                checkWin(Cell.O_VALUE);

                if (isEnd) {
                    winLabel.setText(winner + " win!");
                    winDialog.setVisible(true);
                }
                else
                {
                    pringCurrentPlayer();
                }

            }
        });

        mainPanel.add(gamePanel);
        mainPanel.add(textPanel);

        add(mainPanel);
        pack();
        setTitle("Gomoku");
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void pringCurrentPlayer()
    {
        currentPlayerStatus.setText(currentPlayer + " turn");
    }

    private void InitMatrix()
    {
        for (int j = 0; j < N; j++)
            for (int i = 0; i < M; i++)
            {
                Cell cell = new Cell(CELL_WIDTH, CELL_HEIGHT);
                matrix[i][j] = cell;
                gamePanel.add(cell);
            }
        repaint();
    }

    public void reset()
    {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                matrix[i][j].setValue(Cell.EMPTY_VALUE);
            }
        repaint();
        isEnd = false;
        winner = "";
        currentPlayer = Cell.X_VALUE;
    }

    public void checkWin(String playerValue)
    {
        // Check row
        for (int i = 0; i < N; i++)
        {
            int count = 0;
            for (int j = 0; j < M; j++)
            {
                if (matrix[i][j].getValue().equals(playerValue))
                {
                    count++;
                }
                else
                {
                    count = 0;
                }
                if (count == 5)
                {
                    winner = playerValue;
                    isEnd = true;
                    return;
                }
            }
        }

        // Check column
        for (int j = 0; j < M; j++)
        {
            int count = 0;
            for (int i = 0; i < N; i++)
            {
                if (matrix[i][j].getValue().equals(playerValue))
                {
                    count++;
                }
                else
                {
                    count = 0;
                }
                if (count == 5)
                {
                    winner = playerValue;
                    isEnd = true;
                    return;
                }
            }
        }

        // Check diagonal
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                int count = 0;
                for (int k = 0; k < 5; k++)
                {
                    if (i + k < N && j + k < M && matrix[i + k][j + k].getValue().equals(playerValue))
                    {
                        count++;
                    }
                    else
                    {
                        count = 0;
                    }
                    if (count == 5)
                    {
                        winner = playerValue;
                        isEnd = true;
                        return;
                    }
                }
            }

        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                int count = 0;
                for (int k = 0; k < 5; k++)
                {
                    if (i + k < N && j - k >= 0 && matrix[i + k][j - k].getValue().equals(playerValue))
                    {
                        count++;
                    }
                    else
                    {
                        count = 0;
                    }
                    if (count == 5)
                    {
                        winner = playerValue;
                        isEnd = true;
                        return;
                    }
                }
            }
    }

    public Boolean isEnd()
    {
        return isEnd;
    }

    public String getWinner()
    {
        return winner;
    }

    @Override
    public void paint(Graphics g) {
        int w = gamePanel.getWidth() / N;
        int h = gamePanel.getHeight() / M;
        int k = 0;

        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                int x = i * w;
                int y = j * h;

                // Update matrix
                Cell cell = matrix[i][j];
                cell.setX(x);
                cell.setY(y);
                cell.setW(w);
                cell.setH(h);
                cell.repaint();
            }

        textPanel.repaint();
    }
    public boolean setCell(int x, int y, String value)
    {
        if (isEnd)
        {
            return false;
        }
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                Cell cell = matrix[i][j];
                if (cell.isContain(x, y))
                {
                    if (cell.getValue().equals(Cell.EMPTY_VALUE))
                    {
                        cell.setValue(value);
                        cell.repaint();
                        return true;
                    }
                }
            }
        return false;
    }
    public String getCurrentPlayer()
    {
        return currentPlayer;
    }

}
