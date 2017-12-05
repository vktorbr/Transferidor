import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Cliente implements Runnable{
    Socket sockServer;
    boolean pausado;
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
    public void run() {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            is = sockServer.getInputStream();

            String a=receberMsg(sockServer);

            JFileChooser salvandoArquivo = new JFileChooser();
            salvandoArquivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int resultado = salvandoArquivo.showSaveDialog(null);

            if(resultado==JFileChooser.APPROVE_OPTION){

                // Cria arquivo local no cliente
                fos=new FileOutputStream(new File(salvandoArquivo.getSelectedFile().getPath()+"\\filme."+a));
                System.out.println("Arquivo Local Criado");
            }

            // Prepara variaveis para transferencia
            byte[] cbuffer = new byte[1024];
            int bytesRead;

            // Copia conteudo do canal
            System.out.println("Recebendo arquivo...");
            while ((bytesRead = is.read(cbuffer)) != -1) {
                verificaPausa();
                fos.write(cbuffer, 0, bytesRead);
                fos.flush();
            }System.out.println("Arquivo recebido!");
            fos.close();
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
