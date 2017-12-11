import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RTTServidor implements Runnable{
	DatagramSocket serverSocket;
	byte[] receiveData;
	byte[] sendData;
	InetAddress clientIP;
	int port;

	RTTServidor() throws SocketException{
		
			serverSocket = new DatagramSocket(5001);
		
			//abrir socket na porta 5000
		receiveData = new byte[1];
		
	}
			
	public void run() {
		//definindo onde os dados recebidos do cliente vão ser armazenados
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			serverSocket.receive(receivePacket);
			clientIP = receivePacket.getAddress();	//pegando o IP do pacote que chegou
			port = receivePacket.getPort();	//pegando a porta do pacote que chegou
			sendData = ("2").getBytes();	//definindo dados que vão ser enviados
			
			//definindo os dados que vão ser enviados para o cliente e definindo para qual cliente vai enviar
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);//
			serverSocket.send(sendPacket);	//enviando os dados para o cliente
		} catch (IOException e) {
			
			e.printStackTrace();
		}	
		
	}
}