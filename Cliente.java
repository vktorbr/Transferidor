import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Cliente implements Runnable{
    Socket sockServer;
    boolean pausado;
    boolean cancelado;
    public Cliente(String ip, int port) throws IOException {
        //Cria conexao com o servidor
        System.out.println("Conectado com o servidor pela porta: "+port);
        sockServer = new Socket(ip, port);
    }
    public synchronized void verificaPausa() throws InterruptedException {
        while(pausado){
            System.out.println("A TRANSFERENCIA FOI PAUSADA!");
            wait();

        }
    }
    
    public void setCancelado(boolean cancelado) {
    	this.cancelado=cancelado;
    	System.out.println("TRANSFERENCIA CANCELADA");
    }
    public synchronized void setPausado(boolean pausado){
        this.pausado=pausado;
        if(!this.pausado){
            notifyAll();
            System.out.println("A TRANSFERENCIA FOI RETORNADA!");
        }
    }
    public String receberMsg(Socket sockServer) throws IOException {
        while(true) {
            DataInputStream entrada = new DataInputStream(sockServer.getInputStream());
            String extensao = entrada.readUTF();
            return extensao;
        }
    }
    
    public void receberArquivo(FileOutputStream fos, InputStream is, File file) throws IOException, InterruptedException {
    	 // Prepara variaveis para transferencia
        byte[] cbuffer = new byte[1024];
        int bytesRead;
        double tamanho = file.length();
        double current=0;
        double porcentagem=0;
        // Criando canal de transferencia
        is = sockServer.getInputStream();
        
        // Copia conteudo do canal
        System.out.println("Recebendo arquivo...");
        while (((bytesRead = is.read(cbuffer)) != -1) && cancelado==false) {
            verificaPausa();                
            fos.write(cbuffer, 0, bytesRead);
            fos.flush();
            current+=bytesRead;
            porcentagem=(current/tamanho)*100; 
            
            System.out.printf("%.2f", porcentagem);
            System.out.print("%");
            System.out.println();
        }   
        fos.close();
        is.close();
        
        if(cancelado==true) {
        	file.delete();
        	
        	System.out.println("Arquivo apagado");
        }else {
        	 System.out.println("Arquivo recebido!");
        }
    }
    
    public void run() {
        FileOutputStream fos = null;
        InputStream is = null;
        File file = null;
        try {
            
            
            String nome = receberMsg(sockServer);
            
            JFileChooser salvandoArquivo = new JFileChooser();
            salvandoArquivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            int resultado = salvandoArquivo.showSaveDialog(null);

            if(resultado==JFileChooser.APPROVE_OPTION){

                // Cria arquivo local no cliente
            	file = new File(salvandoArquivo.getSelectedFile().getPath()+"\\"+nome);
                fos=new FileOutputStream(file);
                
                System.out.println("Arquivo Local Criado");
            }

           receberArquivo(fos, is, file);
            
           
           
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sockServer != null) {
                try {
                    sockServer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}