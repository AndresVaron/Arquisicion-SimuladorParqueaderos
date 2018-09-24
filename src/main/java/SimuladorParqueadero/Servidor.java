package simuladorparqueadero;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 *
 * @author Arquisicion
 */
public class Servidor extends Thread {

    /**
     * Es el punto por el cual los clientes solicitan conexiones.
     */
    private ServerSocket receptor;

    /**
     * Representa el simulador de la aplicacion.
     */
    private Simulador simulador;

    /**
     * Es una colección con los encuentros que se están llevando a cabo en este
     * momento
     */
    protected Collection encuentros;

    /**
     *
     * @param simulador simulador de la aplicacion
     */
    public Servidor(Simulador simulador) {
        this.simulador = simulador;
    }

    /**
     * Este método se encarga de recibir todas las conexiones entrantes.
     *
     */
    public void recibirConexiones() {
        System.out.println("Recibiendo conexiones");
        try {
            receptor = new ServerSocket(0);
            System.out.println("{ \"id\": " + simulador.getId() + ", \"cupos\":" + simulador.darCuposActuales() + ", \"ip\":\"" + InetAddress.getLocalHost().getHostAddress() + "\"" + ", \"puerto\":\"" + receptor.getLocalPort() + "\"}");
            ProducerRecord<String, String> rec = new ProducerRecord<>("Update_Conexion", "{ \"id\": " + simulador.getId() + ", \"cupos\":" + simulador.darCuposActuales() + ", \"ip\":\"" + InetAddress.getLocalHost().getHostAddress() + "\"" + ", \"puerto\":\"" + receptor.getLocalPort() + "\"}");
            try {
                simulador.getProducer().send(rec);
            } catch (Exception e) {
            }
            while (true) {
                Socket socketConexion = receptor.accept();
                System.out.println("Conexion Recibida");
                Encuentro encuentro = new Encuentro(socketConexion, simulador);
                encuentros.add(encuentro);
                encuentro.start();
                try {
                    socketConexion.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                receptor.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void run() {
        recibirConexiones();
    }
}
