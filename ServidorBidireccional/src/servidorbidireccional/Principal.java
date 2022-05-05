package servidorbidireccional;

import java.util.Scanner;
import utilidades.Servidor;
import utilidades.Validacion;

public class Principal {

    public static void main(String[] args) {

        Validacion.validar( new String[]{
            "int","Puerto para crear el servidor, en el rango de 0 hasta 65535."
        }, args );
        
        Servidor servidor = new Servidor( Integer.valueOf( args[0] ) );
        Scanner teclado = new Scanner( System.in );
        
        while(true){
            try{
                String entrada = servidor.cliente.leerString();
                System.out.println( "El cliente dice: " + entrada );
                switch ( entrada.toLowerCase() ) {
                    case "fin":
                        servidor.aceptar();
                        break;
                    default:
                        System.out.print("Escriba un mensaje para el cliente: ");
                        String salida = teclado.nextLine();
                        servidor.cliente.escribir( salida );
                        switch ( salida.toLowerCase() ) {
                            case "fin":
                                servidor.aceptar();
                                break;
                        }
                        break;
                }
            }catch(Exception ex){
                System.err.println(ex.getMessage());
                servidor.aceptar();
            }
        }
        
    }
    
}
