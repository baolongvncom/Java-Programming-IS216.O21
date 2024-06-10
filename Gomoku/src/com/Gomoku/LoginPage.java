package com.Gomoku;

import javax.swing.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;


public class LoginPage extends JFrame implements ActionListener {

    private final JButton btn_login;
    private final JButton btn_signup;
    private final JTextField JT_username;
    private final JPasswordField JT_password;

    private MongoCollection<Document> collection;

    private String username;

    LoginListener loginListener;


    public LoginPage(MongoDatabase database)
    {
        collection = database.getCollection("player");

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JPanel playerAccountInfoPanel = new JPanel();
        playerAccountInfoPanel.setPreferredSize(new Dimension(300, 150));
        playerAccountInfoPanel.setLayout(null);

        Font font = new Font("Arial", Font.PLAIN, 18);
        // Username field
        JLabel lb_username = new JLabel("Username");
        lb_username.setFont(font);
        lb_username.setBounds(10, 10, 80, 25);

        JT_username = new JTextField();
        JT_username.setBounds(100, 10, 160, 25);

        // Password field

        JLabel lb_password = new JLabel("Password");
        lb_password.setFont(font);
        lb_password.setBounds(10, 40, 80, 25);

        JT_password = new JPasswordField();
        JT_password.setBounds(100, 40, 160, 25);


        // Login button
        btn_login = new JButton("Login");
        btn_login.setFocusable(false);
        btn_login.addActionListener(this);

        // Signup button
        btn_signup = new JButton("Sign up");
        btn_signup.setFocusable(false);
        btn_signup.addActionListener(this);


        playerAccountInfoPanel.add(lb_username);
        playerAccountInfoPanel.add(JT_username);

        playerAccountInfoPanel.add(lb_password);
        playerAccountInfoPanel.add(JT_password);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(300, 50));
        buttonPanel.setLayout(new GridLayout(1, 2));

        buttonPanel.add(btn_login);
        buttonPanel.add(btn_signup);

        jPanel.add(playerAccountInfoPanel);
        jPanel.add(buttonPanel);

        add(jPanel);

        setTitle("Login Page");
        setSize(300, 150);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_login)
        {
            username = JT_username.getText();
            String password = Arrays.toString(JT_password.getPassword());

            Document query = new Document("username", username).append("password", password);

            if (collection.countDocuments(query) > 0)
            {
                if (loginListener != null)
                {
                    loginListener.onLoginSuccess();
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Login failed");
            }
        }
        if (e.getSource() == btn_signup)
        {
            username = JT_username.getText();
            String password = Arrays.toString(JT_password.getPassword());

            // Check if username existed
            Document query = new Document("username", username);

            if (collection.countDocuments(query) > 0)
            {
                JOptionPane.showMessageDialog(this, "Account existed");
            }
            else
            {
                query.append("password", password).append("win", 0).append("lose", 0);
                collection.insertOne(query);
                JOptionPane.showMessageDialog(this, "Account created");
                loginListener.onLoginSuccess();
            }
        }
    }

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    public String getUsername() {
        return username;
    }
}
