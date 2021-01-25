import constants.Commands;
import sockedprocessing.DISMethod;
import sockedprocessing.MethodSockedProcessing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
  private String nikName;
  private Server server;
  private Socket socket;
  private MethodSockedProcessing socketProcessing;


  public ClientHandler(Server server, Socket socket) {
//        try{
    this.server = server;
    this.socket = socket;
    socketProcessing = new DISMethod(socket);

//            in = new DataInputStream(socket.getInputStream());
//            out = new DataOutputStream(socket.getOutputStream());

    new Thread(() -> {
      try {
        // Аудентификация

        while (true) {
          String str = socketProcessing.getInMsg().trim();
          if (str.indexOf('/') == 0) {

            if (str.startsWith(Commands.AUT)) {
              String[] token = str.split("\\s");
              String newNick = server.getAuthServece()
                  .getNicName(token[1], token[2]);
              if (newNick != null) {
                nikName = newNick;
                server.subscribe(this);
                socketProcessing.outMsg(Commands.AUTOK + " " + nikName);
                System.out.println("Клиент подписан" + socket.getRemoteSocketAddress());
                break;
              } else {
                socketProcessing.outMsg("#Неверный логин или пароль.");
                socketProcessing.outMsg(Commands.AUTNO + " " + null);
              }
            }
            if (str.equals(Commands.END)) {
              System.out.println("client disconnected");
              sendMsg("/end");
              break;
            }
          }
        }


        // Работа
        while (true) {
          String str = socketProcessing.getInMsg().trim();
          if (str.indexOf('/') == 0) {


            if (str.equals(Commands.END)) {
              System.out.println("client disconnected");
              sendMsg("/end");
              break;
            }
            if (str.startsWith(Commands.PRIV)) {
              System.out.println("Подучена команда приватного сообщения");
              //  String[] token =str.split("\\s");
              server.privateMsg(this, str);
            }
          } else {

            System.out.println("#Получил сообщение: " + str);
            server.broadcastMsg(this, str);

          }


        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        server.unSubscribe(this);
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();

  }

  public void sendMsg(String msg) {
    try {
      socketProcessing.outMsg(msg);
//      System.out.println("Отправляю > " + msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getNikName() {
    return nikName;
  }
}
