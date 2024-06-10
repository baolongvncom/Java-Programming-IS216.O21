package com.Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Cell extends JPanel {
    private int w;
    private int h;

    private int x;
    private int y;

    private String value;
    private String currentPlayer;
    private BufferedImage image;

    public static final String X_VALUE = "X";
    public static final String O_VALUE = "O";
    public static final String EMPTY_VALUE = "";

    public Cell(int w, int h)
    {
        this.value = EMPTY_VALUE;
        this.currentPlayer = EMPTY_VALUE;

        // Load image

        setBackground(Color.WHITE);

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(w, h));

    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D) g;

        if (!value.equals(EMPTY_VALUE))
        {
            if (value.equals(X_VALUE)) {
                graphics2D.drawImage(ImageStorage.getXImage(), 0, 0, w, h, null);
            } else if (value.equals(O_VALUE)) {
                graphics2D.drawImage(ImageStorage.getOImage(), 0, 0, w, h, null);
            }
        }
    }

    public boolean isContain(int x, int y)
    {
        return x >= this.x + 1 && x <= this.x + w - 1 && y >= this.y + 1 && y <= this.y + h - 1;
    }

    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
