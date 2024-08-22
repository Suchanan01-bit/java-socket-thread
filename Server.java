import java.io.*;
import java.net.*;

public class Server {
  private ServerSocket serverSocket;
  public static final int PORT =  42200;
 

  public Server() {
    try{
      serverSocket = new ServerSocket(PORT);
      System.out.println("Server listening on port:"+PORT);
      acceptConnections();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void acceptConnections() throws IOException{
    while (true) {
      Socket clienSocket = serverSocket.accept();
      System.out.println("New client connected");
      if (clienSocket.isConnected()){ 
      ClientConnectionThread client = new ClientConnectionThread(clienSocket);
        client.start();
      }

        } 
    }
  
  public static void main(String[] args) {
    new Server();
  }
}

class ClientConnectionThread extends Thread {

  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;
  public static final String FILES_PATH = "./files";
   ClientConnectionThread(Socket socket) {
      this.socket = socket;
      try {
          in = new DataInputStream(socket.getInputStream());
          out = new DataOutputStream(socket.getOutputStream());
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
    
  @Override
  public void run() {
    try {
         sendList(out);
    String clientChoice = in.readUTF();
    File fileSend = new File(FILES_PATH+"/"+clientChoice);
    sendSelectedFile(fileSend);
    } catch (IOException ex) {
        ex.printStackTrace();
    }
   
  }
  private void sendSelectedFile(File fileToSend) throws IOException {
      if (fileToSend.exists() && !fileToSend.isDirectory()) {
        out.writeLong(fileToSend.length());
        byte[] buffer = new byte[4096];
        FileInputStream fis = new FileInputStream(fileToSend);
        int count;
        while ((count = fis.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        fis.close();
        System.out.println("File sent: " + fileToSend);
      } else {
        out.writeLong(0);
        System.out.println("File not found: " + fileToSend);
      }
    }


    

  public static  void sendList(DataOutputStream out)throws IOException{

      File folder = new File(FILES_PATH);
              File[] listOfFiles = folder.listFiles();
              if (listOfFiles != null) {
                  out.writeInt(listOfFiles.length);
                  for (File file : listOfFiles) {
                      out.writeUTF(file.getName());
                  }
              }
  }
}
