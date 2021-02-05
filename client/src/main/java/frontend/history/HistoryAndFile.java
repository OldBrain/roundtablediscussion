package frontend.history;

import java.io.*;
import java.nio.Buffer;
import java.util.*;


public class HistoryAndFile implements HistoryProcessing  {
  final static int BUFFER_SIZE = 10;
  Queue<String> writeHistory = new LinkedList<>();
  Queue<String> readHistory = new LinkedList<>();

  File file;
  String fileName;
  final String PATH="history/";
  FileWriter writer;
  BufferedReader reader;


  public HistoryAndFile(String clientName) {
    this.fileName = PATH+"history_"+clientName+".txt";

    file = new File(fileName);
    try {
      checkFile();

    } catch (Exception ioException) {
      ioException.printStackTrace();
      System.out.println("Ошибка чтения/записи истории сообщений");

      try {
        writer.close();
        reader.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  private void checkFile() throws IOException {
    if (!file.exists()) {
      file.createNewFile();
    }
    fileName = file.getCanonicalPath();
  }

  private void saveToQueue(String msg) {
    writeHistory.add(msg+"\n");
    if (writeHistory.size()>=BUFFER_SIZE) {
      saveToFile();
    }

  }

  @Override
  public Queue<String>  getHistory(int count) {
    return getHistoryFromFile(count);
  }

  private Queue<String> getHistoryFromFile(int count)  {
    int i = 0;
    try {
      reader = new BufferedReader(new FileReader(fileName));
        reader.lines().forEach(o ->{
          readHistory.add(o);
          if (readHistory.size() >= count) {
            throw new RuntimeException();

          }

        });
    } catch (Exception e) {
//      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

    return readHistory;
  }

  @Override
  public void saveHistory(String msg) {
    saveToQueue(msg);
  }

  private void saveToFile()  {
    FileWriter out = null;

    try {
      out = new FileWriter(fileName,true);


      int size = writeHistory.size();
      for (int i = 0; i < size; i++) {
         out.write(writeHistory.poll());
      }
    } catch (IOException ioException) {
      ioException.printStackTrace();

    }finally {
      try {
        out.flush();
        out.close();
        System.out.println(writeHistory.toString());
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }

  @Override
  public void clearBuffer() {
    saveToFile();
    int size = writeHistory.size();
    for (int i = 0; i < size; i++) {
      writeHistory.poll();
    }
  }


}


