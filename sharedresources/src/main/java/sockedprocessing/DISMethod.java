package sockedprocessing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DISMethod implements MethodSockedProcessing {
  private DataInputStream in;
  private DataOutputStream out;
  private Socket socket;

  public DISMethod(Socket socket) {
    this.socket = socket;
    try {
      in = new DataInputStream(socket.getInputStream());
      out = new DataOutputStream(socket.getOutputStream());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public String getInMsg() throws IOException {
    return in.readUTF();
  }

  public void outMsg(String outMsg) throws IOException {
    out.writeUTF(outMsg);
  }
}
