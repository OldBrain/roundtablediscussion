package frontend.messages;


public class FactoryMsg {
  public Msg greatMsg(MsgType typeOfMsg, String textMsg) {

    switch (typeOfMsg) {
      case IN:
        return new InMsg(textMsg);
      case OUT:
        return new OutMsg(textMsg);
      case SYSTEM:
        return new SysMsg(textMsg);
      default:
        return null;
    }

  }


}
