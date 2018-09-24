package simuladorparqueadero;

/**
 *
 * @author Arquisicion
 */
public class Entrada extends Thread {

    Simulador simulador;

    public Entrada(Simulador simulador) {
        this.simulador = simulador;
    }

    @Override
    public void run() {
        int millis = ((int) (Math.random() * 60 + 10)) * 1000;
        while (true) {
            try {
                simulador.EntrarCarro();
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
            }
        }
    }
}
