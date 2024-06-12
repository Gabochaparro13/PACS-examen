
package Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    private ServerSocket serverSocket;
    private List<Socket> clients;
    private List<Integer> numbers;

    public Servidor(int port ) throws IOException {
        serverSocket = new ServerSocket(5542);
        clients = new CopyOnWriteArrayList<>();
        numbers = new ArrayList<>();
    }

    public void start() {
        System.out.println("EL SERVIDOR SE ESTA INICIANDO ESPERE CONEXIONES");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                new Thread(new ClientHandler(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    int number = Integer.parseInt(message);
                    synchronized (numbers) {
                        numbers.add(number);
                        broadcastNumbers();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    clients.remove(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void broadcastNumbers() {
        for (Socket client : clients) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                out.println(numbers);
            } catch (IOException e) {
                e.printStackTrace();
                clients.remove(client);
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        try {
            Servidor server = new Servidor(5542);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}