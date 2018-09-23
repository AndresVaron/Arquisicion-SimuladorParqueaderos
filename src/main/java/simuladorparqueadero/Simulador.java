package simuladorparqueadero;

import java.util.Scanner;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;

/**
 * @author Arquisicion
 */
public class Simulador {

    /**
     * Representa el id del parqueadero.
     */
    private static Long id;

    /**
     * Representa los cupos del parqueadero.
     */
    private static int cupos;

    /**
     * Representa el numero de torniquetes de entrada a simular.
     */
    private static int entrada;

    /**
     * Representa el numero de torniquetes de salida a simular.
     */
    private static int salida;

    /**
     * Representa el numero actual de cupos disponibles en el parqueadero.
     */
    private static int cuposActuales;

    /**
     * Metodo main de la aplicacion, recibe los parametros e inicializa la
     * aplicacion.
     *
     * @param args id y cupos de la aplicacion.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            id = Long.parseLong(args[0]);
            cupos = Integer.parseInt(args[1]);
            entrada = Integer.parseInt(args[2]);
            salida = Integer.parseInt(args[3]);
        } else {
            Scanner reader = new Scanner(System.in);
            System.out.println("Ingrese el id del parqueadero");
            id = reader.nextLong();
            System.out.println("Ingrese el numero de cupos disponibles en el parqueadero");
            cupos = reader.nextInt();
            System.out.println("Ingrese el numero de entradas vehiculares a simular");
            entrada = reader.nextInt();
            System.out.println("Ingrese el numero de entradas vehiculares a simular");
            salida = reader.nextInt();
        }
        cuposActuales = cupos;
        try {
            new Simulador();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static synchronized int darCuposActuales() {
        return cuposActuales;
    }

    public static synchronized void aumentarCupo() {
        cuposActuales++;
    }

    public static synchronized void disminuirCupo() {
        cuposActuales--;
    }

    //Metodos del simulador
    public Simulador() throws Exception {
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.24.41.199:8084");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        try (org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);) {
                ProducerRecord<String, String> rec = new ProducerRecord<>("Update_Cupos", "{ \"id\": 1, \"tipo\":\"llegada\"}");
                producer.send(rec);          
        }
    }
}
