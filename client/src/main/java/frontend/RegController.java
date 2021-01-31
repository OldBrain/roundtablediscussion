package frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.awt.*;

public class RegController {



  Controller controller = new Controller();
  @FXML
  public TextField autLogin;
  @FXML
  public TextField autPass;
  @FXML
  public TextField autName;
  @FXML
  private TextArea autMsg;

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void trayReg(ActionEvent actionEvent) {
    String login = autLogin.getText().trim();
    String pass = autPass.getText().trim();
    String nickName = autName.getText().trim();

    controller.trayToReg(login, pass, nickName);

  }

  public void regOk(){
    autMsg.appendText("Регистрация прошла успешно\n");
  }
  public void regNo(){
    autMsg.appendText("Отказано в регистрации\n");
  }

}
