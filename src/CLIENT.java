import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class CLIENT {
    private static Socket socket;

    static final int PORT = 8080;
    static final String IP = "127.0.0.1";
    static void CLIENTGET(String page)
    {
        BufferedInputStream dataIn = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(IP, PORT);
            System.out.println("Connected....");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            out.println("GET /"+page+" HTTP/1.0");
            out.println();
            out.flush();

            String input = in.readLine();
            System.out.println(input);

            input = in.readLine();
            System.out.println(input);

            input = in.readLine();
            System.out.println(input);

            StringTokenizer parse = new StringTokenizer(input);
           String length = parse.nextToken();
           length = parse.nextToken();

            int len = Integer.parseInt(length);


            dataIn = new BufferedInputStream(socket.getInputStream());
            File recieved = new File("C:\\Users\\Shaheer\\Desktop\\Engineering\\Computer Networks\\project 1\\New folder\\Client\\"+page);
            FileOutputStream stream = new FileOutputStream(recieved);
            DataOutputStream outputFile = new DataOutputStream(stream);

           for (int i=0;i<=len+1;i++) {
               outputFile.write(dataIn.read());
           }

            outputFile.close();


            socket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     static void CLIENTPUT(String s) {
         BufferedOutputStream dataOut = null;
         BufferedReader in = null;
         PrintWriter out = null;

         FileInputStream fileIn = null;


         try {
             socket = new Socket(IP, PORT);
             System.out.println("Connected....");
             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

             out = new PrintWriter(socket.getOutputStream());

             dataOut = new BufferedOutputStream(socket.getOutputStream());

             File file = new File("C:\\Users\\Shaheer\\Desktop\\Engineering\\Computer Networks\\project 1\\New folder\\Client\\" + s);
             int fileLength = (int) file.length();
             String content = "";
             if (s.endsWith(".htm") || s.endsWith(".html"))
                 content = "html";
             else
                 content = "jpg";


             byte[] fileData = new byte[fileLength];
             try {
                 fileIn = new FileInputStream(file);
                 fileIn.read(fileData);
             } finally {
                 if (fileIn != null)
                     fileIn.close();
             }


             // send HTTP Headers
             out.println("PUT /" + s + " HTTP/1.0");
             out.println("Content-type: " + content);
             out.println("Content-length: " + fileLength);
             out.println(); // blank line between headers and content, very important !
             out.flush(); // flush character output stream buffer
             dataOut.write(fileData, 0, fileLength);
             dataOut.flush();




         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             try {
                 in.close();
                 out.close();
                 dataOut.close();
                 socket.close(); // we close socket connection

             } catch (Exception e) {
                 System.err.println("Error closing stream : " + e.getMessage());
             }


         }
         System.out.println("File has been put Successfully");

     }


    public static void main(String[] arg)
    {
       CLIENTGET("2.jpg");
        //CLIENTPUT("2.jpg");
    }


}