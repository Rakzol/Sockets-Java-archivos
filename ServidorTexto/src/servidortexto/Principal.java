package servidortexto;

import utilidades.Servidor;
import utilidades.Validacion;

public class Principal {

    public static void main(String[] args) {

        Validacion.validar( new String[]{
            "int","Puerto para crear el servidor, en el rango de 0 hasta 65535."
        }, args );
        
        Servidor servidor = new Servidor( Integer.valueOf( args[0] ) );
        
        while(true){
            try{
                servidor.cliente.subir( true );
                servidor.aceptar();
            }catch( Exception ex ){
                System.err.println( ex.getMessage() );
                servidor.aceptar();
            }
        }

    }
    
}
