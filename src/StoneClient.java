import lenz.htw.yakip.ColorChange;
import lenz.htw.yakip.net.NetworkClient;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by m on 7/2/16.
 */
class StoneClient implements Runnable {
    private final int stone;
    private final NetworkClient network;
    private final Board board;
    private final long seed;
    private final int timeout;
    private final int myPlayerNumber;

    private int lastPreviousPosition = 0;

    StoneClient(final int stone, final NetworkClient network, Board board) {
        this.stone = stone;
        this.network = network;
        this.board = board;

        this.myPlayerNumber = network.getMyPlayerNumber();

        this.seed = Config.CLIENT_SEED;
        this.timeout = getTimeoutForStone(stone);
    }

    static int getTimeoutForStone(int stone) {
        switch (stone) {
            case 0:
                return 350;
            case 1:
                return 750;
            default:
            case 2:
                return 1100;
        }
    }

    @Override
    public void run() {
        final int[][] previousPositions = new int[ClientThreadManager.PREVIOUS_POSITIONS][];

        Random rnd = new Random(seed);

        while (network.isAlive()) {
            if (network.getMyPlayerNumber() != 0) {
                moveRandom(network, rnd, stone);
                continue;
            }

            final long start = System.currentTimeMillis();

            updateBoardWithPlayerPosition(board, network, previousPositions);
            updateBoardWithColors(board, network);

            float[] nextVector;
            if (stone == 0 && !board.isSelfColored(stone)) {
                nextVector = new float[]{0.0f, 0.0f};
            } else if (hasNotMovedTooLong(previousPositions)) {
                nextVector = new float[]{rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f};
            } else {
                nextVector = Algorithm.getNextVector(board, stone);
            }
            network.setMoveDirection(stone, nextVector[0], nextVector[1]);

            final int diff = (int) (System.currentTimeMillis() - start);
            final int timeoutLeft = timeout - diff;
            if (timeoutLeft > 0) {
                wait(timeoutLeft);
            }
        }
    }

    private void moveRandom(NetworkClient network, Random rnd, int stone) {
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

    private void updateBoardWithPlayerPosition(Board board, NetworkClient network, int[][] previousPositions) {
        float x = network.getX(myPlayerNumber, stone);
        float y = network.getY(myPlayerNumber, stone);
        board.setStonePosition(stone, x, y);
        previousPositions[lastPreviousPosition] = new int[]{(int) x, (int) y};

        lastPreviousPosition = (lastPreviousPosition + 1) % ClientThreadManager.PREVIOUS_POSITIONS;
    }

    private void updateBoardWithColors(Board board, NetworkClient network) {
        ColorChange cc;
        while ((cc = network.getNextColorChange()) != null) {
            board.setField(cc.x, cc.y, cc.newColor);
        }
    }

    private void wait(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
