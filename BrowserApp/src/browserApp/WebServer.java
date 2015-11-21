package browserApp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WebServer {
	private final static int port = 8080;
	private static ServerSocket listener;
	
	public static void main(String[] args) {
		System.out.println("[SERVER] The web server is running...");
		try {
			listener = new ServerSocket(port);
			while(true) {
				new ConnectionHandler(listener.accept()).start();
			}
		} catch (IOException e) {
			System.err.println("ERROR: " + e);
		}
	}
	
	/**
	 * Handler for every single client connection to the WebServer.
	 */
	private static class ConnectionHandler extends Thread {
		private final String message = "GET / HTTP/1.1";
		private Socket connection;
		private BufferedReader in;
        private PrintWriter out;
        private DataOutputStream dout;
        
		public ConnectionHandler(Socket connection) {
			this.connection = connection;
		}
		
		public void run() {
			try {
				initializeIOStreams();
				respondRequest(parseGetRequest());
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					closeConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void initializeIOStreams() throws IOException {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new PrintWriter(connection.getOutputStream(), true);
            dout = new DataOutputStream(connection.getOutputStream());
		}
		
		private String parseGetRequest() throws IOException {
			String getRequest = in.readLine();
			System.out.println("[SERVER] "+ getRequest);
			String[] tokens = getRequest.split(" ",3);
			System.out.println("[SERVER] "+ tokens[1].substring(1));
			return tokens[1].substring(1);
		}
		
		private void respondRequest(String filename) throws IOException {
			Path source;
			if (filename.isEmpty()) {
				source = Paths.get(".\\GET Requests\\index.html");
				System.out.println("[SERVER] PATH 1");
			} else {
				source = Paths.get(".\\GET Requests\\"+filename);
				System.out.println("[SERVER] PATH 2");
			}
				
			//List<String> strLines = Files.readAllLines(source);
			byte[] data = Files.readAllBytes(source);
			dout.write(data);
			dout.flush();
			
			//for (String s : strLines) {
				//out.println(s);
			//}
		}
		
		private void closeConnection() throws IOException {
			in.close();
			out.close();
			dout.flush();
			connection.close();
		}
	}

}
