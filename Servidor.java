import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Servidor implements Runnable  {
    ServerSocket servsock;
    Usuario usuario;
    String ipcliente;
    public Servidor(int porta, Usuario usuario)throws IOException{
        // Abrindo porta para conexao de clients
        servsock = new ServerSocket(porta);
        System.out.println("Porta "+porta+" aberta!");
        RTTServidor rttServer = new RTTServidor(1);
		Thread rttServerT = new Thread(rttServer);
		rttServerT.start();
		
		
        this.usuario=usuario;           
    }
    public void mandarMsg(Socket sock, String extensao) throws IOException {
        DataOutputStream saida = new DataOutputStream(sock.getOutputStream());
        saida.writeUTF(extensao);
    }

    public void mandarTam(Socket sock, long length) throws IOException {
        DataOutputStream saida = new DataOutputStream(sock.getOutputStream());
        saida.writeLong(length);
    }
    public String receberMsg(Socket sockServer) throws IOException {
           while(true) {
                DataInputStream entrada = new DataInputStream(sockServer.getInputStream());
                String extensao = entrada.readUTF();
                return extensao;
            }
        }   
        
    
    public void enviarArquivo(Socket sock, OutputStream socketOut, FileInputStream fileIn, double tamanho) throws IOException {
        // Criando tamanho de leitura
        byte[] cbuffer = new byte[1024];
        int bytesRead;
        double current = 0;
        double porcentagem=0;
        long velocidade=0;
        long tempoRestante=0;
        // Criando canal de transferencia
        socketOut = sock.getOutputStream();
        
        // Lendo arquivo criado e enviado para o canal de transferencia
        System.out.println("Enviando Arquivo...");
        
        while ((bytesRead = fileIn.read(cbuffer)) != -1) {
            long tempoInicio = System.nanoTime();
            socketOut.write(cbuffer, 0, bytesRead);
            socketOut.flush();

            long tempoFinal = ((System.nanoTime()-tempoInicio));
            
            if(tempoFinal!=0){
            	velocidade = bytesRead / tempoFinal;
            }
            
            current+=bytesRead;
            
            long tamanhoRestante = (long) (tamanho-current);
            if(velocidade!=0){
            	tempoRestante = tamanhoRestante/velocidade;
            }
            porcentagem = (current/tamanho)*100;
            
            usuario.setarTempo(tempoRestante/1000000, usuario.tempoServidor);
            usuario.setarPorcento(porcentagem, usuario.Porcentagem);
        }fileIn.close();
        socketOut.close();
        sock.close();
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

                mandarTam(sock, file.length());
                //ipcliente = receberMsg(sockServer);
                fileIn = new FileInputStream(file);

                System.out.println("Lendo arquivo...");
            }  ipcliente = receberMsg(sock);
            usuario.setarIp(ipcliente);
            
            RTTCliente rttCliente2 = new RTTCliente(usuario.ipCliente.getText(), usuario, 1);
            Thread rttClientT = new Thread(rttCliente2);
            rttClientT.start();
           
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