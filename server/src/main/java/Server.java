import constants.Commands;

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
  //  private AuthServece authServece;
  private AuthServece authServece;

  public Server() {
    clients = new CopyOnWriteArrayList<>();
//    authServece = new SimpleAuthServece();
    authServece = new DbController();
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

  public void broadcastHistory(ClientHandler clientHandler, String msg, int id) {
    for (ClientHandler ch : clients) {
      if (ch.id == id) {
        ch.sendMsg(msg);
      }
    }
  }

  public void broadcastMsg(ClientHandler clientHandler, String msg) {
    String message = String.format("[%s:] %s", clientHandler.getNikName(), msg);
    for (ClientHandler ch : clients) {

      ch.sendMsg(message);
    }
  }


  public void broadcastClientsList() {
    StringBuilder sb = new StringBuilder(Commands.ClIENTS_LIST);

    for (ClientHandler c : clients
    ) {
      sb.append(" ").append(c.getNikName());
    }
    String msg = sb.toString();
    for (ClientHandler c : clients
    ) {
      c.sendMsg(msg);
    }

  }

  void privateMsg(ClientHandler clientHandler, String msg) {
    String[] token = msg.split("\\s", 3);
    String privMsg = String.format("[%s:]->[%s] %s", clientHandler.getNikName(), token[1], token[2]);

    for (ClientHandler ch : clients) {
      if (ch.getNikName().equals(token[1])) {
//        String message = String.format("[%s:]->[%s] %s", clientHandler.getNikName(), token[1], token[2]);
        ch.sendMsg(privMsg);
        authServece.addMsg(ch.id, privMsg, clientHandler.id);
        clientHandler.sendMsg(privMsg);
        return;
      }
    }
    clientHandler.sendMsg("#Получателя [" + token[1] + "] нет в сети! Как только появится доставлю.");
    authServece.addMsg(authServece.getId(token[1]), privMsg, clientHandler.id);
  }

  void subscribe(ClientHandler clientHandler) {
    clients.add(clientHandler);

    broadcastClientsList();
  }

  void unSubscribe(ClientHandler clientHandler) {
    clients.remove(clientHandler);
    broadcastClientsList();
  }

  public AuthServece getAuthServece() {
    return authServece;
  }

  public boolean isLoginExist(String login) {
    return clients.stream().anyMatch(o -> o.getLogin().equals(login));
  }
}
