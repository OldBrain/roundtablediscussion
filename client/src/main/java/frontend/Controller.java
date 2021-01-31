package frontend;

import constants.Commands;
import frontend.messages.MsgType;
import frontend.messages.FactoryMsg;
import frontend.messages.Msg;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sockedprocessing.DISMethod;
import sockedprocessing.MethodSockedProcessing;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;


public class Controller implements Initializable {

  private final String IP = "localhost";
  private final int PORT = 8189;
  private MethodSockedProcessing socketProcessing;
  private Scanner scanner;
  Socket socket;
  private Stage stage;
  private Stage regStage;
  private boolean identification;
  private FactoryMsg factoryMsg = new FactoryMsg();
  private RegController regController;
  @FXML
  private Button sendButton;
  @FXML
  private TextArea sendText;
  @FXML
  private VBox msgPanel;
  @FXML
  private AnchorPane autPanel;
  @FXML
  private AnchorPane sendPanel;
  @FXML
  private TextField textLogin;
  @FXML
  private TextField textPass;
  @FXML
//  private ListView<String> clientsList;
  private ListView<String> clientsList;

  private String outText;
  private String nickName;


  public Controller() {
  }


  @FXML
  public void clickSendButton(ActionEvent actionEvent) {


    try {
      socketProcessing.outMsg(sendText.getText());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
//    msgInPanel(MsgType.OUT, sendText.getText());
    sendText.clear();
    sendText.requestFocus();
  }


  public void setIdentification(boolean identification) {
    this.identification = identification;
    sendPanel.setVisible(identification);
    sendPanel.setManaged(identification);

    autPanel.setVisible(!identification);
    autPanel.setManaged(!identification);

    if (!identification) {
      nickName = "";
    } else {

    }
    setTile(nickName);
    Platform.runLater(() -> {

      msgPanel.getChildren().clear();
    });
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Platform.runLater(() -> {
      stage = (Stage) msgPanel.getScene().getWindow();
      stage.setOnCloseRequest(event -> {
        if (socket != null && !socket.isClosed()) {
          try {
            socketProcessing.outMsg(Commands.END);
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }
        }
      });
    });

    setIdentification(false);


    msgPanel.setSpacing(5);// Отступ между элементами
  }


  private void connect() {
    try {
      socket = new Socket(IP, PORT);
      System.out.println("Client connect OK!");
      msgInPanel(MsgType.SYSTEM, "Client connect OK! :" + socket.getLocalPort());
      socketProcessing = new DISMethod(socket);
    } catch (IOException ioException) {
//      System.out.println("#the server is not available");
      msgInPanel(MsgType.SYSTEM, "no connection to the server");
    }
    // Входящие сообщения
    Thread threadIn = new Thread(new Runnable() {
      public void run() {

        try {


// Аудентификация

          while (true) {
            String tmpText = socketProcessing.getInMsg();
            if (tmpText.startsWith("/")) {

              if (tmpText.equals(Commands.REG_OK)) {
                regController.regOk();
              }

              if (tmpText.equals(Commands.REG_NO)) {
                regController.regNo();
              }


              if (tmpText.startsWith(Commands.AUTOK)) {
                nickName = tmpText.split("\\s")[1];
                setIdentification(true);

                break;
              }

              if (tmpText.equals("/end")) {
                msgInPanel(MsgType.SYSTEM, "Вы отключены.");
                setIdentification(false);
                throw new RuntimeException("Сервер отключил нас");
              }

            } else {
              if (tmpText.startsWith("[" + nickName)) {
                msgInPanel(MsgType.OUT, tmpText);
              } else {

                msgInPanel(MsgType.IN, tmpText);
              }

            }

          }


          // Работа
          while (true) {
            String tmpText = socketProcessing.getInMsg();
            if (tmpText.startsWith("/")) {

              if (tmpText.startsWith(Commands.ClIENTS_LIST)) {
                String[] token = tmpText.split("\\s");

                Platform.runLater(() -> {

                  clientsList.getItems().clear();

                  for (int i = 1; i < token.length; i++) {
                    clientsList.getItems().add(token[i]);
                  }

                });

              }

              if (tmpText.equals("/end")) {
                msgInPanel(MsgType.SYSTEM, "Вы отключены.");
                setIdentification(false);
                break;
              }
            } else {

              if ((tmpText.startsWith("#"))) {
                msgInPanel(MsgType.SYSTEM, tmpText);
              } else {
                if (tmpText.startsWith("[" + nickName)) {
                  msgInPanel(MsgType.OUT, tmpText);
                } else {
                  msgInPanel(MsgType.IN, tmpText);
                }
              }
            }
          }

        } catch (IOException ioException) {
//          ioException.printStackTrace();
          System.out.println("The server is not available");
          msgInPanel(MsgType.SYSTEM, "The server is not available");
        } finally {
          try {
            System.out.println("The server is not available. Socket close");
            msgInPanel(MsgType.SYSTEM, "No connection to the server. Socket close");
            setIdentification(false);

            socket.close();
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }

        }
      }
    });
    threadIn.start();
  }

  public void msgInPanel(MsgType type, String text) {
    Msg newMsg = factoryMsg.greatMsg(type, text);
    Platform.runLater(() -> {
      msgPanel.getChildren().add((TextField) newMsg);
    });
//    sendText.requestFocus();
  }

  public void btRegClick(ActionEvent actionEvent) {
    if (regStage == null) {
      createRegWindow();
      regStage.show();
    } else {
      regStage.show();
    }
  }

  private void createRegWindow() {
    try {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/auth.fxml"));
      regStage = new Stage();
      Parent root = fxmlLoader.load();
      regStage.setTitle("Chat registration");
      regStage.setScene(new Scene(root, 600, 600));
      regController = fxmlLoader.getController();
      regController.setController(this);
      ((RegController) fxmlLoader.getController()).setController(this);
      regStage.initModality(Modality.APPLICATION_MODAL);
      regStage.initStyle(StageStyle.UTILITY);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

  }

  public void trayToReg(String login, String password, String nickName) {
    if (socket == null || socket.isClosed()) {
      connect();
    }

    String msg = String.format("%s %s %s %s", Commands.REGISTRATION, login, password, nickName);
    try {
      socketProcessing.outMsg(msg);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

  }

  public void btExitClick(ActionEvent actionEvent) {
  }

  public void btLoginClick(ActionEvent actionEvent) {
    if (socket == null || socket.isClosed()) {
      connect();
    }
    if (textLogin.getText().trim().equals("") || textPass.getText().trim().equals("")) {
      textLogin.requestFocus();
      msgInPanel(MsgType.SYSTEM, "Пустой логин или пароль.");
      return;
    }
    String msg = String.format(Commands.AUT + " %s %s", textLogin.getText().trim(), textPass.getText().trim());

    try {
      socketProcessing.outMsg(msg);
      textPass.clear();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  private void setTile(String nick) {
    if (nick == "") {
      Platform.runLater(() -> {
        stage.setTitle("");
      });
    } else {

      Platform.runLater(() -> {
        String title = String.format("RoundTable [%s]", nick);
        stage.setTitle(title);
      });

    }
  }


  public void commandPrivate(MouseEvent mouseEvent) {
    System.out.println(clientsList.getSelectionModel().getSelectedItem());
    sendText.setText(Commands.PRIV + " " + clientsList.getSelectionModel().getSelectedItem());
  }


}