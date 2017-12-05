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
                Cliente cliente = new Cliente("localhost", 12345);
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
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}