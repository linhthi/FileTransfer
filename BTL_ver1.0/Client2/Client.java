import java.io.*;
import java.net.*;
import java.util.*;

class Client {
	public static void main(String[] args){
		System.out.println("Enter server's IP addr and port in the form of <IP>:<port>");
		Scanner scanner = null;
		String[] tokens = null;
		Socket servSock = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		try{
			scanner = new Scanner(System.in);
			tokens = scanner.nextLine().split(":");
			String host = tokens[0];
			int port = Integer.parseInt(tokens[1]);

            InetAddress addr = InetAddress.getByName(host);
            // InetAddress addr = InetAddress.getByName("127.0.0.1");
            // int port = 4000;
			servSock = new Socket(addr, port);

			out = new DataOutputStream(servSock.getOutputStream());
			in = new DataInputStream(servSock.getInputStream());
			byte[] buff = new byte[4096];

			while (true){
				String s = null;
                String filename = in.readUTF();
                System.out.println("Filename: "+ filename);
                if (filename.equals("@logout")) {
                    break;
                }
                //read filesize
                s = in.readUTF();
                long filesize = Long.parseLong(s);
                if (filesize == 0){
                    System.out.println("Cannot download the file");
                    continue;
                }
                System.out.println("filesize: " + filesize);

                long nBytesRead = 0;
                //File file = new File(filename);
                FileOutputStream fos = new FileOutputStream(filename);

                try{
                    while (nBytesRead < filesize){
                        //the number of byte to read 
                        long needToRead = 0;
                        if (filesize - nBytesRead < 4096)
                            needToRead = filesize - nBytesRead;
                        else
                            needToRead = 4096;

                        int nBytes = in.read(buff, 0, (int)needToRead);
                        nBytesRead += nBytes;
                        fos.write(buff, 0, nBytes);
                    }
                }catch (SocketTimeoutException ex){
                    System.err.println("encountered error while reading the file at the server side");
                    fos.close();
                    continue;
                }
                System.out.printf("downloading '%s' done\n", filename);
                out.writeUTF("done");
                //fos.close();
            
			}
			//servSock.close();
			// scanner.close();
		} catch (IOException ex){
			ex.printStackTrace();
			System.exit(-1);
		//} catch (UnknownHostException ex){
		//	ex.printStackTrace();
		//	System.exit(-1);
		}
		
	}
}