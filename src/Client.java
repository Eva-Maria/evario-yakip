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
        NetworkClient network = new NetworkClient(hostname, Config.TEAM_NAME);
        Board board = new Board(network.getMyPlayerNumber());
        initBoardWithWall(board, network);
        Algorithm algorithm = new Algorithm(board, network);

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

                updateBoardWithPlayerPosition(board, network);
                updateBoardWithColors(board, network);

                if (network.getMyPlayerNumber() == 0) {
                    algorithm.getNextPath();
                }

                printAndWait(board, network);
            }
        }
    }

    private void updateBoardWithPlayerPosition(Board board, NetworkClient network) {
        int myPlayerNumber = network.getMyPlayerNumber();
        for (int stone = 0; stone < 3; stone++) {
            float x = network.getX(myPlayerNumber, stone);
            float y = network.getY(myPlayerNumber, stone);
            board.setStonePosition(stone, x, y);
        }
    }

    private void printAndWait(Board board, NetworkClient network) {
        if (network.getMyPlayerNumber() == 0) {
            System.out.println(board);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateBoardWithColors(Board board, NetworkClient network) {
        ColorChange cc;
        while ((cc = network.getNextColorChange()) != null) {
            board.setField(cc.x, cc.y, cc.newColor);
        }
    }

    private void initBoardWithWall(Board board, NetworkClient network) {
        for (int y = 0; y < Board.MAX_Y; y++) {
            for (int x = 0; x < Board.MAX_X; x++) {
                if (network.isWall(x, y)) {
                    board.setField(x, y, Board.WALL);
                }
            }
        }
    }
}
