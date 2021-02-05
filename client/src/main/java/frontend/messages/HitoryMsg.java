package frontend.messages;

public class HitoryMsg extends Messages implements Msg {



  public HitoryMsg(String text) {
    super(text);
    this.setStyle("-fx-background-color: #4B4A54; -fx-text-fill: #FFFFFF;-fx-background-radius: 1em;");

  }

  @Override
  public void showMsg() {

  }
}
