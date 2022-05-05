package utilidades;

public class Validacion {
    
    public static void validar( String[] argumentos, String[] args ){
        
        boolean valido = true;
        
        if( args.length != argumentos.length / 2 ){
            
            System.err.println("No se agregó la cantidad de argumentos solicitados:");
            
            for( int c = 0; c < argumentos.length; c+=2 ){
                System.err.println( ( (c/2) + 1 ) + " .- " + argumentos[c+1] );
            }
            
            System.exit(0);
        }
        
        for( int c = 0; c < argumentos.length; c+=2 ){
            
            switch( argumentos[c] ){
                
                case "int":
                    try{
                        Integer.valueOf( args[c/2] );
                    }catch( Exception ex ){
                        System.err.println("Argumento: " + ( (c/2) + 1 ) + " no es válido.");
                        System.err.println( ( (c/2) + 1 ) + " .- " + argumentos[c+1] );
                        valido = false;
                    }
                    break;  
            }
        }
        
        if( !valido ){
            System.exit(1);
        }
        
    }
}
