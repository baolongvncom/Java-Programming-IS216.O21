import java.io.*;
import java.net.*;
import java.util.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class Main {
    private static final int PORT = 8888;
    private static final ArrayList<ClientHandler> clients = new ArrayList<>();
    private static MongoCollection<Document> collection;

    // Tạo một danh sách phòng chơi với tên phòng và số người chơi
    private static final Map<String, Integer> rooms = new HashMap<>();

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("Gomoku");

        collection = database.getCollection("player");


        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server đang chạy trên cổng " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Kết nối từ " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private String currentRoom;

        private DataInputStream in;
        private DataOutputStream out;

        private String username;

        private int x = 0;
        private int y = 0;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                while (true)
                {
                    String position = in.readUTF();
                    System.out.println(position);
                    if (position.startsWith("create-room"))
                    {
                        // Check xem phòng đã tồn tại chưa
                        String room = position.split(" ")[1];
                        if (rooms.containsKey(room))
                        {
                            out.writeUTF("create-room-fail");
                        }
                        else
                        {
                            rooms.put(room, 1);
                            out.writeUTF("create-room-success");
                        }
                    }
                    else if (position.startsWith("join-room"))
                    {
                        // Check xem phòng đã tồn tại chưa
                        String room = position.split(" ")[1];
                        if (rooms.containsKey(room))
                        {
                            if (rooms.get(room) == 2)
                            {
                                out.writeUTF("room-is-full");
                            }
                            else
                            {
                                rooms.put(room, 2);
                                out.writeUTF("join-room-success");
                            }
                        }
                        else
                        {
                            out.writeUTF("room-not-exist");
                        }
                    }
                    else if (position.startsWith("win"))
                    {
                       // Xóa phòng chơi
                        rooms.remove(position.split(" ")[1]);

                        // Cộng thêm vào số trận thắng của người chơi
                        Document query = new Document("username", position.split(" ")[2]);
                        Document update = new Document("$inc", new Document("win", 1));

                        // Update vào database
                        collection.updateOne(query, update);

                        for (ClientHandler client : clients) {
                            client.sendMessage(position);
                        }
                    }
                    else if (position.startsWith("lose"))
                    {
                        // Cộng thêm vào số trận thua của người chơi
                        Document query = new Document("username", position.split(" ")[2]);
                        Document update = new Document("$inc", new Document("lose", 1));

                        // Update vào database
                        collection.updateOne(query, update);

                        for (ClientHandler client : clients) {
                            client.sendMessage(position);
                        }
                    }
                    else if (position.startsWith("move"))
                    {
                        for (ClientHandler client : clients) {
                            client.sendMessage(position);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String position) {
            try {
                out.writeUTF(position);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
