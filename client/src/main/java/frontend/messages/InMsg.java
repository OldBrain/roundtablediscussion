package frontend.messages;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class InMsg extends Messages implements Msg {

  public InMsg(String text) {
    super(text);
    this.setStyle("-fx-background-color: #F9F871; -fx-background-radius: 1em;");

  }


  public void showMsg() {

  }
}
