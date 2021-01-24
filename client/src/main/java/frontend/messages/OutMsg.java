package frontend.messages;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class OutMsg extends Messages implements Msg {

  public OutMsg(String text) {
    super(text);

    this.setStyle("-fx-background-color: #95A7DD; -fx-background-radius: 1em;");
  }


  public void showMsg() {

  }
}
