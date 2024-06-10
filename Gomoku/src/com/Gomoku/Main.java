package com.Gomoku;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main  {

    private static OnlineHomePage onlineHomePage;
    private static HomePage homePage;

    public static void main(String[] args)
    {
        // Kết nối tới MongoDB
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("Gomoku");

        // Login Page and Game Window
        LoginPage loginPage = new LoginPage(database);


        // HomePage
        homePage = new HomePage();



        // Thiết lập sự kiện cho LoginPage khi đăng nhập thành công
        loginPage.setLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess() {
                loginPage.setVisible(false);
                onlineHomePage = new OnlineHomePage(loginPage.getUsername());
                // Thiết lập sự kiện cho WelcomePage khi đăng xuất
                onlineHomePage.setLogoutListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onlineHomePage.setVisible(false);
                        loginPage.setVisible(true);
                    }
                });
                onlineHomePage.setVisible(true);
            }

        });

        homePage.setQuitMultiplayerListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginPage.setVisible(true);
            }
        });

        // Hiển thị trang đăng nhập ban đầu
        homePage.setVisible(true);
    }
}
