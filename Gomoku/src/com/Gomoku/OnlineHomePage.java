package com.Gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


class OnlineHomePage extends JFrame implements Runnable{
    private int win;
    private int lose;
    private String username;

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8888;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private Thread thread;

    private static GameWindow gameWindow;
    private static ActionListener logoutListener;

    private JPanel userPanel;

    private JLabel usernameValue;
    private JLabel winValue;
    private JLabel loseValue;

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public void connectServer() {

        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OnlineHomePage(String username)
    {
        // Connect to socket
        connectServer();

        this.username = username;

        // Connect to mongodb
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("Gomoku");

        // Create a collection
        collection = database.getCollection("player");

        gameWindow = new GameWindow(database);
        gameWindow.setUsername(username);

        JPanel jPanel = new JPanel();

        // Lấy số trận thắng và số trận thua của người chơi
        Document player = collection.find(new Document("username", username)).first();
        int win = player.getInteger("win");
        int lose = player.getInteger("lose");

        setLayout(new GridLayout(2, 2));
        setSize(400, 600);

        userPanel = new JPanel();
        userPanel.setSize(400, 300);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new GridLayout(1, 2));
        JLabel usernameLabel = new JLabel("Username: ");
        usernameValue = new JLabel(username);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameValue);

        JPanel winPanel = new JPanel();
        winPanel.setLayout(new GridLayout(1, 2));
        JLabel winLabel = new JLabel("Win: ");
        winValue = new JLabel(String.valueOf(win));
        winPanel.add(winLabel);
        winPanel.add(winValue);

        JPanel losePanel = new JPanel();
        losePanel.setLayout(new GridLayout(1, 2));
        JLabel loseLabel = new JLabel("Lose: ");
        loseValue = new JLabel(String.valueOf(lose));
        losePanel.add(loseLabel);
        losePanel.add(loseValue);

        userPanel.add(usernamePanel);
        userPanel.add(winPanel);
        userPanel.add(losePanel);

        jPanel.add(userPanel);

        // Play with bot
        OfflineBoard offlineBoard = new OfflineBoard(760, 19);
        OfflineGameWindow offlineGameWindow = new OfflineGameWindow(offlineBoard);

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

        JButton playWithFriend = new JButton("Multiplayer");
        playWithFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameWindow.unhidenDialog();
            }
        });

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(logoutListener);
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (logoutListener != null) {
                    logoutListener.actionPerformed(e);
                }
            }
        });

        gamePanel.add(playWithBot);
        gamePanel.add(playWithFriend);
        gamePanel.add(btnLogout);

        jPanel.add(gamePanel);

        add(jPanel);

        pack();
        setLocationRelativeTo(null);

        thread = new Thread(this, "HomePage");
        thread.start();

    }
    public void setLogoutListener(ActionListener listener) {
        this.logoutListener = listener;
    }

    @Override
    public void run() {
        while (true)
        {
            try {
                String message = in.readUTF();
                if (message.startsWith("win") || message.startsWith("lose"))
                {
                    System.out.println("Test HomePage");
                    // Update win or lose
                    Document player = collection.find(new Document("username", username)).first();
                    win = player.getInteger("win");
                    lose = player.getInteger("lose");

                    winValue.setText(String.valueOf(win));
                    loseValue.setText(String.valueOf(lose));

                    winValue.revalidate();
                    loseValue.revalidate();

                    userPanel.revalidate();

                    winValue.repaint();
                    loseValue.repaint();

                    userPanel.repaint();

                    OnlineHomePage.this.revalidate();
                    OnlineHomePage.this.repaint();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
