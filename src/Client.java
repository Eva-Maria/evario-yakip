import lenz.htw.yakip.ColorChange;
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

        setWallOnBoard(network);

        Random rnd = new Random(seed);
        while (network.isAlive()) {
            for (int stone = 0; stone < 3; ++stone) {
                if (stone == 0 && rnd.nextBoolean()) {
                    network.setMoveDirection(stone, 0.0f, 0.0f);
                } else {
                    network.setMoveDirection(stone, rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f);
                }
//                network.getMyPlayerNumber();
//                network.getX(network.getMyPlayerNumber(), 1);

                ColorChange cc;
                while ((cc = network.getNextColorChange()) != null) {
                    //TODO farben in spielbrett einarbeiten
                }
            }
        }
    }

    private void setWallOnBoard(NetworkClient network) {
        for (int y = 0; y < Board.MAX_Y; y++) {
            for (int x = 0; x < Board.MAX_X; x++) {
                if (network.isWall(x, y)) {
                    Board.setField(x, y, Board.WALL);
                }
            }
        }
        System.out.println(Board.draw());
    }
}
