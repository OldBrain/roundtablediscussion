import constants.Commands;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

public class DbController implements AuthServece {


  private Connection conn;
  private Statement st;

  PreparedStatement psOnline;
  PreparedStatement psHistory;
  PreparedStatement psReg;

  public DbController() {
    try {
      connect();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  public void connect() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    conn = DriverManager.getConnection("jdbc:sqlite:dbchat.db");
//    conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    st = conn.createStatement();
//st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
  }

  @Override
  public void getHistory(String commands, int towhom, Server server, ClientHandler clientHandler) {

    try {
      String sql = "SELECT date,msgtext FROM msg  WHERE ( towhom = 0 or towhom=" + towhom + ");";
      System.out.println(sql);
      ResultSet resultS = st.executeQuery(sql);
      while (resultS.next()) {
        String s = "[" + resultS.getString("date").toString() + "]" + resultS.getString("msgtext");
        server.broadcastHistory(clientHandler, s, towhom);
      }


    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      try {
        st.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }


  }

  @Override
  public void addMsg(int towhom, String text, int fromWhom) {

    String sql = "INSERT INTO msg (towhom,msgtext,fromwhom,date) VALUES (" + towhom + ",'" + text + "'," + fromWhom + ",date('now'));";
    try {
      st.executeUpdate(sql);

    } catch (SQLException throwables) {
      throwables.printStackTrace();
//      System.out.println(throwables);
    } finally {
      try {
        st.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }
  }


  @Override

  public String getNicName(String login, String password) {
    ResultSet rsGetNick = null;
    String nick = null;
    try {
      rsGetNick = st.executeQuery("select * from users where login=" + login + " and password=" + password + ";");
      if (rsGetNick.next()) {
        nick = rsGetNick.getString("nick");
        return nick;
      } else {
        return null;
      }

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      try {
        rsGetNick.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }
    return nick;
  }

  @Override
  public int getId(String nickName) {
    ResultSet rsGetNick = null;
    int id = 0;
    try {
      rsGetNick = st.executeQuery("select * from users where nick='" + nickName + "';");
      if (rsGetNick.next()) {
        id = rsGetNick.getInt("id");
        return id;
      } else {
        return 0;
      }

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      try {
        rsGetNick.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }
    return id;
  }

  @Override
  public int getId(String login, String password) {
    ResultSet rsGetNick = null;
    int id = 0;
    try {
      rsGetNick = st.executeQuery("select * from users where login=" + login + " and password=" + password + ";");
      if (rsGetNick.next()) {
        id = rsGetNick.getInt("id");
        return id;
      } else {
        return 0;
      }

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {
      try {
        rsGetNick.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }
    return id;
  }

  @Override
  public boolean registration(String login, String pass, String name) {
    try {
      psReg = conn.prepareStatement("SELECT * FROM users WHERE (login=? AND nick=? AND password=?);");
      psReg.setString(1, login);
      psReg.setString(2, pass);
      psReg.setString(3, name);
      psReg.executeQuery();

      if (psReg.executeQuery().next()) {
        return false;

      } else {

        psReg = conn.prepareStatement("INSERT INTO users (login,nick,password,online) VALUES (?,?,?,true)");
        psReg.setString(1, login);
        psReg.setString(2, pass);
        psReg.setString(3, name);
        psReg.executeUpdate();

      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    } finally {

    }

    return true;
  }


  @Override
  public boolean setName(String name, int id) {

    try {
      String sql = "UPDATE users SET nick='" + name + "' WHERE id=" + id + ";";
//      System.out.println(sql);;
      int result = st.executeUpdate(sql);
      if (result != 0) {
        return true;
      } else {
        return false;
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
      return false;
    } finally {
      try {
        st.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }

  }


  public void disConnect() {
    try {
      st.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    try {
      conn.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

  }
}