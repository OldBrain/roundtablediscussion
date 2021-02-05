package frontend.messages;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class InMsg extends Messages implements Msg {

  public InMsg(String text) {
    super(text);
    this.setStyle("-fx-background-color: #2F4858; -fx-text-fill: #FFFFFF; -fx-background-radius: 1em;");

  }


  public void showMsg() {

  }
}
