package clientebasico;

import utilidades.Cliente;
import utilidades.Validacion;

public class Principal {

    public static void main(String[] args) {

        Validacion.validar( new String[]{
            "string","Dirección IPv4 para realizar la conexión con el servidor.",
            "int","Numero de puerto para realizar conexión con el servidor, en el rango de 0 hasta 65535.",
            "string","Mensaje para enviar al servidor."
        }, args );
        
        Cliente cliente = new Cliente( args[0], Integer.valueOf( args[1] ) );

        try{
            cliente.escribir( args[2] );
            System.out.println( "Mensaje enviado al servidor." );
        }catch(Exception ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        
    }
    
}
