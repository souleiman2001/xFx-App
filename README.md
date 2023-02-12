xFx Protocol

The client opens a connection with the server and *informs* the server whether it wants to *download* or *upload* or *get the list of files in the server folder* a file using a *header*.

## Download
If the client wants to download a file, then the header will be as the following:
download[one space][file name][Line Feed]**

Upon receiving this header, the server searches for the specified file.
If the file is not found, then the server shall reply with a header as the following:
  NOT[one space]FOUND[Line Feed]**
 If the file is found, then the server shall reply with a header as the following:
    -OK[one space][file size][Line Feed]**
    - followed by the bytes of the file
 If the file is found on server side but already exist on client side we check the followings:
     -If the size of the file is the same: we check if the content stayed the same.
          -If it stayed the same, the client sends the following header to the server: 
	  already[one space]downloaded[line feed]
          -If the content has changed, the client sends the following header to the server:
	Download[one space]updated[line feed]   
      -If the size of the file has changed, the client sends the following header to the server:
	Download[one space]updated[line feed]   
If the file is found on server side and does not exist on client side:
No header is sent after that the bytes of the files were sent by the server and then the file got stored in the client folder.


		
## Upload
If the client wants to upload a file, then the header will be as the following:
-upload[one space][file name][one space][file size][Line Feed]**

After sending the header, the client shall send the bytes of the file

If the client wants to upload a file but ended up entering a file that does not exist in its folder:
then no header should be sent.

## Get list of files
The client sends a list file request with a following header.
List[one space][String][Line Feed]**
Upon receiving this header, the server checks whether its folder contain some files or not.
-If the ServerShare does not contain any file which is a very rare case, the server shall reply with the following header:
empty[one space][Line Feed]
-If the ServerShare contains at least one file, the server shall reply with the following header.
ok[one space][line feed]
followed by the list of available files in the ServerShare (server folder)
separated by a line feed.   file1[line Feed] file2[line Feed]...
