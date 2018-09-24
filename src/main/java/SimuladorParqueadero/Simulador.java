package simuladorparqueadero;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.kafka.clients.producer.*;
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
     * Productor de kafka de la aplicacion.
     */
    private org.apache.kafka.clients.producer.Producer producer;

    /**
     * Lista de entradas.
     */
    private List<Entrada> entradas;

    /**
     * Lista de salidas.
     */
    private List<Salida> salidas;

    /**
     * Metodo main de la aplicacion, recibe los parametros e inicializa la
     * aplicacion.
     *
     * @param args id y cupos de la aplicacion.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            setId((Long) Long.parseLong(args[0]));
            setCupos(Integer.parseInt(args[1]));
            setEntrada(Integer.parseInt(args[2]));
            setSalida(Integer.parseInt(args[3]));
        } else {
            Scanner reader = new Scanner(System.in);
            System.out.println("Ingrese el id del parqueadero");
            setId((Long) reader.nextLong());
            System.out.println("Ingrese el numero de cupos disponibles en el parqueadero");
            setCupos(reader.nextInt());
            System.out.println("Ingrese el numero de entradas vehiculares a simular");
            setEntrada(reader.nextInt());
            System.out.println("Ingrese el numero de entradas vehiculares a simular");
            setSalida(reader.nextInt());
        }
        setCuposActuales(getCupos());
        try {
            new Simulador();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized int darCuposActuales() {
        return getCuposActuales();
    }

    public synchronized void aumentarCupo() {
        setCuposActuales(getCuposActuales() + 1);
        System.out.println("{ \"id\": " + getId() + ", \"tipo\":\"salida\"}" + getCuposActuales());
        ProducerRecord<String, String> rec = new ProducerRecord<>("Update_Cupos", "{ \"id\": " + getId() + ", \"tipo\":\"salida\"}");
        try {
            getProducer().send(rec);
        } catch (Exception e) {
        }
    }

    public synchronized void disminuirCupo() {
        setCuposActuales(getCuposActuales() - 1);
        System.out.println("{ \"id\": " + getId() + ", \"tipo\":\"llegada\"}" + getCuposActuales());
        ProducerRecord<String, String> rec = new ProducerRecord<>("Update_Cupos", "{ \"id\": " + getId() + ", \"tipo\":\"llegada\"}");
        try {
            getProducer().send(rec);
        } catch (Exception e) {
        }
    }

    public Simulador() {
        System.out.println("Iniciando simulador");
        entradas = new ArrayList<>();
        salidas = new ArrayList<>();
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.24.41.199:8084");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer(configProperties);
        Servidor servidor = new Servidor(this);
        System.out.println("Iniciando entradas");
        for (int i = 0; i < entrada; i++) {
            Entrada entrada = new Entrada(this);
            entradas.add(entrada);
            entrada.start();
        }
        System.out.println("Iniciando salidas");
        for (int i = 0; i < salida; i++) {
            Salida salida = new Salida(this);
            salidas.add(salida);
            salida.start();
        }
        servidor.recibirConexiones();
    }

    public synchronized void EntrarCarro() {

        while (darCuposActuales() == 0) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("Carro Entrando");
        disminuirCupo();
        notifyAll();
    }

    public synchronized void SalirCarro() {
        while (darCuposActuales() == getCupos()) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("Carro Saliendo");
        aumentarCupo();
        notifyAll();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param aId the id to set
     */
    public static void setId(Long aId) {
        id = aId;
    }

    /**
     * @return the cupos
     */
    public static int getCupos() {
        return cupos;
    }

    /**
     * @param aCupos the cupos to set
     */
    public static void setCupos(int aCupos) {
        cupos = aCupos;
    }

    /**
     * @return the entrada
     */
    public static int getEntrada() {
        return entrada;
    }

    /**
     * @param aEntrada the entrada to set
     */
    public static void setEntrada(int aEntrada) {
        entrada = aEntrada;
    }

    /**
     * @return the salida
     */
    public static int getSalida() {
        return salida;
    }

    /**
     * @param aSalida the salida to set
     */
    public static void setSalida(int aSalida) {
        salida = aSalida;
    }

    /**
     * @return the cuposActuales
     */
    private static int getCuposActuales() {
        return cuposActuales;
    }

    /**
     * @param aCuposActuales the cuposActuales to set
     */
    private static void setCuposActuales(int aCuposActuales) {
        cuposActuales = aCuposActuales;
    }

    /**
     * @return the producer
     */
    public org.apache.kafka.clients.producer.Producer getProducer() {
        return producer;
    }

}
