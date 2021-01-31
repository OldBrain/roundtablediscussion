import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    public String getLogin() {
      return login;
    }
  }

  private CopyOnWriteArrayList<UserData> users;

  public SimpleAuthServece() {
    users = new CopyOnWriteArrayList<UserData>();
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

  @Override
  public boolean registration(String login, String pass, String name) {
    boolean ok = users.stream().noneMatch(userData -> userData.login.equals(login)
        || userData.nicName.equals(name)
    );
    if (ok) {
      users.add(new UserData(login, pass, name));
    }
    return ok;

  }
}
