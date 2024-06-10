package com.Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JFrame {
    private JPanel gamePanel;
    private ActionListener quitPlayWithFriendListenr;
    private ActionListener quitPlayWithBotListener;
    private ActionListener quitMultiplayerListener;

    public HomePage(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1, 1));
        // Play with bot
        OfflineBoard offlineBoard = new OfflineBoard(760, 19);
        OfflineGameWindow offlineGameWindow = new OfflineGameWindow(offlineBoard);

        // Play with friend
        FriendBoard friendBoard = new FriendBoard(760, 19);

        // Create window for player to decide who starts first
        JRadioButtonMenuItem playerStarts = new JRadioButtonMenuItem("Player starts");
        JRadioButtonMenuItem aiStarts = new JRadioButtonMenuItem("AI starts");

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(playerStarts);
        buttonGroup.add(aiStarts);

        JDialog dialog = new JDialog(this, "Who starts first?", true);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);

        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerStarts.isSelected()) {
                    offlineGameWindow.setAIStarts(false);
                } else {
                    offlineGameWindow.setAIStarts(true);
                }
                dialog.setVisible(false);
                offlineGameWindow.start();
            }
        });

        dialog.add(playerStarts);
        dialog.add(aiStarts);
        dialog.add(btnStart);

        offlineBoard.setRestartOfflineGameListner(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offlineGameWindow.end();
                offlineGameWindow.restart();
                dialog.setVisible(true);
            }
        });

        // Game panel
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 1));
        gamePanel.setSize(400, 300);

        JButton playWithBot = new JButton("Play with Bot");
        playWithBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(true);
            }
        });

        JButton playOnlineButton = new JButton("Multiplayer");
        playOnlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (quitMultiplayerListener != null) {
                    quitMultiplayerListener.actionPerformed(e);
                }
            }
        });

        JButton playWithFriend = new JButton("Play with Friend");
        playWithFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendBoard.setVisible(true);
            }
        });

        gamePanel.add(playWithBot);
        gamePanel.add(playOnlineButton);
        gamePanel.add(playWithFriend);

        jPanel.add(gamePanel);

        add(jPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }


    public void setQuitMultiplayerListener(ActionListener listener) {
        this.quitMultiplayerListener = listener;
    }

}
