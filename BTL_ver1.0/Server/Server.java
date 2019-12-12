import java.io.*;
import java.net.*;
import java.util.*;

class ClientHandler extends Thread {
    private Socket conn; // Socket connect server with client
    private static final int BUFSIZE = 4096; // buffer size
    public static int totalFiledown = 0;
    public String fileName; 
    // Constructor
    public ClientHandler(Socket conn, String fileName) {
        this.conn = conn;
        this.fileName = fileName;
    }

    public void run() {
        // Get address from client
        InetAddress addr = conn.getInetAddress();
        int port = conn.getPort();
        System.out.println("Connecttion from client:" + addr.toString() + ":" + port);

        // Prepare the IO Socket
        Scanner scanner = new Scanner(System.in);
        DataInputStream in = null;
        DataOutputStream out = null;

        try{
            in = new DataInputStream(conn.getInputStream());
            out = new DataOutputStream(conn.getOutputStream());
            byte[] buff = new byte[BUFSIZE];

            // Send file to client
            File file = new File("SharedFolder/"+fileName);
            FileInputStream fis = new FileInputStream(file);
            long filesize = file.length();
            if (filesize == 0) {
                System.out.println("Can't opent: "+ fileName);
            }
            else {
                System.out.print("Sent file to cleint: " + addr);
                out.writeUTF(fileName);
                out.flush();
                out.writeUTF(Long.toString(filesize));
                out.flush();
                int nBytes = 0;
                boolean fileError = false;
                while (nBytes != -1) {
                    try {
                        nBytes = fis.read(buff, 0, BUFSIZE);
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                        fileError = true;
                        fis.close();
                    }

                    if (nBytes < 0) {
                        break;
                    }
                    // Write data in Socket conn
                    out.write(buff, 0, nBytes);
                    out.flush();
                }
                if (fileError) ;
                System.out.println("Sending file " + fileName + " to " + addr.toString() + "done!");
                increaseCount();
            }
                
        }catch(IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing connection to " + addr.toString()+ ":" + port);
        }

    }

    public synchronized void increaseCount() {
        totalFiledown++;
    }
}
class Server {
    public static final int PORT = 4000;
    private static final String menu = "1. List file\n2. Sendfile to cleint\n3. Close server";
    public static void main(String[] args) {
        ServerSocket servSock = null;
        try {
            servSock = new ServerSocket(PORT);
            servSock.setReuseAddress(true);
            Scanner scanner = new Scanner(System.in);
            String cmd = null;
            while (true) {
                System.out.println(menu);
                cmd = scanner.nextLine();
                if (cmd.equals("1")) { // List file server has
                    File dir = new File("SharedFolder");
                    String[] children = dir.list();
                    if (children == null) {
                        System.out.println("Either dir does not exist or not is a directory");
                    }
                    else {
                        System.out.println("List file");
                        for (int i = 0 ; i < children.length; i++) {
                            System.out.println(children[i]);
                        }
                    }
                }
                else if (cmd.equals("2")) { // Send file to client
                    System.out.println("Enter filename:");
                    String fileName = scanner.nextLine();
                    fileName.trim();
                    int numClient = 0;
                    while(numClient < 3) {
                        Socket conn = servSock.accept();
                        ClientHandler st = new ClientHandler(conn, fileName);
                        numClient++;
                        System.out.println(numClient);
                        st.start(); 
                    }
   
                }
                else if (cmd.equals("3")) {

                }
                else {
                    System.out.println("Ops! Wrong cmd!");
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}