import constants.Commands;

import java.sql.SQLException;

public interface AuthServece {
  String getNicName(String login, String password) throws SQLException;

  boolean registration(String login, String pass, String name);

  public int getId(String login, String password);

  public boolean setName(String name, int id);

  public void addMsg(int id, String text, int toWhom);

  public int getId(String nickName);

  public void getHistory(String commands, int towhom, Server server, ClientHandler clientHandler);
}
