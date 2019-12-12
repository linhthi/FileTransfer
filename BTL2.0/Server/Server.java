import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class SocketThread extends Thread {
    private Socket conn; // Socket connect server with client
    private static final int BUFSIZE = 4096; // buffer size
    private static final String menu = "1. List file\n2. Sendfile to cleint\n3. Close server";
    
    // Constructor
    public SocketThread(Socket conn) {
        this.conn = conn;
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
            String cmd = null;

            // Server serve
            while(true) {
                System.out.println(menu);
                cmd = scanner.nextLine();
                if (cmd.equals("1")) { // List file server has
                    File dir = new File("SharedFlode");
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
                    long filesize = 0;
                    System.out.println("Enter filename:");
                    String fileName = scanner.nextLine();
                    fileName.trim();
                    File file = new File("SharedFolder/"+fileName);
                    FileInputStream fis = new FileInputStream(file);
                    filesize = file.length();
                    if (filesize == 0) {
                        System.out.println("Can't opent: "+ fileName);
                    }
                    else {
                        System.out.print("Sent file to cleint: " + addr);
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
                        if (fileError) 
                            continue;
                        System.out.println("Sending file " + fileName + " to " + addr.toString() + "done!");
                    }
                }
                else if (cmd.equals("3")) {

                }
                else {
                    System.out.println("Ops! Wrong cmd!");
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing connection to " + addr.toString()+ ":" + port);
        }

    }
}
class Server {
    public static final int PORT = 4000;
    public static void main(String[] args) {
        ServerSocket servSock = null;
        try {
            servSock = new ServerSocket(PORT);
            servSock.setReuseAddress(true);
            Socket conn = null;
            while (true) {
                try {
                    conn = servSock.accept();
                    SocketThread st = new SocketThread(conn);
                    st.start();    
                }
                catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}