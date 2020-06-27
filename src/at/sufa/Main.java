package at.sufa;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


public class Main {

    static int id1 = 0;
    static int id2 = 0;
    static Scanner input = new Scanner(System.in);
    static String username = "";
    static Timer timer = new Timer();

    public static int idMax = 0;

    public static void main(String[] args) {
        boolean chatting = true;

        System.out.println("Write your Username");
        username = input.nextLine();

        checkUser(username);
        selectChatPartner();

        while (chatting){
            receiveMessage();
            sendMessage();
        }

    }


        public static void receiveMessage(){

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {

                    Connection connection = null;
                    String url = "jdbc:mysql://localhost:3306/chat?user=root";

                    connection = DriverManager.getConnection(url);
                    String sql = "Select * from messages where userID1 = '" + id1 + "' and userID2 = '" + id2 + "' " +
                            "or userID1 = '" + id2 + "' and userID2 = '" + id1 + "'";
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(sql);

                    while (rs.next()) {
                        String message = rs.getString("message");
                        int messageID = rs.getInt("messageID");
                        if (messageID > idMax) {
                            System.out.println(message);
                            idMax = messageID;
                        }
                    }
                    connection.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, 1000);

    }

    public static void sendMessage() {
        try {

            Connection connection = null;
            String url = "jdbc:mysql://localhost:3306/chat?user=root";

            connection = DriverManager.getConnection(url);

            String message = username + ": " + input.nextLine();

            String sqlMessage = "INSERT INTO `messages`(`userID1`, `userID2`, `message`) " +
                    "VALUES ('" + id1 + "','" + id2 + "','" + message + "')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlMessage);

            connection.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void checkUser(String username) {
        try {
            ArrayList<String> usernames = new ArrayList<>();

            Connection connection = null;
            String url = "jdbc:mysql://localhost:3306/chat?user=root";

            connection = DriverManager.getConnection(url);

            String sql = "Select * from user";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String user = rs.getString("username");
                usernames.add(user);
            }

            boolean userExists = false;

            for (String name : usernames) {
                if (name.equals(username)) {
                    userExists = true;
                }
            }

            if (userExists) {
                System.out.println("Welcome back " + username + "!");
            } else {
                String usernameSQL = "INSERT INTO `user`(`username`) VALUES ('" + username + "')";
                statement.executeUpdate(usernameSQL);
                System.out.println("Willkommen im Chat " + username + "!");
            }
            String sqlID = "Select id from `user` where username = '" + username + "'";
            ResultSet rsID = statement.executeQuery(sqlID);

            while (rsID.next()) {
                id1 = rsID.getInt("id");
                //System.out.println(id1);
            }
            //String sqlIDWrite = "INSERT INTO `messages`(`userID1`) VALUES ('" + id1 + "')";
            connection.close();

        } catch (
                SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void selectChatPartner() {
        System.out.println("Wähle deinen Chatpartner.");

        try {
            Connection connection = null;
            String url = "jdbc:mysql://localhost:3306/chat?user=root";

            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            String sqlUserlist = "Select * from user where not id = '" + id1 + "'";
            ResultSet rs = statement.executeQuery(sqlUserlist);

            System.out.println("ID:\tUsername:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                System.out.println(id + " \t" + username);
            }

            System.out.println("Wähle die ID deines gewünschten Chatpartners");
            id2 = input.nextInt();

            String sqlFindUser = "Select username from user where id = '" + id2 + "'";
            rs = statement.executeQuery(sqlFindUser);

            while (rs.next()) {
                String chatBuddy = rs.getString("username");
                System.out.println("Du chattest jetzt mit " + chatBuddy + "! Viel Spaß!");
            }
            connection.close();


        } catch (
                SQLException ex) {
            ex.printStackTrace();
        }
    }
}

