package WebServer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientConnection extends Thread {
	private Socket socket;
	private BufferedReader clientIn;
	private PrintStream clientOut;
	private InputStream i;
	private String stream;
	private String filename;
	private StringTokenizer tokenizer;
	private String fileType;
	private String error = "HTTP/1.0 404 Not Found\r\n" + "Content-type: text/html\r\n\r\n"+ "<html><head></head><body>FILE NOT FOUND!</body></html>\n";

	public ClientConnection(Socket s) {
		socket = s;
		start();
	}

	public void run() {
		try {
			clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			clientOut = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

			// READ THIS FROM STREAM: "GET /filename.html ..."
			stream = clientIn.readLine();
			System.out.println("          - " + stream);  // Log the request

			filename = "";
			tokenizer = new StringTokenizer(stream);
			
			try {
				// GET FILENAME FROM THE "GET..."
				if(tokenizer.hasMoreElements() && tokenizer.nextToken().equalsIgnoreCase("GET") && tokenizer.hasMoreElements())
					filename = tokenizer.nextToken();
				else
					throw new FileNotFoundException();  // SORRY WALANG FILE :(

				fixFilename(filename);
				determineFileTypeAndHeader(filename);
				
				System.out.println("Filename:" + filename);
				
				i = new FileInputStream(filename);
				byte[] a = new byte[4096];
				int n;
				
				while((n = i.read(a)) > 0) {
						clientOut.write(a, 0, n);
				}
						
				clientOut.close();
			} catch(FileNotFoundException e) {
				clientOut.println(error);
				clientOut.close();
			}
		} catch(IOException x) {
			System.out.println(x);
		}
	}
	
	public void determineFileTypeAndHeader(String filename) {
		fileType = "text/plain";

		if(filename.endsWith(".html") || filename.endsWith(".htm"))
			fileType = "text/html";
		
		clientOut.print("HTTP/1.0 200 OK\r\n" + "Content-type: " + fileType + "\r\n\r\n");
	}
	
	public void fixFilename(String filename) {
		if(filename.endsWith("/")) // IF ENDS WITH "/", TURN TO "/index.html"
			filename += "index.html";

		while(filename.indexOf("/")==0) // REMOVE EXTRA "/" FROM THE FILENAME
			filename = filename.substring(1);

		filename = filename.replace('/', File.separator.charAt(0)); // REPLACE "\" WITH "/"
	}
}