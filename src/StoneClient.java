import lenz.htw.yakip.ColorChange;
import lenz.htw.yakip.net.NetworkClient;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by m on 7/2/16.
 */
class StoneClient implements Runnable {

    static final String EXCEPTION = "Exception caught. We do NOT stop on exception like other students!";
    static final float[] DONT_MOVE = new float[]{0.0f, 0.0f};

    private final int stone;
    private final NetworkClient network;
    private final Board board;
    private final int myPlayerNumber;

    private int lastPreviousPosition = 0;

    StoneClient(final int stone, final NetworkClient network, Board board) {
        this.stone = stone;
        this.network = network;
        this.board = board;

        this.myPlayerNumber = network.getMyPlayerNumber();
    }

    @Override
    public void run() {
        final int[][] previousPositions = new int[ClientThreadManager.PREVIOUS_POSITIONS][];

        final Random rnd = new Random(Config.SEED);
        final boolean moveRandomOnly = Arrays.binarySearch(Config.MOVE_RANDOM_PLAYERS, myPlayerNumber) >= 0;

        while (network.isAlive()) {
            if (moveRandomOnly) {
                moveRandom(network, rnd, stone);
                continue;
            }

            final long start = System.currentTimeMillis();
            boolean waitUntilFieldPassed = false;

            try {
                updateBoardWithPlayerPosition(board, network, previousPositions);
                updateBoardWithColors(board, network);

                float[] nextVector;
                final boolean selfColored = board.isSelfColored(stone);
                boolean hasNotMovedTooLong = hasNotMovedTooLong(previousPositions);

                if (stone == 0 && !selfColored) {
                    if (hasNotMovedTooLong) {
                        nextVector = getRandomVector(rnd);
                    } else {
                        nextVector = DONT_MOVE;
                    }
                } else if (hasNotMovedTooLong) {
                    nextVector = getRandomVector(rnd);
                } else {
                    nextVector = Algorithm.getNextVector(board, stone);
                    waitUntilFieldPassed = true;
                }
                network.setMoveDirection(stone, nextVector[0], nextVector[1]);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(EXCEPTION);
            }

            if (waitUntilFieldPassed) {
                final int diff = (int) (System.currentTimeMillis() - start);
                final int timeoutLeft = Config.TIMEOUT_STONES[stone] - diff;
                if (timeoutLeft > 0) {
                    wait(timeoutLeft);
                }
            } else {
                wait(60);
            }
        }
    }

    private float[] getRandomVector(Random rnd) {
        return new float[]{rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f};
    }

    private void moveRandom(NetworkClient network, Random rnd, int stone) {
        if (stone == 0 && rnd.nextBoolean()) {
            network.setMoveDirection(stone, 0.0f, 0.0f);
        } else {
            network.setMoveDirection(stone, rnd.nextFloat() - 0.5f, rnd.nextFloat() - 0.5f);
        }
        wait(100);
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
        try {
            while ((cc = network.getNextColorChange()) != null) {
                board.setField(cc.x, cc.y, cc.newColor);
            }
        } catch (ArrayIndexOutOfBoundsException ignore) {
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
