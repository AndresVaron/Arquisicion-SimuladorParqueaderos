package simuladorparqueadero;

import java.io.*;
import java.net.*;

/**
 *
 * @author Arquisicion
 */
public class Encuentro extends Thread {

    /**
     * El canal usado para comunicarse con el servidor
     */
    private Socket socket;

    /**
     * El flujo de escritura conectado con el servidor de nidoo
     */
    private PrintWriter out;

    /**
     * El flujo de lectura conectado con el servidor de nidoo
     */
    private BufferedReader in;

    /**
     * El simulador de la aplicacion
     */
    private Simulador simulador;

    /**
     * Indica que el encuentro debe terminar
     */
    private boolean fin;

    public Encuentro(Socket canal, Simulador simulador) throws IOException {
        this.simulador = simulador;
        this.socket = canal;

        out = new PrintWriter(canal.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(canal.getInputStream()));

        fin = false;
    }

    /**
     * Metodo que recibe los mensajes del servidor de nidoo y los procesa.
     */
    @Override
    public void run() {
        System.out.println("Encuentro inicializado");
        try {
            while (!fin) {
                String info = in.readLine();
                if (info != null) {
                    //El formato deberia ser   
                    // INFORMACION:CUPOS:ID=x
                    if (info.startsWith("INFORMACION")) {
                        String[] params = info.split(":");
                        boolean validado = false;
                        for (String param : params) {
                            if (param.contains("ID") && Long.parseLong(param.split("=")[1]) == simulador.getId()) {
                                out.println("CONSULTA:ID=" + getId() + ":CUPOS=" + simulador.darCuposActuales());
                                System.out.println("CONSULTA:ID=" + getId() + ":CUPOS=" + simulador.darCuposActuales());
                                validado = true;
                            }
                        }
                        if (!validado) {
                            throw new Exception("Los ids no corresponden");
                        }
                    } else if (info.startsWith("RESERVA")) {
                        System.out.println("RESERVA");
                        System.out.println(info);
                        simulador.disminuirCupo();
                    }
                    else if(info.startsWith("FINALIZAR")&&info.split(":").length>1&&info.split(":")[1].equals("ID="+simulador.getId())){
                        System.out.println("FINALIZAR");
                        simulador.aumentarCupo();
                        out.println("OK");
                    }
                    else if (info.startsWith("FIN")) {
                        System.out.println("Fin conexion");
                        fin = true;
                    }
                } else {
                    throw new Exception("Se recibió una cadena nula");
                }
            }

            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e1) {
            }
        }
    }

}
