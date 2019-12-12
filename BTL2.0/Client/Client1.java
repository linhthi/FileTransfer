import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import Client.SocketThread;

public class Client {
    public static final int PORT = 5000;
    public static String IP = "127.0.0.1"; // Test on local host
    public static int PORT_SERVER = 4000;
    public static final int BUF_SIZE = 4 * 1024;
    public static void connectCLient(ServerSocket servSock) {
        Socket conn = null;
        while (true) {
            try {
                conn = servSock.accept();
                SocketThread st = new SocketThread(conn);
                st.start();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    public static void connectServer(Socket conn) {
        Scanner scanner = new Scanner(System.in);
        DataInputStream in = new DataInputStream(conn.getInputStream());
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        byte[] buff = new byte[BUF_SIZE];


    }

    public static void main(String[] args) {
        Socket connServer = new Socket(IP, PORT_SERVER);
        ServerSocket socketListen = new ServerSocket(PORT);
        socketListen.setReuseAddress(true);
        connServer(connServer);
        connectCLient(socketListen);

        while(true) {
            System.out.println("If you want to quit enter: logout");
            String s = Scanner.nextLine();
            if (s.equals("logout")) {
                break;
            }
            connServer(connServer);
            connectCLient(socketListen);
        }
    }       

}