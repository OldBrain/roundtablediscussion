import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
  private ServerSocket server;
  private Socket socket;
  private final int PORT = 8189;
  private List<ClientHandler> clients;
  private AuthServece authServece;

  public Server() {
    clients = new CopyOnWriteArrayList<>();
    authServece = new SimpleAuthServece();
    try {
      server = new ServerSocket(PORT);
      System.out.println("Server started");

      while (true) {
        socket = server.accept();
        System.out.println("Client connected: " + socket.getLocalPort());
        new ClientHandler(this, socket);


      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        System.out.println("Server CLOSE!");
        server.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void broadcastMsg(ClientHandler clientHandler, String msg) {
    String message = String.format("[%s:] %s", clientHandler.getNikName(), msg);
    for (ClientHandler ch : clients) {
      ch.sendMsg(message);
    }
  }

  void privateMsg(ClientHandler clientHandler, String msg) {
    String[] token = msg.split("\\s", 3);

    for (ClientHandler ch : clients) {
      if (ch.getNikName().equals(token[1])) {
        String message = String.format("[%s:]->[%s] %s", clientHandler.getNikName(), token[1], token[2]);
        System.out.println(message);
        ch.sendMsg(message);
        clientHandler.sendMsg(message);
        return;
      }
    }
    clientHandler.sendMsg("#Получателя [" + token[1] + "] нет в сети!");

  }

  void subscribe(ClientHandler clientHandler) {
    clients.add(clientHandler);
  }

  void unSubscribe(ClientHandler clientHandler) {
    clients.remove(clientHandler);
  }

  public AuthServece getAuthServece() {
    return authServece;
  }
}
