import java.util.ArrayList;
import java.util.List;

public class SimpleAuthServece implements AuthServece {

  private class UserData {
    String login;
    String password;
    String nicName;

    public UserData(String login, String password, String nicName) {
      this.login = login;
      this.password = password;
      this.nicName = nicName;
    }
  }

  private List<UserData> users;

  public SimpleAuthServece() {
    users = new ArrayList<UserData>();
    users.add(new UserData("qwe", "qwe", "qwe"));
    users.add(new UserData("asd", "asd", "asd"));
    users.add(new UserData("zxc", "zxc", "zxc"));
    users.add(new UserData("111", "111", "111"));
  }

  @Override
  public String getNicName(String login, String password) {
    for (UserData user : users) {
      if (user.login.equals(login) && user.password.equals(password)) {
        return user.nicName;
      }
    }
    return null;
  }
}
