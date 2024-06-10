package com.Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;


public class OnlineBoard extends JPanel implements Runnable{
    private static final int N = 10;
    private static final int M = 10;

    private String winner = "";
    private static Boolean isEnd;
    private static final String X_WIN = "X win";
    private static final String O_WIN = "O win";

    private final int CELL_WIDTH = getWidth() / N;
    private final int CELL_HEIGHT = getHeight() / M;

    private boolean myTurn;
    private String currentPlayer;

    private DataOutputStream out;
    private DataInputStream in;

    private String room;
    private Boolean isHost;

    private Thread thread;

    private SetWinListener setWinListener;

    // Mang quan li
    private final Cell[][] matrix = new Cell[N][M];

    public OnlineBoard(DataOutputStream out, DataInputStream in, String room, Boolean isHost)
    {
        setLayout(new GridLayout(N, M));
        isEnd = false;
        this.out = out;
        this.in = in;
        this.room = room;
        this.isHost = isHost;
        InitMatrix();
        myTurn = isHost;
        currentPlayer = isHost ? Cell.X_VALUE : Cell.O_VALUE;

        // Create Thread
        thread = new Thread(this, "Board");
        thread.start();

        // Add mouse listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnd)
                {
                    return;
                }

                super.mousePressed(e);
                int x = e.getX();
                int y = e.getY();
                try {
                    if (out != null && myTurn) {
                        System.out.println("move " + x + " " + y + " " + room + " " + currentPlayer);
                        out.writeUTF("move " + x + " " + y + " " + room + " " + currentPlayer);
                        myTurn = false;
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void InitMatrix()
    {
        for (int j = 0; j < N; j++)
            for (int i = 0; i < M; i++)
            {
                Cell cell = new Cell(CELL_WIDTH, CELL_HEIGHT);
                matrix[i][j] = cell;
                add(cell);
            }
        repaint();
    }

    public int[][] getBoardMatrix() {
        int[][] boardMatrix = new int[N][M];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                if (matrix[i][j].getValue().equals(Cell.X_VALUE))
                {
                    boardMatrix[i][j] = 1;
                }
                else if (matrix[i][j].getValue().equals(Cell.O_VALUE))
                {
                    boardMatrix[i][j] = 2;
                }
                else
                {
                    boardMatrix[i][j] = 0;
                }
            }
        return boardMatrix;
    }


    public void reset()
    {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
            {
                matrix[i][j].setValue(Cell.EMPTY_VALUE);
                matrix[i][j].repaint();
            }
        isEnd = false;
        winner = "";
        currentPlayer = "";
        isHost = false;
    }
    public void setNewGame(String player, Boolean isHost, String room)
    {
        reset();
        myTurn = isHost;
        currentPlayer = player;
        room = room;
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

    public void setWinListener(SetWinListener listener) {
        this.setWinListener = listener;
    }

    @Override
    public void paint(Graphics g) {
        int w = getWidth() / N;
        int h = getHeight() / M;
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
    }
    public void setCell(int x, int y, String value)
    {
        if (isEnd)
        {
            return;
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
                        if (!value.equals(currentPlayer))
                        {
                            myTurn = true;
                        }
                        cell.repaint();
                    }
                }
            }
    }
    public String getCurrentPlayer()
    {
        return currentPlayer;
    }

    @Override
    public void run() {
        while (!isEnd())
        {
            String position = null;
            try {
                position = in.readUTF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] parts = position.split(" ");
            while (!Objects.equals(parts[0], "move") && !Objects.equals(parts[3], room))
            {
                try {
                    position = in.readUTF();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                parts = position.split(" ");
            }
            if (Objects.equals(parts[0], "move"))
            {
                setCell(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[4]);
            }
            checkWin(parts[4]);
            if (!Objects.equals(currentPlayer, parts[4]))
            {
                myTurn = true;
            }
        }
    }
}
