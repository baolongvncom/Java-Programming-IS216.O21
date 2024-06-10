package com.Gomoku;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;



public class OfflineBoard {

    private OfflineBoardGUI gui;
    private int[][] boardMatrix; // 0: Empty 1: White 2: Black
    private ActionListener restartOfflineGameListner;


    public OfflineBoard(int sideLength, int boardSize) {

        gui = new OfflineBoardGUI(sideLength, boardSize);
        boardMatrix = new int[boardSize][boardSize];

        gui.setRestartOfflineGameListner(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (restartOfflineGameListner != null) {
                    restartOfflineGameListner.actionPerformed(e);
                }
            }
        });

    }
    public void setRestartOfflineGameListner(ActionListener listener) {
        this.restartOfflineGameListner = listener;
    }

    // Fake copy constructor (only copies the boardMatrix)
    public OfflineBoard(OfflineBoard board) {
        int[][] matrixToCopy = board.getBoardMatrix();
        boardMatrix = new int[matrixToCopy.length][matrixToCopy.length];
        for(int i=0;i<matrixToCopy.length; i++) {
            for(int j=0; j<matrixToCopy.length; j++) {
                boardMatrix[i][j] = matrixToCopy[i][j];
            }
        }
    }
    public int getBoardSize() {
        return boardMatrix.length;
    }
    public void removeStoneNoGUI(int posX, int posY){
        boardMatrix[posY][posX] = 0;
    }
    public void addStoneNoGUI(int posX, int posY, boolean black) {
        boardMatrix[posY][posX] = black ? 2 : 1;
    }
    public boolean addStone(int posX, int posY, boolean black) {

        // Check whether the cell is empty or not
        if(boardMatrix[posY][posX] != 0) return false;

        gui.drawStone(posX, posY, black);
        boardMatrix[posY][posX] = black ? 2 : 1;
        return true;

    }
    public ArrayList<int[]> generateMoves() {
        ArrayList<int[]> moveList = new ArrayList<int[]>();

        int boardSize = boardMatrix.length;

        // Look for cells that has at least one stone in an adjacent cell.
        for(int i=0; i<boardSize; i++) {
            for(int j=0; j<boardSize; j++) {

                if(boardMatrix[i][j] > 0) continue;

                if(i > 0) {
                    if(j > 0) {
                        if(boardMatrix[i-1][j-1] > 0 ||
                                boardMatrix[i][j-1] > 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(j < boardSize-1) {
                        if(boardMatrix[i-1][j+1] > 0 ||
                                boardMatrix[i][j+1] > 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(boardMatrix[i-1][j] > 0) {
                        int[] move = {i,j};
                        moveList.add(move);
                        continue;
                    }
                }
                if( i < boardSize-1) {
                    if(j > 0) {
                        if(boardMatrix[i+1][j-1] > 0 ||
                                boardMatrix[i][j-1] > 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(j < boardSize-1) {
                        if(boardMatrix[i+1][j+1] > 0 ||
                                boardMatrix[i][j+1] > 0) {
                            int[] move = {i,j};
                            moveList.add(move);
                            continue;
                        }
                    }
                    if(boardMatrix[i+1][j] > 0) {
                        int[] move = {i,j};
                        moveList.add(move);
                        continue;
                    }
                }

            }
        }

        return moveList;

    }
    public int[][] getBoardMatrix() {
        return boardMatrix;
    }

    public void startListening(MouseListener listener) {
        gui.attachListener(listener);
    }
    public OfflineBoardGUI getGUI() {
        return gui;
    }
    public int[] getRelativePos(int x, int y) {
        return gui.getRelativePos(x, y);
    }
    public void printWinner(int winner) {
        gui.printWinner(winner);
    }
    public void thinkingStarted() {
        gui.setAIThinking(true);
    }
    public void thinkingFinished() {
        gui.setAIThinking(false);
    }
    public void reset() {
        gui.reset();
        boardMatrix = new int[boardMatrix.length][boardMatrix.length];
    }


}
