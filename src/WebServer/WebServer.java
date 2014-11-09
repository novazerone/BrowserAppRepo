package WebServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
	private static ServerSocket serverSocket;

	public static void main(String[] args) throws IOException {
		serverSocket = new ServerSocket(8080);
		
		System.out.println("<+> KDC SERVER IS STARTED.");
		System.out.println("    >> Awaiting connections.");
		
		while(true) {
			try {
				Socket socket = serverSocket.accept();
				new ClientConnection(socket);
				System.out.println("       -> New connection established with address: " + socket.getInetAddress());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}