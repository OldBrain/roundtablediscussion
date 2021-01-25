package sockedprocessing;

import java.io.IOException;

// Способ обработки socked
public interface MethodSockedProcessing {

  public void outMsg(String outMsg) throws IOException;

  public String getInMsg() throws IOException;
}
