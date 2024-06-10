package com.Gomoku;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.*;


public class OfflineBoardGUI extends JPanel {

    /**
     *
     */
    private Graphics2D g2D;
    private BufferedImage image;
    private boolean isAIThinking = false;

    protected JPanel GUIPanel;
    private JLabel currentPlayerStatus;
    private JLabel winLabel;
    private JPanel textPanel;

    private Cell[][] matrix;

    private static final long serialVersionUID = 1L;

    private int sideLength; // Side length of the square board in pixels
    private int boardSize; // Number of cells in one side (e.g. 19 for a 19x19 board)
    private final int cellLength;

    private JDialog winDialog;

    private ActionListener restartOfflineGameListner;// Side length of a single cell in pixels


    public OfflineBoardGUI(int sideLength, int boardSize) {
        this.sideLength = sideLength;
        this.boardSize = boardSize;
        this.cellLength  = sideLength / boardSize;

        matrix = new Cell[boardSize][boardSize];

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        textPanel = new JPanel();

        currentPlayerStatus = new JLabel("Your turn");
        currentPlayerStatus.setFont(new Font("Arial", Font.BOLD, 20));
        currentPlayerStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(currentPlayerStatus);

        // Win dialog
        winDialog = new JDialog();
        winDialog.setLayout(new GridLayout(2, 1));
        winDialog.setSize(200, 100);
        winDialog.setLocationRelativeTo(null);

        JButton okButton = new JButton("Restart");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (restartOfflineGameListner != null) {
                    restartOfflineGameListner.actionPerformed(e);
                }
            }
        });

        winLabel = new JLabel("You win!");
        winLabel.setFont(new Font("Arial", Font.BOLD, 20));
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        winDialog.add(winLabel);

        winDialog.add(okButton);

        InitMatrix();

        add(GUIPanel);

        add(textPanel);

    }

    public void setRestartOfflineGameListner(ActionListener listener) {
        this.restartOfflineGameListner = listener;
    }

    private void InitMatrix()
    {
        GUIPanel = new JPanel();
        GUIPanel.setLayout(new GridLayout(boardSize, boardSize));
        for (int j = 0; j < boardSize; j++)
            for (int i = 0; i < boardSize; i++)
            {
                Cell cell = new Cell(cellLength, cellLength);
                matrix[j][i] = cell;
                GUIPanel.add(cell);
            }
    }



    public int[] getRelativePos(int x, int y) {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
            {
                Cell cell = matrix[i][j];
                if (cell.isContain(x, y))
                {
                    return new int[]{j, i};
                }
            }
        return new int[]{-1, -1};
    }
    public Dimension getPreferredSize()
    {
        return new Dimension(sideLength, sideLength);
    }
    public void printWinner(int winner) {
if (winner == 2)
        {
            currentPlayerStatus.setText("You win!");
            winLabel.setText("You win!");
            winDialog.setVisible(true);
        }
        else if (winner == 1)
        {
            currentPlayerStatus.setText("AI wins!");
            winLabel.setText("AI wins!");
            winDialog.setVisible(true);
        }
        else
        {
            currentPlayerStatus.setText("Draw!");
            winLabel.setText("Draw!");
            winDialog.setVisible(true);
        }
    }
    public void drawStone(int posX, int posY, boolean black) {

        if(posX >= boardSize || posY >= boardSize) return;

        Cell cell = matrix[posY][posX];
        cell.setValue(black ? Cell.X_VALUE : Cell.O_VALUE);
        cell.repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g.create();

        int w = GUIPanel.getWidth() / boardSize;
        int h = GUIPanel.getHeight() / boardSize;

        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
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



    private void printThinking(Graphics2D g2D) {

    }

    public void attachListener(MouseListener listener) {
        addMouseListener(listener);
    }
    public void setAIThinking(boolean flag) {
        if (flag)
        {
            currentPlayerStatus.setText("AI is thinking...");
        }
        else
        {
            currentPlayerStatus.setText("Your turn");
        }
        isAIThinking = flag;
//        currentPlayerStatus.repaint();
    }

    public void reset() {
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
            {
                Cell cell = matrix[i][j];
                cell.setValue(Cell.EMPTY_VALUE);
                cell.repaint();
            }
        currentPlayerStatus.setText("Your turn");
    }

}
