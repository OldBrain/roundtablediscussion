package frontend.history;

import java.util.Queue;

public interface HistoryProcessing {
  public Queue<String> getHistory(int count);
  public void saveHistory(String msg);
  public void clearBuffer();
}
