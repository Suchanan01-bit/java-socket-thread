import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client {

  private Socket socket;
  private DataInputStream in = null;
  private DataOutputStream out = null;
  public static final String DOWNLOAD_FOLDER = "./download";
  Scanner scanner = new Scanner(System.in);
  public Client() {
    try{
      socket = new Socket("localhost",42200);
      in = new DataInputStream((socket.getInputStream()));
      out = new DataOutputStream(socket.getOutputStream());
      

      getFile();
    }  catch (IOException e){
      e.printStackTrace();
    }
  }

  private void getFile() throws IOException{
    int allVideoFile = in.readInt();
    String[] files = new String[allVideoFile+1];
    System.out.println("Avaiable files list: ");
    for(int i =1 ;i<=allVideoFile;i++){
      files[i] = in.readUTF();
      System.out.println(i + " -- " +files[i]);
    }
    
    System.out.print("Choose files to download: ");
    int userSelection = 0;
    boolean isSelectionCorrect = false;
    while (!isSelectionCorrect) {
      System.out.println("Select a file number : ");
      userSelection = scanner.nextInt();
      isSelectionCorrect = userSelection>0 && userSelection<=allVideoFile;
    }
    if(isSelectionCorrect){
        String chosenFile = files[userSelection];
      out.writeUTF(chosenFile);
      receiveVideoFile(chosenFile);
    }
    
  }
  private void receiveVideoFile(String chosenFile) throws IOException {
    long fileSize = in.readLong();
    if (fileSize <= 0) {
        System.out.println("File not found or empty. Download aborted.");
        return;
    }
    else if (fileSize > 0) {
        File file = new File(DOWNLOAD_FOLDER + "/" + chosenFile);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int count;
        while (fileSize > 0 && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) > 0) {
            fos.write(buffer, 0, count);
            fileSize -= count;
           
        }
        fos.close();
        System.out.println("File downloaded: " + chosenFile);
    } else {
        System.out.println("File not found on server.");
    }
}

public static void main(String[] args) {
    new Client();
  }
}
