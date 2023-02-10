import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

public class FxClient {
	public static void main(String[] args) throws Exception {
		String command = args[0];
		String fileName = args[1];
		

		try (Socket connectionToServer = new Socket("localhost", 80)) {

			// I/O operations

			InputStream in = connectionToServer.getInputStream();
			OutputStream out = connectionToServer.getOutputStream();

			BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
			BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));
			DataInputStream dataIn = new DataInputStream(in);
			
			if (command.equals("d")) {
				String header = "download " + fileName + "\n";
				headerWriter.write(header, 0, header.length());
				headerWriter.flush();

				header = headerReader.readLine();

				if (header.equals("NOT FOUND")) 
				{
					System.out.println("We're extremely sorry, the file you specified is not available!");
				}
				else
				 {
				    StringTokenizer strk = new StringTokenizer(header, " ");
					String status = strk.nextToken();

					if (status.equals("OK")) {

						String temp = strk.nextToken();

						int size = Integer.parseInt(temp);

					try{
			           FileInputStream fileIn = new FileInputStream("ClientShare/" + fileName);
			           int offset = fileIn.available();		
					   fileIn.close();		
					   if (offset == size){
					   // If the file sizes match, close the connection and show a message
      		           System.out.println("File already downloaded");
					   }
			          else if(offset < size)
			         {
						// now we should receive the remaining bytes
					    // Create an output stream to write the bytes to the file
    					
						RandomAccessFile output = new RandomAccessFile(fileName, "rw");
    
   						// Seek to the offset in the file
   	 					output.seek(offset);
    
   					  // Read the bytes from the server and write them to the file
    				
    				  byte[] buffer = new byte[4096];
    				  int count;
    				  while ((count = dataIn.read(buffer)) != -1)
					 {
      				 output.write(buffer, 0, count);
    				 }
   				 	 // Close the output stream
    				 output.close();
   				 	 // Show a message indicating that the download is complete
    				 System.out.println("Download complete");
 			  }
  
 			 		// Close the socket connection
  					//connectionToServer.close();
							   
		        } catch (Exception ex) {
					 // file does not exist in clientshare so we download it
					 byte[] space = new byte[size];
					 dataIn.readFully(space);
                     try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
							fileOut.write(space, 0, size);
						}
						System.out.println("the file was downloaded successfully");
					}
    		           // Check if the current file size is equal to the file size on the server
    		        
					
				}
				 else {
						System.out.println("You're not connected to the right Server!");
					}

				}

		


			} else if (command.equals("u")) {
				
				try {
					FileInputStream fileIn = new FileInputStream("ClientShare/" + fileName);
					int fileSize = fileIn.available();
					String header = "upload " + fileName + " "+ fileSize +"\n";
				    headerWriter.write(header, 0, header.length());
				    headerWriter.flush();
					
					byte[] bytes = new byte[fileSize];
					fileIn.read(bytes);

					fileIn.close();
					DataOutputStream dataOut = new DataOutputStream(out);
					dataOut.write(bytes, 0, fileSize);
					

				} catch (Exception ex) {
					System.out.println("Sorry the file that you want to upload is not available !");
					
				} finally {
					connectionToServer.close();
				}

			} else if(command.equals("l")){
				System.out.println("the client sent a list files request to the server");
				String header ="List "+ fileName + "\n";
				headerWriter.write(header,0,header.length());
				headerWriter.flush();
				while (true) {
					/*  fn is the filename that is read through the data inputstream each time 
					as long as the whole list of file names is
				    */
					String fn = dataIn.readUTF();
					//the statement break is used to exit the loop and it happens when we reach the end of the file names
					if (fn == null || fn.equals(""))
					{  /* as soon as there is no more filenames, the program receives an empty string and leaves the loop
						hence it can close the connection with the server
						*/
						break;
					}
                	System.out.println("Received file name: " + fn);
				 }
				 connectionToServer.close();


			} else {
				System.out.println("Sorry you entered a wrong command, you should either enter d( for download) with the filename or u(for upload) with the filename, or l(for list) with keyword files ");
			}
		}
	}
}
