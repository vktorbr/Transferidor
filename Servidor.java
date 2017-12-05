import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Servidor implements Runnable {
    ServerSocket servsock;

    public Servidor()throws IOException{
        // Abrindo porta para conexao de clients
        servsock = new ServerSocket(12345);
        System.out.println("Porta de conexao aberta 12345");
    }
    public void mandarMsg(Socket sock, String extensao) throws IOException {
        DataOutputStream saida = new DataOutputStream(sock.getOutputStream());
            saida.writeUTF(extensao);
    }
    public void enviarArquivo(Socket sock, OutputStream socketOut, FileInputStream fileIn, double tamanho) throws IOException {
    	 // Criando tamanho de leitura
        byte[] cbuffer = new byte[1024];
        int bytesRead;
        double current = 0;
        double porcentagem=0;
    	 // Criando canal de transferencia
        socketOut = sock.getOutputStream();
        
        // Lendo arquivo criado e enviado para o canal de transferencia
        System.out.println("Enviando Arquivo...");

        while ((bytesRead = fileIn.read(cbuffer)) != -1) {
            socketOut.write(cbuffer, 0, bytesRead);
            socketOut.flush();
            current+=bytesRead;
            porcentagem=(current/tamanho)*100; 
            
            System.out.printf("%.2f", porcentagem);
            System.out.print("%");
            System.out.println();
        }
        System.out.println("Arquivo Enviado!");
    }
    public void run() {
        // Checa se a transferencia foi completada com sucesso
        OutputStream socketOut = null;
        FileInputStream fileIn = null;
        File file=null;
        try {
            // Cliente conectado
            Socket sock = servsock.accept();
            System.out.println("Conexao recebida pelo cliente");

            //Escolhendo arquivo a ser enviado
            JFileChooser fileChooser = new JFileChooser();
            int opt = fileChooser.showOpenDialog(null);
            if(opt==JFileChooser.APPROVE_OPTION){

                // Criando arquivo que sera transferido pelo servidor
                file = fileChooser.getSelectedFile();           
                
                mandarMsg(sock, file.getName());
                
                fileIn = new FileInputStream(file);
                
                System.out.println("Lendo arquivo...");
            }
            enviarArquivo(sock, socketOut, fileIn, file.length());
           
        }catch(SocketException a) {
        	System.out.println("Cliente cancelou a transferencia");
        	
        	//socketOut.close();
        	run(); 
        	
        }
        catch (Exception e) {
            // Mostra erro no console
            e.printStackTrace();
        } finally {
            if (socketOut != null) {
                try {
                    socketOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (servsock != null) {
                try {
                    servsock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileIn != null) {
                try {
                    fileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}