import java.net.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Arrays;

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
					   //fileIn.close();		
				if (offset == size)
				{  // If the file sizes match,  we need to check if the content of the file has changed or not
					   // if it has changed we should download it otherwise not !  
					   byte[] server_data = new byte[size];
					   byte[] client_data = new byte[offset];
					   fileIn.read(client_data);
					   dataIn.readFully(server_data);
					   if (Arrays.equals(client_data, server_data)) {
						// File on the client and server are the same, no need to download again
						System.out.println("File already downloaded");
					} 
				      else{
					// go overwrite it now !
					try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
							fileOut.write(server_data, 0, size);
						}
					}
					  
					   
      		          // System.out.println("File already downloaded");
				}
                else if(offset != size)
			        {   byte[] server_data = new byte[size];
						dataIn.readFully(server_data);
						try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
							fileOut.write(server_data, 0, size);
						}
						/* 
						// now we should receive the remaining bytes
					    // Create an output stream to write the bytes to the file
    			        RandomAccessFile output = new RandomAccessFile("ClientShare/" + fileName, "rw");
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
					   */

                         
                    }
  


 			 		// Close the socket connection
  					//connectionToServer.close();
					  fileIn.close();			   
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
				header = headerReader.readLine();
				StringTokenizer strk = new StringTokenizer(header, " ");
				String status = strk.nextToken();
				if (status.equals("ok")){
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
				}
                else{
					System.out.println("sorry the servershare folder is empty");
				}
			

				
				 connectionToServer.close();


			} else {
				System.out.println("Sorry you entered a wrong command, you should either enter d( for download) with the filename or u(for upload) with the filename, or l(for list) with keyword files ");
			}
		}
	}
}
