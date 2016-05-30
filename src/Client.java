import lenz.htw.yakip.net.NetworkClient;

import java.util.Random;

/**
 * Created by eve on 5/30/16.
 */
public class Client implements Runnable {

    private final String hostname;
    private final long seed;

    public Client(String hostname, long seed) {
        this.hostname = hostname;
        this.seed = seed;

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NetworkClient network = new NetworkClient(hostname, Config.TEAM_NAME);

        Random rnd = new Random(seed);
        while (network.isAlive()) {
            for (int i = 0; i < 3; ++i) {
                network.setMoveDirection(i, rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f);
                network.getMyPlayerNumber();
                network.getX(network.getMyPlayerNumber(), 1);
                network.isWall(3, 5);
                /*ColorChange cc;
                while ((cc = network.getNextColorChange()) != null) {
                    //TODO farben in spielbrett einarbeiten
                }*/
            }
        }
    }
}
