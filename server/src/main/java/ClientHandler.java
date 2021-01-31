import constants.Commands;
import sockedprocessing.DISMethod;
import sockedprocessing.MethodSockedProcessing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
  private String nikName;
  private String login;
  private Server server;
  private Socket socket;
  private MethodSockedProcessing socketProcessing;


  public ClientHandler(Server server, Socket socket) {

    this.server = server;
    this.socket = socket;
    socketProcessing = new DISMethod(socket);


    new Thread(() -> {
      try {
        // Аудентификация
        socket.setSoTimeout(120000);
        while (true) {
          String str = socketProcessing.getInMsg().trim();
          if (str.indexOf('/') == 0) {

            if (str.startsWith(Commands.REGISTRATION)) {
              String[] token = str.split("\\s");
              if (token.length < 4) {
                continue;
              }
              boolean isRegistered = server.getAuthServece().registration(token[1], token[2], token[3]);
              if (isRegistered) {
                socket.setSoTimeout(0);
                socketProcessing.outMsg(Commands.REG_OK);
              } else {
                socketProcessing.outMsg(Commands.REG_NO);
              }
            }


            if (str.startsWith(Commands.AUT)) {
              String[] token = str.split("\\s");
              String newNick = server.getAuthServece()
                  .getNicName(token[1], token[2]);
              login = token[1];
              if (newNick != null) {

                if (!server.isLoginExist(login)) {
                  socket.setSoTimeout(0);
                  nikName = newNick;
                  socketProcessing.outMsg(Commands.AUTOK + " " + nikName);
                  System.out.println("Клиент подписан" + socket.getRemoteSocketAddress());
                  server.subscribe(this);
                  break;
                } else {

                }
                socketProcessing.outMsg("#Логин занят");

              } else {
                socketProcessing.outMsg("#Неверный логин или пароль.");
                socketProcessing.outMsg(Commands.AUTNO + " " + null);
              }
            }
            if (str.equals(Commands.END)) {
              sendMsg("/end");
              throw new RuntimeException("\"client disconnected\" >" + login);
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
//              System.out.println("Подучена команда приватного сообщения");
              //  String[] token =str.split("\\s");
              server.privateMsg(this, str);
            }


          } else {
//            System.out.println("#Получил сообщение: " + str);
            server.broadcastMsg(this, str);

          }


        }
      } catch (SocketTimeoutException e) {
        System.out.println("Неавторизованный клиент: " + socket.getRemoteSocketAddress() + " отключон по таймингу");
//        socketProcessing.outMsg("");
        sendMsg("/end");
      } catch (RuntimeException e) {
        System.out.println(e.getMessage());


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

  public String getLogin() {
    return login;
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
