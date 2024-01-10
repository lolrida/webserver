package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(3000);
        while(true){
            System.out.println("Server in avvio!");
            


            Socket s = server.accept(); // accetta connessione
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            
            String messaggio = input.readLine();
            String [] messaggStrings = messaggio.split(" ");

            String path = messaggStrings[1].substring(1);
            File file = new File("htdoc/" + path);

            while(!messaggio.isEmpty()){
                System.out.println("Messaggio ricevuto: " + messaggio);
                messaggio = input.readLine();
            }

            if (path.equals("test")) {
                output.writeBytes("HTTP/1.1 301 Moved Permanently\n");
                output.writeBytes("Location: https://www.google.com\n");
                output.writeBytes("\n");
            }
            if(path.equals("")){ // se non viene specificato un file, allora restituiamo il file index.html
                file = new File("htdoc/index.html");
            }

            if(file.exists()){
                sendBinariFile(s, file);
            }else {
                    String msg = "Il file non esiste";
                    output.writeBytes("HTTP/1.1 404 Not Found\n"); // specifichiamo il protocollo e la versione
                    output.writeBytes("Content-Length: " + msg.length() + "\n"); // specifichiamo la lunghezza del contenuto
                    output.writeBytes("Content-Type: text/plain\n"); // specifichiamo al browser che il contenuto è di tipo testo,  se fosse stato un'immagine avremmo dovuto mettere image/png o image/jpeg, nel caso fosse stato un html text/html>
                    output.writeBytes("\n");
                    output.writeBytes(msg);
            }
               
            s.close();
            }
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
    }

    private static void sendBinariFile(Socket socket, File file) throws IOException{
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeBytes("HTTPS/1.1 200 OK\n");
        output.writeBytes("Content-Length: " + file.length() + "\n");

        //scelgo il tipo di file
        output.writeBytes("Content-Type:" + getContentType(file) + "\n");

        output.writeBytes("\n");
        InputStream input = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int n;
        while((n = input.read(buffer)) > 0){
            output.write(buffer, 0, n);
        }
        input.close();
    }

    private static String getContentType(File file) {
        String fileName = file.getName();
        String[] split = fileName.split("\\.");
        String extension = split[split.length - 1];
        switch (extension) {
            case "html":
                return "text/html";
            case "png":
                return "image/png";
            case "jpg":
                return "image/jpeg";
            case "css":
                return "text/css";
            case "js":
                return "text/javascript";
            default:
                return "text/plain";
        }
    }

    
}