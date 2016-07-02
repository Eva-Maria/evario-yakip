import lenz.htw.yakip.ColorChange;
import lenz.htw.yakip.net.NetworkClient;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by eve on 5/30/16.
 */
public class Client implements Runnable {

    public static final int STONE_COUNT = 3;
    public static final int PREVIOUS_POSITIONS = 3;

    private final String hostname;
    private final long seed;

    private int lastPreviousPosition = 0;

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

        final int[][][] previousPositions = new int[STONE_COUNT][PREVIOUS_POSITIONS][];

        Random rnd = new Random(seed);

        while (network.isAlive()) {
            for (int stone = 0; stone < STONE_COUNT; ++stone) {

                updateBoardWithPlayerPosition(board, network, previousPositions);
                updateBoardWithColors(board, network);

                if (network.getMyPlayerNumber() == 0) {
                    float[] nextVector;
                    if (hasNotMovedTooLong(previousPositions[stone])) {
                        nextVector = new float[]{rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f};
                    } else {
                        nextVector = algorithm.getNextVector(stone);
                    }
                    network.setMoveDirection(stone, nextVector[0], nextVector[1]);
                    wait(500);
                } else {
                    moveOpponentRandom(network, rnd, stone);
                }
            }
        }

    }

    private void moveOpponentRandom(NetworkClient network, Random rnd, int stone) {
        if (stone == 0 && rnd.nextBoolean()) {
            network.setMoveDirection(stone, 0.0f, 0.0f);
        } else {
            network.setMoveDirection(stone, rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f);
        }
        wait(2000);
    }

    private boolean hasNotMovedTooLong(int[][] previousPosition) {
        int[] previous = previousPosition[0];
        for (int i = 1; i < previousPosition.length; i++) {
            final int[] nextPrevious = previousPosition[i];
            if (!Arrays.equals(previous, nextPrevious)) {
                return false;
            }
        }

        return true;
    }

    private void updateBoardWithPlayerPosition(Board board, NetworkClient network, int[][][] previousPositions) {
        int myPlayerNumber = network.getMyPlayerNumber();
        for (int stone = 0; stone < STONE_COUNT; stone++) {
            float x = network.getX(myPlayerNumber, stone);
            float y = network.getY(myPlayerNumber, stone);
            board.setStonePosition(stone, x, y);
            previousPositions[stone][lastPreviousPosition] = new int[]{(int) x, (int) y};
        }

        lastPreviousPosition = (lastPreviousPosition + 1) % PREVIOUS_POSITIONS;
    }

    private void wait(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
