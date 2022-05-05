package utilidades;

import java.net.Socket;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Cliente{

    public Socket cliente = null;
    
    public Cliente( String ip, int puerto ){
        try {
            cliente = new Socket( ip, puerto );
        } catch ( UnknownHostException ex ) {
            System.err.println("Error: La IPv4 para realizar la conexión no es válida.");
            System.exit(1);
        } catch ( IllegalArgumentException ex ) {
            System.err.println("Error: El Puerto para realizar la conexión está fuera del rango de 0 hasta 65535.");
            System.exit(1);
        } catch ( ConnectException ex ){
            System.err.println("Error: Tiempo para realizar la conexión agotado o conexión rechazada.");
            System.exit(1);
        } catch ( SocketException | SecurityException ex ){
            System.err.println("Error: No tiene permisos para realizar la conexión.");
            System.exit(1);
        } catch ( Exception ex ){
            System.err.println("Error: No se puede realizar la conexión.");
            System.exit(1);
        }
    }
   
    public Cliente( Socket cliente ) {
        this.cliente = cliente;
    }
    
    public String leerString() throws Exception {
        return new String( leerBytes() );
    }
    
    public int leerInt() throws Exception {
        return ByteBuffer.wrap( leerBytes() ).getInt();
    }
    
    public float leerFloat() throws Exception {
        return ByteBuffer.wrap( leerBytes() ).getFloat();
    }
    
    public long leerLong() throws Exception {
        return ByteBuffer.wrap( leerBytes() ).getLong();
    }
    
    public double leerDouble() throws Exception {
        return ByteBuffer.wrap( leerBytes() ).getDouble();
    }
    
    public void descargar( String rutaServidor, String rutaCliente, boolean mostrar ) throws Exception {

        String nombre = new File(rutaServidor).getName();
        File directorio = new File( rutaCliente );
        RandomAccessFile archivo = null;
        byte[] buffer;
        long tam;
        long puntero = 0l;
        
        try{
            escribir( rutaServidor );
            
            if( leerInt() == 1 ){
                throw new Exception( leerString() );
            }
            tam = leerLong();

            if( !directorio.exists() ){
                if( !directorio.mkdirs() ){
                    escribir(1);
                    escribir("Error: No se tienen permisos para subir el archivo en la ruta especificada.");
                    throw new Exception( "Error: No se tienen permisos para descargar el archivo en la ruta especificada." );
                }
            }

            if( directorio.getUsableSpace() < tam ){
                escribir(1);
                escribir("Error: No hay suficiente espacio en el directorio donde se quiere subir el archivo.");
                throw new Exception( "Error: No hay suficiente espacio en el directorio donde se quiere descargar el archivo." );
            }

            if( new File( rutaCliente + "/" + nombre ).exists() ){
                int prefijo = 0;
                while( new File( rutaCliente + "/" + prefijo + " " + nombre ).exists() ){
                    prefijo++;
                }
                nombre = prefijo + " " + nombre;
            }

            try{
                archivo = new RandomAccessFile( rutaCliente + "/" + nombre, "rw" );
            }catch( Exception ex ){
                escribir(1);
                escribir("Error: No se tienen permisos para subir el archivo en la ruta especificada.");
                throw new Exception( "Error: No se tienen permisos para descargar el archivo en la ruta especificada." );
            }

            try{
                archivo.setLength(tam);
            }catch( Exception ex ){
                escribir(1);
                escribir("Error: Error al leer la información del archivo mientras se subía.");
                throw new Exception("Error: Error al leer la información del archivo mientras se descargaba.");
            }

            escribir(0);

            if( !mostrar ){
                progreso( tam, puntero );
            }
            
            while( tam - puntero > 0 ){
                if( leerInt() == 1 ){
                    throw new Exception( leerString() );
                }
                buffer = leerBytes();
                try{
                    archivo.write( buffer );
                    puntero = archivo.getFilePointer();
                    if( mostrar ){
                        for( int c = 0; c < buffer.length; c++ ){
                            System.out.print( (char)buffer[c] );
                        }
                    }else{
                        progreso( tam, puntero );
                    }
                }catch( Exception ex ){
                    escribir(1);
                    escribir("Error: Error al leer la información del archivo mientras se subía.");
                    throw new Exception("Error: Error al leer la información del archivo mientras se descargaba.");
                }
                escribir(0);
            }
        }catch( Exception ex ){
            throw new Exception( ex.getMessage() );
        }finally{
            try{
                archivo.close();
            }catch(Exception ex){}
            System.out.println();
        }
        
    }
    
    public byte[] leerBytes() throws Exception {
        try{
            
            byte[] entrada = new byte[4];
            int leidosTotal = 0;
            int leidosIteracion;

            while( leidosTotal < 4 ){
                leidosIteracion = cliente.getInputStream().read(entrada, leidosTotal, 4 - leidosTotal);
                if( leidosIteracion == -1 ){ throw new Exception(); }
                if( cliente.getSoTimeout() != 3000 ){ cliente.setSoTimeout(3000); }
                leidosTotal += leidosIteracion;
            }
            
            leidosTotal = 0;
            entrada = new byte[ ByteBuffer.wrap(entrada).getInt() ];
            
            while( leidosTotal < entrada.length ){
                leidosIteracion = cliente.getInputStream().read(entrada, leidosTotal, entrada.length - leidosTotal);
                if( leidosIteracion == -1 ){ throw new Exception(); }
                leidosTotal += leidosIteracion;
            }
            
            cliente.setSoTimeout(0);
            cliente.getOutputStream().write( new byte[]{0} );
            
            return entrada;
        }catch(Exception ex){
            throw new Exception("Error: No se puede recibir información porque la conexión está cerrada.");
        }
    }
    
    public void escribir( byte[] salida ) throws Exception {
        try{
            cliente.getOutputStream().write( ByteBuffer.allocate(4).putInt(salida.length).array() );
            cliente.getOutputStream().write( salida );
            if( cliente.getInputStream().read( new byte[]{0} ) == -1 ){ throw new Exception(); }
        }catch(Exception ex){
            throw new Exception("Error: No se puede enviar información porque la conexión está cerrada.");
        }
    }
    
    public void subir( boolean mostrar ) throws Exception {

        File directorio;
        RandomAccessFile archivo = null;
        byte[] buffer = new byte[ 10485760 ];
        long tam;
        long puntero = 0l;
        
        try{
            directorio = new File( leerString() );
            
            if( !directorio.isFile() ){
                escribir(1);
                escribir( "Error: No se encontró el archivo que se intenta descargar." );
                throw new Exception( "Error: No se encontró el archivo que se intenta subir." );
            }

            try{
                archivo = new RandomAccessFile( directorio, "rw" );
            }catch(Exception ex){
                escribir(1);
                escribir( "Error: No se tienen permisos para acceder al archivo que se intenta descargar." );
                throw new Exception( "Error: No se tienen permisos para acceder al archivo que se intenta subir." );
            }

            try{
                tam = archivo.length();
            }catch( Exception ex ){
                escribir(1);
                escribir("Error: Error al leer la información del archivo mientras se descargaba.");
                throw new Exception("Error: Error al leer la información del archivo mientras se subía.");
            }

            escribir(0);
            escribir( tam );

            if( leerInt() == 1 ){
                throw new Exception( leerString() );
            }

            if( !mostrar ){
                progreso( tam, puntero );
            }
            
            while( tam - puntero >= buffer.length ){
                try{
                    archivo.readFully(buffer);
                    puntero = archivo.getFilePointer();
                }catch( Exception ex ){
                    escribir(1);
                    escribir("Error: Error al leer la información del archivo mientras se descargaba.");
                    throw new Exception("Error: Error al leer la información del archivo mientras se subía.");
                }
                escribir(0);
                escribir(buffer);
                if( mostrar ){
                    for( int c = 0; c < buffer.length; c++ ){
                        System.out.print( (char)buffer[c] );
                    }
                }else{
                    progreso( tam, puntero );
                }
                if( leerInt() == 1 ){
                    throw new Exception( leerString() );
                }
            }
            if( tam - puntero > 0 ){
                buffer = new byte[ (int)( tam - puntero ) ];
                try{
                    archivo.readFully(buffer);
                    puntero = archivo.getFilePointer();
                }catch( Exception ex ){
                    escribir(1);
                    escribir("Error: Error al leer la información del archivo mientras se descargaba.");
                    throw new Exception("Error: Error al leer la información del archivo mientras se subía.");
                }
                escribir(0);
                escribir(buffer);
                if( mostrar ){
                    for( int c = 0; c < buffer.length; c++ ){
                        System.out.print( (char)buffer[c] );
                    }
                }else{
                    progreso( tam, puntero );
                }
                if( leerInt() == 1 ){
                    throw new Exception( leerString() );
                }
            }
        }catch( Exception ex ){
            throw new Exception( ex.getMessage() );
        }finally{
            try{
                archivo.close();
            }catch(Exception ex){}
            System.out.println();  
        }
    }
    
    public void escribir( String salida ) throws Exception {
        escribir( salida.getBytes() );
    }
    
    public void escribir( int salida ) throws Exception {
        escribir( ByteBuffer.allocate(4).putInt(salida).array() );
    }
    
    public void escribir( float salida ) throws Exception {
        escribir( ByteBuffer.allocate(4).putFloat(salida).array() );
    }
    
    public void escribir( long salida ) throws Exception {
        escribir( ByteBuffer.allocate(8).putLong(salida).array() );
    }
    
    public void escribir( double salida ) throws Exception {
        escribir( ByteBuffer.allocate(8).putDouble(salida).array() );
    }
    
    public void cerrar(){
        try{
            cliente.close();
        }catch( Exception ex ){}
    }
 
    public void progreso( long tam, long puntero ){
        for( int c = 0; c < 79; c++ ){
            System.out.print("\b");
        }
        System.out.print(" ");
        for( int c = 0; c < ( 78 * ( ( 100 * puntero ) / tam ) ) / 100; c++ ){
            System.out.print("█");
        }
        for( int c = 0; c < 78 - ( ( 78 * ( ( 100 * puntero ) / tam ) ) / 100 ); c++ ){
            System.out.print("░");
        }
    }
    
}
