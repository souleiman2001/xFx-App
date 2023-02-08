# xFx-App# 
The client opens a connection with the server and *informs* the server whether it wants to *download* or *upload* or *get the list of files* a file using a *header*.

## Download
If the client wants to download a file, then the header will be as the following:
- **download[one space][file name][Line Feed]**

Upon receiving this header, the server searches for the specified file.
- If the file is not found, then the server shall reply with a header as the following:
  - **NOT[one space]FOUND[Line Feed]**
- If the file is found, then the server shall reply
  - with a header as the following:
    - **OK[one space][file size][Line Feed]**
  - followed by the bytes of the file
		
## Upload
If the client wants to upload a file, then the header will be as the following:
- **upload[one space][file name][one space][file size][Line Feed]**

After sending the header, the client shall send the bytes of the file


// if the client wants to upload a file but ended up entering a file that does not exist in its folder:
then no header should be sent.


## Get list of files
The client sends a list file request with a following header.
**List[one space][String][Line Feed]**
Upon receiving this header, the server should respond with
the list of available files in the servershare (server folder)
separated by a line feed.
- file1[line Feed] file2[line Feed]...

