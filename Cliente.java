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
        while(this.pausado && this.cancelado==false){
            System.out.println("A TRANSFERENCIA FOI PAUSADA!");
            wait();
        }
    }

    public synchronized void setCancelado(boolean cancelado) throws InterruptedException {
        this.cancelado=cancelado;
        if(pausado)
        {
        notifyAll();	
        }
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

    public long receberTam(Socket sockServer) throws IOException {
        while(true) {
            DataInputStream entrada = new DataInputStream(sockServer.getInputStream());
            long tamanho = entrada.readLong();

            return tamanho;
        }
    }

    public void receberArquivo(Socket sockServer, FileOutputStream fos, InputStream is, File file, double tamanho) throws IOException, InterruptedException {
        // Prepara variaveis para transferencia
        byte[] cbuffer = new byte[1024];
        int bytesRead;
        double current=0;
        double porcentagem=0;
        double velocidade=0;
        // Criando canal de transferencia
        is = sockServer.getInputStream();

        // Copia conteudo do canal
        System.out.println("Recebendo arquivo...");
        while (((bytesRead = is.read(cbuffer)) != -1) && cancelado==false) {
            verificaPausa();
            
            if(!cancelado){
            double tempoInicio = System.nanoTime();	
            fos.write(cbuffer, 0, bytesRead);
            fos.flush();
            double tempoFinal =( (System.nanoTime()-tempoInicio)/1000000000);
            System.out.println(System.nanoTime());
            velocidade = (bytesRead/1024)/tempoFinal;
            current+=bytesRead;
            double tamanhoRestante = (tamanho-current)/1024;
            double tempoRestante = tamanhoRestante/velocidade;
            porcentagem=(current/tamanho)*100;
          //  System.out.println("TEMPO RESTANTE: "+tempoRestante + "TAMANHO RESTANTE: "+tamanhoRestante);
           // System.out.printf("%.2f", porcentagem);
            //System.out.print("%");
           // System.out.println();
            }
        }System.out.println("Conexao fechada!");
        fos.close();
        is.close();
        if(cancelado==true) {
        	fos.close();
        	is.close();
            file.delete();
            System.out.println("Arquivo apagado");
        }
        sockServer.close();
        
    }

    public void run() {
        FileOutputStream fos = null;
        InputStream is = null;
        File file = null;
        try {


            String nome = receberMsg(sockServer);
            long tamanho = receberTam(sockServer);
            JFileChooser salvandoArquivo = new JFileChooser();
            salvandoArquivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int resultado = salvandoArquivo.showSaveDialog(null);

            if(resultado==JFileChooser.APPROVE_OPTION){

                // Cria arquivo local no cliente
                file = new File(salvandoArquivo.getSelectedFile().getPath()+"\\"+nome);
                fos=new FileOutputStream(file);

                System.out.println("Arquivo Local Criado");
            }

            receberArquivo(sockServer, fos, is, file, tamanho);
            


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