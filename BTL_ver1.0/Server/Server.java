import java.io.*;
import java.net.*;
import java.util.*;

class Server {
    public static final int PORT = 4000;
    //private static final String menu = "1. List file\n2. Sendfile to cleint\n3. Close server";
    static Vector<ClientHandler> ar = new Vector<>();
    static int numClient = 0;
    public static void main(String[] args) {
        try {
            ServerSocket servSock = new ServerSocket(PORT);
            servSock.setReuseAddress(true);
            System.out.println("Server listen at port 4000");
            while (true) {
                Socket conn = servSock.accept();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                numClient++;
                ClientHandler clientHandler = new ClientHandler(conn, "Client" + numClient, numClient, dis, dos);
                InetAddress addr = conn.getInetAddress();
                int port = conn.getPort();
                System.out.println("Connecttion from client:" + addr.toString() + ":" + port);
                ar.add(clientHandler);
                clientHandler.start();
                if (numClient == 3) {
                    numClient = 0;
                } 
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

class ClientHandler extends Thread {
    private Socket conn; // Socket connect server with client
    private static final int BUFSIZE = 4096; // buffer size
    public static int totalFiledown = 0;
    private DataInputStream dis;
	private DataOutputStream dos;
    public String name;
    public static int numClient;
    public static long start;
	public static long end;
	public static long totalTime = 0;

    // Constructor
    public ClientHandler(Socket conn, String name, int numClient, DataInputStream dis, DataOutputStream dos) {
        this.conn = conn;
        this.name = name;
        this.numClient = numClient;
        this.dis = dis;
        this.dos = dos;
    }

    public void run() {

        // Prepare the IO Socket
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Number Client: " + numClient);
            String fileName;
            if (numClient == 3) {
                byte[] buff = new byte[BUFSIZE];
    
                ClientHandler c1 = Server.ar.get(0);
                ClientHandler c2 = Server.ar.get(1);
                ClientHandler c3 = Server.ar.get(2);

                while(true) {
                    // System.out.println("Enter file name want to send client");
                    // String fileName = scanner.nextLine();
                    File file = null;
                    while(true) {
                        System.out.print("\nEnter file's name to send to client: ");
                        fileName = scanner.nextLine();
                        start = System.currentTimeMillis();
                        System.out.println("Start time: " + start);
                        file = new File("SharedFolder/"+fileName);

                        if(file.exists() || fileName.equals("@logout")){
                            break;
                        }
                        else {
                            System.out.print("\nFile is not found!");
                        }
                    }

                    // Send exist to client
                    if (fileName.equals("@logout")) {
                        c1.dos.writeUTF(fileName);
                        c1.dos.flush();

                        c2.dos.writeUTF(fileName);
                        c2.dos.flush();

                        c3.dos.writeUTF(fileName);
                        c3.dos.flush();
                        break;
                    }
                    else {
                        //System.out.print("Sent filename  to cleint: " + addr);
                        c1.dos.writeUTF(fileName);
                        c1.dos.flush();

                        c2.dos.writeUTF(fileName);
                        c2.dos.flush();

                        c3.dos.writeUTF(fileName);
                        c3.dos.flush();

                        System.out.println("Sent file's name to all client.");
                        System.out.println("\nStart sending file directly...");
                        
                        // send file size
                        long filesize = file.length();
                        c1.dos.writeUTF(Long.toString(filesize));
                        c1.dos.flush();

                        c2.dos.writeUTF(Long.toString(filesize));
                        c2.dos.flush();

                        c3.dos.writeUTF(Long.toString(filesize));
                        c3.dos.flush();

                        // send file
                        int nBytes = 0;
                        FileInputStream fis = new FileInputStream(file);
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
                            c1.dos.write(buff, 0, nBytes);
                            c1.dos.flush();
                            c2.dos.write(buff, 0, nBytes);
                            c2.dos.flush();
                            c3.dos.write(buff, 0, nBytes);
                            c3.dos.flush();


                        }
                        if (fileError) ;
                        System.out.println("Sending file done!");
                        end = System.currentTimeMillis();
                        totalTime = (end - start);
                        System.out.println("Total time: " + totalTime + " ms");
                        increaseCount();
                    }

                }
                Server.ar.clear();
                System.out.println("Finish send file!\nWaiting connect...");
                c1.dis.close();
                c1.dos.close();
                c2.dis.close();
                c2.dos.close();
                c3.dis.close();
                c3.dos.close();
                conn.close();
                    
            }
    
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void increaseCount() {
        totalFiledown++;
    }
}