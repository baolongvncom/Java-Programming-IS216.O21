package com.Gomoku;
import com.mongodb.client.MongoDatabase;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class GameWindow extends JFrame implements Runnable{
    private ActionListener logoutListener;

    private OnlineBoard onlineBoard;

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 8888;

    private String username;

    private String room;

    private DataInputStream in;
    private DataOutputStream out;

    private int x;
    private int y;

    private Boolean isJoin = false;

    private boolean isHost;

    JDialog dialog;
    private JPanel jPanel = null;

    private Socket socket;

    private Thread thread;

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

    public GameWindow(MongoDatabase database)
    {
        // Tạo dialog để vào phòng
        dialog = new JDialog(this, "Join Room", true);
        dialog.setLayout(new GridLayout(2, 2));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);

        // Create text field nhập room
        JTextField txtRoom = new JTextField();
        dialog.add(txtRoom);

        JButton btnJoin = new JButton("Join Room");
        btnJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room = txtRoom.getText();
                if (socket == null)
                    connectServer();
                try {
                    out.writeUTF("join-room " + room);
                    String roomMessage = in.readUTF();
                    switch (roomMessage) {
                        case "room-is-full" -> JOptionPane.showMessageDialog(null, "Room is full");
                        case "room-not-exist" -> JOptionPane.showMessageDialog(null, "Room not exist");
                        case "join-room-success" -> {
                            isHost = false;
                            isJoin = true;
                            initBoard(room, isHost);
                            dialog.setVisible(false);
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        dialog.add(btnJoin);

        JTextField txtcreateRoom = new JTextField();
        dialog.add(txtcreateRoom);

        JButton btnCreate = new JButton("Create Room");
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                room = txtcreateRoom.getText();
                if (socket == null)
                    connectServer();
                try {
                    out.writeUTF("create-room " + room);
                    String roomMessage = in.readUTF();
                    switch (roomMessage) {
                        case "create-room-fail" -> {
                            JOptionPane.showMessageDialog(null, "Room is exist");
                        }
                        case "create-room-success" -> {
                            isHost = true;
                            isJoin = true;
                            initBoard(room, isHost);
                            dialog.setVisible(false);
                        }
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        dialog.add(btnCreate);
    }

    // Initialize board
    public void initBoard(String room, Boolean isHost) {
        // Clear
        if (jPanel != null) {
            remove(jPanel);
        }

        jPanel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
        jPanel.setLayout(boxLayout);
        // Bottom Panel
        JPanel bottomPanel = new JPanel();

        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEADING, 0, 0);
        //bottomPanel.setLayout(flowLayout);


        //bottomPanel.setPreferredSize(new Dimension(300, 50));
        bottomPanel.setBackground(Color.YELLOW);



        JLabel lblTime = new JLabel("00:00");
        bottomPanel.add(lblTime);
        // Board Game

        onlineBoard = new OnlineBoard(out, in, room, isHost);

        onlineBoard.setPreferredSize(new Dimension(600, 600));

        onlineBoard.setWinListener(new SetWinListener() {
            @Override
            public void onWin() {
            }

        });
        jPanel.add(onlineBoard);
        jPanel.add(bottomPanel);
        // Add to main Panel
        setResizable(true);

        jPanel.setVisible(true);
        add(jPanel);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        if (thread == null)
        {
            thread = new Thread(this, "GameWindow");
            thread.start();
        }
    }

    public void reset() {
        onlineBoard.reset();
        room = "";
        isJoin = false;
        isHost = false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void unhidenDialog() {
        dialog.setVisible(true);
    }

    @Override
    public void run() {
        while (socket != null && !socket.isClosed() && isJoin) {
            try {
                if (onlineBoard.isEnd())
                {
                    System.out.println("End game");
                    isJoin = false;
                    if (Objects.equals(onlineBoard.getWinner(), onlineBoard.getCurrentPlayer()))
                    {
                        out.writeUTF("win " + room + " " + username);
                    }
                    else
                    {
                        out.writeUTF("lose " + room + " " + username);
                    }
                    if (onlineBoard.getWinner().equals(Cell.X_VALUE))
                    {
                        JOptionPane.showMessageDialog(this, "X win");
                    }
                    else if (onlineBoard.getWinner().equals(Cell.O_VALUE)) {
                        JOptionPane.showMessageDialog(this, "O win");
                    }
                    reset();
                    dialog.setVisible(true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
