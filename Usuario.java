import java.io.IOException;
import java.util.Scanner;

public class Usuario {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            (new Thread(new Servidor())).start();
            System.out.println("DIGITE s PARA RECEBER ARQUIVOS:");
            String entrada = in.next();
            if(entrada.equals("s")) {
                Cliente cliente = new Cliente("172.20.4.210", 3000);
                Thread client;
                client = new Thread(cliente);
                client.start();
                System.out.println("DIGITE p PARA PAUSAR A TRANSFERENCIA");
                if(in.next().equals("p")){
                    cliente.setPausado(true);
                    System.out.println("DIGITE r PARA RETORNAR A TRANSFERENCIA");
                    if(in.next().equals("r")){
                        cliente.setPausado(false);
                    }
                }if(in.next().equals("c")) {
                	cliente.setCancelado(true);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}