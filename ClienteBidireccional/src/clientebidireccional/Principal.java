package clientebidireccional;

import java.util.Scanner;
import utilidades.Cliente;
import utilidades.Validacion;

public class Principal {

    public static void main(String[] args) {

        Validacion.validar( new String[]{
            "string","Dirección IPv4 para realizar la conexión con el servidor.",
            "int","Numero de puerto para realizar conexión con el servidor, en el rango de 0 hasta 65535."
        }, args );
        
        Cliente cliente = new Cliente( args[0], Integer.valueOf( args[1] ) );
        Scanner teclado = new Scanner( System.in );
        
        while(true){
            try{
                System.out.print("Escriba un mensaje para el servidor: ");
                String salida = teclado.nextLine();
                cliente.escribir( salida );
                switch ( salida.toLowerCase() ) {
                    case "fin":
                        cliente.cerrar();
                        System.exit(0);
                        break;
                    default:
                        String entrada = cliente.leerString();
                        System.out.println( "El servidor dice: " + entrada );
                        switch ( entrada.toLowerCase() ) {
                            case "fin":
                                cliente.cerrar();
                                System.exit(0);
                                break;
                        }
                        break;
                }
            }catch(Exception ex){
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }
    
}
