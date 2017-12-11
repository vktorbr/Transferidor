import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Cliente implements Runnable{
    Socket sockServer;
    boolean pausado;
    boolean cancelado;
    Usuario usuario;
    public Cliente(String ip, int port, Usuario usuario) throws IOException {
        //Cria conexao com o servidor
        System.out.println("Conectado com o servidor pela porta: "+port);
        sockServer = new Socket(ip, port);
        this.usuario=usuario;
        
        RTTCliente rttCliente = new RTTCliente(ip, usuario, 2);
        Thread rttClienteT = new Thread(rttCliente);
        rttClienteT.start();
        
        RTTServidor rttServer = new RTTServidor(2);
 		Thread rttServerT = new Thread(rttServer);
 		rttServerT.start();
        
        
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
    public void mandarMsg(Socket sock, String extensao) throws IOException {
        DataOutputStream saida = new DataOutputStream(sock.getOutputStream());
        saida.writeUTF(extensao);
    }

    public void receberArquivo(Socket sockServer, FileOutputStream fos, InputStream is, File file, double tamanho) throws IOException, InterruptedException {
        // Prepara variaveis para transferencia
        byte[] cbuffer = new byte[1024];
        int bytesRead;
        double current=0;
        double porcentagem=0;
        long velocidade=0;
        int cont=100;
        long tempoRestante=0;
        // Criando canal de transferencia
        is = sockServer.getInputStream();

        // Copia conteudo do canal
        System.out.println("Recebendo arquivo...");
        while (((bytesRead = is.read(cbuffer)) != -1) && cancelado==false) {
            verificaPausa();
            
            if(!cancelado){
            	
            long tempoInicio = System.nanoTime();	
            fos.write(cbuffer, 0, bytesRead);
            fos.flush();
            long tempoFinal =(System.nanoTime()-tempoInicio);
            if(tempoFinal!=0){
            	velocidade = (bytesRead/1024)/tempoFinal;
            }
            current+=bytesRead;
            
            long tamanhoRestante = (long) ((tamanho-current)/1024);
            if(velocidade!=0){
            	tempoRestante = (tamanhoRestante/velocidade);
            }
           // if(cont==0) {
            //	cont=100;
            usuario.setarTempo(tempoRestante/1000000, usuario.tempoCliente);
            //}cont--;
            porcentagem=(current/tamanho)*100;
            usuario.setarPorcento(porcentagem, usuario.PorcentagemCliente);
           
        } }if(cancelado==false) {
        	System.out.println("Arquivo Recebido");
        }
            System.out.println("Conexao fechada!");
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