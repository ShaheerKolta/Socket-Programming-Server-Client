import java.io.*;
import java.net.*;
import java.util.StringTokenizer;


public class JavaHTTPServer implements Runnable{ 
	
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	// port to listen connection
	static final int PORT = 8080;
	

	
	// Client Connection via Socket Class
	private Socket connect;
	
	public JavaHTTPServer(Socket c) {
		connect = c;
	}
	
	public static void main(String[] args) {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
			// we listen until user halts server execution
			while (true) {
				JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());

				
				// create dedicated thread to manage the client connection
				Thread thread = new Thread(myServer);
				thread.start();
			}
			
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		BufferedInputStream dataIn = null;
		String fileRequested = null;
		
		try {
			// we read characters from the client via input stream on the socket
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			// we get character output stream to client (for headers)
			out = new PrintWriter(connect.getOutputStream());
			// get binary output stream to client (for requested data)
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			// get first line of the request from the client
			String input = in.readLine();
			System.out.println("Request recieved : \n"+input);
			// we parse the request with a string tokenizer
			StringTokenizer parse = new StringTokenizer(input);
			// we get the HTTP method of the client
			String method = parse.nextToken().toUpperCase();
			// we get file requested
			fileRequested = parse.nextToken().toLowerCase();
			
			// we support only GET and HEAD methods, we check
			if (!method.equals("GET")  &&  !method.equals("PUT")) {
				
				System.out.println("NOT SUPPORTED METHOD");
				
			} else {
				// GET or PUT method
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}
				
				File file = new File("C:\\Users\\Shaheer\\Desktop\\Engineering\\Computer Networks\\project 1\\New folder\\server" + fileRequested);
				int fileLength = (int) file.length();
				String content = getContentType(fileRequested);
				
				if (method.equals("GET")) { // GET method so we return content
					byte[] fileData = readFileData(file, fileLength);
					
					// send HTTP Headers
					out.println("HTTP/1.0 200 OK");
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println(); // blank line between headers and content, very important !
					out.flush(); // flush character output stream buffer
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
					System.out.println("File " + fileRequested + " of type " + content + " returned");
				}
				else {
					//method is PUT
					input = in.readLine();
					System.out.println(input);

					input = in.readLine();
					System.out.println(input);

					parse = new StringTokenizer(input);
					String length = parse.nextToken();
					length = parse.nextToken();

					int len = Integer.parseInt(length);


					dataIn = new BufferedInputStream(connect.getInputStream());
					File recieved = new File("C:\\Users\\Shaheer\\Desktop\\Engineering\\Computer Networks\\project 1\\New folder\\server\\"+fileRequested);
					FileOutputStream stream = new FileOutputStream(recieved);
					DataOutputStream outputFile = new DataOutputStream(stream);

					for (int i=0;i<=len;i++) {
						outputFile.write(dataIn.read());
					}
					outputFile.close();
					System.out.println("File " + fileRequested + " of type " + content + " recieved");

				}

				
			}
			
		} catch (FileNotFoundException fnfe) {

			try {
				fileNotFound(out, dataOut, fileRequested);
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}

			
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			

			System.out.println("Connection closed.\n");

		}
		
		
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		
		return fileData;
	}
	
	// return supported MIME Types
	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			return "html";
		else
			return "jpg";
	}

	private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File("C:\\Users\\Shaheer\\Desktop\\Engineering\\Computer Networks\\project 1\\New folder\\server\\" + FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);

		out.println("HTTP/1.0 404 File Not Found");
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer

		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();


		System.out.println("File " + fileRequested + " not found");

	}

}
