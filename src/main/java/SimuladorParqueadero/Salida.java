package SimuladorParqueadero;

/**
 *
 * @author Arquisicion
 */
public class Salida extends Thread {

    Simulador simulador;

    public Salida(Simulador simulador) {
        this.simulador = simulador;
    }

    @Override
    public void run() {
        int millis = ((int) (Math.random() * 60 + 10)) * 1000;
        while (true) {
            try {
                Thread.sleep(millis);
                simulador.SalirCarro();

            } catch (InterruptedException ex) {
            }
        }
    }
}
