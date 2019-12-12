public class SocketThread extends Thread {
    private Socket clientSocket;
    private static final int BUFSIZE = 4096; // buffer size

    public SocketThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
    } 

}