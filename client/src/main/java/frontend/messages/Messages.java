package frontend.messages;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public abstract class Messages extends TextField {
  public Messages(String text) {
    super(text);
    this.setFont(Font.font(15f));
    this.autosize();

    this.setMaxWidth(400);
    this.setMaxHeight(50);

    this.setEditable(false);

  }


}
