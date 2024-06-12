package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Cliente(String host, int port) throws IOException {
        socket = new Socket("192.168.1.16", 5542);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendNumber(int number) {
        out.println(number);
    }

    public void startListening() {
        new Thread(new Listener()).start();
    }

    private class Listener implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("NUMEROS ALMACENADOS EN EL SERVIDOR " + message);
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
    }

    public static void main(String[] args) {
        String host = "192.168.1.16";
        int port = 5542;
        try (Scanner scanner = new Scanner(System.in)) {
            Cliente client = new Cliente("192.168.1.16", 5542);
            System.out.print("Ingrese un numero para enviar al servidor: ");
            int number = scanner.nextInt();
            client.sendNumber(number);
            client.startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

