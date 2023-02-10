
import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class FxServer {

	public static void main(String[] args) throws Exception {

		try (ServerSocket ss = new ServerSocket(80)) {
			while (true) {
				System.out.println("Server waiting...");
				Socket connectionFromClient = ss.accept();
				System.out.println(
						"Server got a connection from a client whose port is: " + connectionFromClient.getPort());
				
				try {
					InputStream in = connectionFromClient.getInputStream();
					OutputStream out = connectionFromClient.getOutputStream();

					String errorMessage = "NOT FOUND\n";

					BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
					BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));

					DataInputStream dataIn = new DataInputStream(in);
					DataOutputStream dataOut = new DataOutputStream(out);

					String header = headerReader.readLine();
					StringTokenizer strk = new StringTokenizer(header, " ");

					String command = strk.nextToken();

					String fileName = strk.nextToken();

					if (command.equals("download")) {
						try {
							FileInputStream fileIn = new FileInputStream("ServerShare/" + fileName);
							int fileSize = fileIn.available();
							header = "OK " + fileSize + "\n";

							headerWriter.write(header, 0, header.length());
							headerWriter.flush();

							byte[] bytes = new byte[fileSize];
							fileIn.read(bytes);

							fileIn.close();

							dataOut.write(bytes, 0, fileSize);

						} catch (Exception ex) {
							headerWriter.write(errorMessage, 0, errorMessage.length());
							headerWriter.flush();

						} finally {
							connectionFromClient.close();
						}
					} else if (command.equals("upload")) {

				     
						
						 String temp= strk.nextToken();
						 int size = Integer.parseInt(temp);
						 byte[] space = new byte[size];

						 dataIn.readFully(space);

						 try (FileOutputStream fileOut = new FileOutputStream("ServerShare/" + fileName)) {
							fileOut.write(space, 0, size);
						}





					}else if(command.equals("List")){
						System.out.println("The server is sending its files to a client whose part is: "+connectionFromClient.getPort());
						File folder = new File("ServerShare");
                        File[] listOfFiles = folder.listFiles();
                        String[] fileNames = new String[listOfFiles.length];
						for (int i = 0; i < listOfFiles.length; i++) {
  							if (listOfFiles[i].isFile()) {
    							fileNames[i] = listOfFiles[i].getName();
  						}
						}
						for (String singlefilename : fileNames) {
							// writeUTF() method is used to write a string in an outputstream
							
							dataOut.writeUTF(singlefilename);
						  }
						  // here we send an empty string to the client so that the client program can close a loop that keeps printing filenames
						dataOut.writeUTF("");

					}

					else {

						System.out.println("Connection got from an incompatible client");

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
