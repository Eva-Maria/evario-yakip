import lenz.htw.yakip.net.NetworkClient;

import java.util.List;

/**
 * Created by m on 6/26/16.
 */
public class Algorithm {
    public static final int WEIGHT_EMPTY_FIELD = 2;
    public static final int WEIGHT_OPPONENT_FIELD = 2;
    private final Board board;
    private final NetworkClient network;
    private final int myPlayerNumber;

    public Algorithm(Board board, NetworkClient network) {
        this.board = board;
        this.network = network;

        myPlayerNumber = network.getMyPlayerNumber();

    }

    public List<int[]> getNextPath() {


        int[][] fields = board.getFields();
        float[][] playerPosition = board.getPlayerPosition();

        int matrix[][] = new int[Board.MAX_Y][Board.MAX_X];

        int playerPosX = (int) playerPosition[0][0];
        int playerPosY = (int) playerPosition[0][1];
        int weight = 0;
        for (int x = playerPosX - 5; x < playerPosX + 5; x++) {
            if (x < 0 || x > Board.MAX_X) {
                continue;
            }

            for (int y = playerPosX - 5; y < playerPosY + 5; y++) {
                if (y < 0 || y > Board.MAX_Y) {
                    continue;
                }

                int field = fields[y][x];
                if (field == Board.WALL) {
                    weight = Integer.MAX_VALUE;
                } else if (field == Board.EMPTY) {
                    weight = WEIGHT_EMPTY_FIELD;
                } else if (field == myPlayerNumber) {
                    weight = Integer.MAX_VALUE;
                } else {
                    weight = WEIGHT_OPPONENT_FIELD;
                }


                matrix[y][x] = weight;

            }
        }


        return null;
    }
}
