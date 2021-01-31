public interface AuthServece {
  String getNicName(String login, String password);

  boolean registration(String login, String pass, String name);
}
