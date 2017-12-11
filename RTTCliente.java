import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RTTCliente implements Runnable{
	DatagramSocket clientSocket;
	byte[] sendData;
	InetAddress IPServer;
	Usuario usuario;
	int opcao;
	RTTCliente(String ip, Usuario usuario, int opcao) throws IOException{
		clientSocket = new DatagramSocket();	//criando o socket UDP
		IPServer= InetAddress.getByName(ip);	//definindo o IP do servidor que vai se comunicar
		this.usuario=usuario;
		this.opcao=opcao;
	}	
		
	public void run() {
		sendData = ("1").getBytes();	//definindo dados que vão ser enviados
		//definindo os dados que vão ser enviados para o cliente e definindo para qual cliente vai enviar
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPServer, 5000);
		
		try {
			long as = System.currentTimeMillis();	//pegando o tempo atual para calcular o RTT
			//enviando o pacote
			clientSocket.send(sendPacket);
			byte[] receiveData = new byte[1];
			//definindo onde os dados recebidos do cliente vão ser armazenados
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);	//recebendo o pacote
			long rtt = System.currentTimeMillis()-as;
			if(opcao==1){
				usuario.setarRTT(rtt, usuario.RTTServidor);
			}else if(opcao==2){
			usuario.setarRTT(rtt, usuario.RTTCliente);
			}
			clientSocket.close();	//fechando o socket
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
	
	}
}
