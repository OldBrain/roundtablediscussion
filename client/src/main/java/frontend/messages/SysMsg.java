package frontend.messages;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class SysMsg extends TextField implements Msg{

  public SysMsg(String text) {
    super(text);
    this.setStyle("-fx-background-color: #000000;-fx-text-fill: #FBCF00; -fx-background-radius: 1em;");

  }

  @Override
  public void showMsg() {

  }
}
