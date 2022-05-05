package utilidades;

import java.net.ServerSocket;

public class Servidor{
    
    public ServerSocket servidor = null;
    public Cliente cliente = null;
    
    public Servidor( int puerto ){
        try{
            servidor = new ServerSocket( puerto );
        }catch(IllegalArgumentException ex) {
            System.err.println("Error: El Puerto para crear el servidor está fuera del rango de 0 hasta 65535.");
            System.exit(1);
        }catch(Exception ex){
            System.err.println("Error: No se puede iniciar el servidor porque el puerto está ocupado.");
            System.exit(1);
        }
        aceptar();
    }
    
    public void aceptar(){
        cerrar();
        System.out.println("Esperando la conexión de un cliente.");
        try{
            cliente = new Cliente ( servidor.accept() );
            System.out.println("Cliente conectado.");
        }catch(Exception ex){
            System.err.println("Error: No se puede establecer conexión con el cliente.");
            aceptar();
        }
    }
    
    public void cerrar(){
        try{
            cliente.cerrar();
        } catch( Exception ex ){}
    }
    
}
