/**
 * Created by eve on 6/13/16.
 */
public class Board {

    public static final int MAX_Y = 32;
    public static final int MAX_X = 32;

    public static final int EMPTY = -2;
    public static final int WALL = -1;
    public static final int PLAYER_0 = 0;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int PLAYER_3 = 3;

    final static String BLACK_COLOR = (char) 27 + "[30m";
    final static String BLUE_COLOR = (char) 27 + "[34m";
    final static String RED_COLOR = (char) 27 + "[31m";
    final static String GREEN_COLOR = (char) 27 + "[32m";
    final static String YELLOW_COLOR = (char) 27 + "[93m";

    private int fields[][] = new int[MAX_Y][MAX_X];
    private int playerPoints[] = new int[4];

    public Board() {
        for (int y = 0; y < Board.MAX_Y; y++) {
            for (int x = 0; x < Board.MAX_X; x++) {
                setField(x, y, EMPTY);
            }
        }
    }

    public void setField(int x, int y, int value) {
        if (x == 0 && y == 0 && value == 0) {
            return;
        }

        if (value >= PLAYER_0 && value <= PLAYER_3) {
            playerPoints[value]++;

            int oldValue = fields[y][x];
            if (oldValue >= PLAYER_0 && oldValue <= PLAYER_3) {
                playerPoints[oldValue]--;
            }
        }

//        System.out.println("x: " + x + " y: " + y + ", value: " + value);
        fields[y][x] = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = Board.MAX_Y-1; y >= 0; y--) {
            for (int x = 0; x < Board.MAX_X; x++) {
                int field = fields[y][x];
                if (field == WALL) {
                    builder.append("x").append(" ");
                } else if (field == PLAYER_0) {
                    builder.append(RED_COLOR).append(PLAYER_0).append(BLACK_COLOR).append(" ");
                } else if (field == PLAYER_1) {
                    builder.append(BLUE_COLOR).append(PLAYER_1).append(BLACK_COLOR).append(" ");
                } else if (field == PLAYER_2) {
                    builder.append(YELLOW_COLOR).append(PLAYER_2).append(BLACK_COLOR).append(" ");
                } else if (field == PLAYER_3) {
                    builder.append(GREEN_COLOR).append(PLAYER_3).append(BLACK_COLOR).append(" ");
                } else if (field == EMPTY) {
                    builder.append(" ").append(" ");
                }
            }
            builder.append("\n");
        }
        builder.append(RED_COLOR).append(PLAYER_0).append(":").append(playerPoints[PLAYER_0]).append(BLACK_COLOR).append(" ");
        builder.append(BLUE_COLOR).append(PLAYER_1).append(":").append(playerPoints[PLAYER_1]).append(BLACK_COLOR).append(" ");
        builder.append(YELLOW_COLOR).append(PLAYER_2).append(":").append(playerPoints[PLAYER_2]).append(BLACK_COLOR).append(" ");
        builder.append(GREEN_COLOR).append(PLAYER_3).append(":").append(playerPoints[PLAYER_3]).append(BLACK_COLOR).append(" ");
        builder.append("\n");
        return builder.toString();
    }
}
