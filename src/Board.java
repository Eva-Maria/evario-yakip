/**
 * Created by eve on 6/13/16.
 */
public class Board {

    public static final int MAX_Y = 32;
    public static final int MAX_X = 32;

    public static final int EMPTY = -2;
    public static final int WALL = -1;

    final static String RESET_OUTPUT_COLOR = (char) 27 + "[0m";
    final static String[] PLAYER_OUTPUT_COLORS = new String[]{
            (char) 27 + "[31m", // red
            (char) 27 + "[34m", // blue
            (char) 27 + "[93m", // yellow
            (char) 27 + "[32m", // green
    };

    private final int myPlayerNumber; // 0..3 players
    private int fields[][] = new int[MAX_Y][MAX_X]; // 0..31 by 0..31 field coordinates
    private float[][] stonePosition = new float[3][]; // 0..2 stones by 0..31 field coordinates
    private int playerPoints[] = new int[4]; // 0..3 players by points

    public Board(int myPlayerNumber) {
        this.myPlayerNumber = myPlayerNumber;
        for (int y = 0; y < Board.MAX_Y; y++) {
            for (int x = 0; x < Board.MAX_X; x++) {
                setField(x, y, EMPTY);
            }
        }
    }

    public int[][] getFields() {
        return fields;
    }

    public float[][] getStonePosition() {
        return stonePosition;
    }

    public void setStonePosition(int stoneNumber, float x, float y) {
        stonePosition[stoneNumber] = new float[]{x, y};
    }

    public void setField(int x, int y, int value) {
        if (x == 0 && y == 0 && value == 0) {
            return;
        }

        if (value >= 0 && value <= 3) {
            playerPoints[value]++;

            int oldValue = fields[y][x];
            if (oldValue >= 0 && oldValue <= 3) {
                playerPoints[oldValue]--;
            }
        }

//        System.out.println("x: " + x + " y: " + y + ", value: " + value);
        fields[y][x] = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < Board.MAX_Y; y++) {
//        for (int y = Board.MAX_Y - 1; y >= 0; y--) {
            for (int x = 0; x < Board.MAX_X; x++) {
                int field = fields[y][x];

                if (field == WALL) {
                    builder.append("x");
                } else if (field == EMPTY) {
                    builder.append(" ");
                } else {
                    String playerOutputColor = PLAYER_OUTPUT_COLORS[field];
                    builder.append(playerOutputColor).append('#').append(RESET_OUTPUT_COLOR);
                }

                boolean hasStone = false;

                for (int stone = 0; stone < 3; stone++) {
                    float[] position = stonePosition[stone];
                    int playerPosX = (int) position[0];
                    int playerPosY = (int) position[1];

                    if (playerPosX == x && playerPosY == y) {
                        hasStone = true;
                        String playerOutputColor = PLAYER_OUTPUT_COLORS[myPlayerNumber];
                        builder.append(playerOutputColor).append(stone).append(RESET_OUTPUT_COLOR);
                        break;
                    }
                }

                if (!hasStone) {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }

        for (int player = 0; player < 4; player++) {
            String playerOutputColor = PLAYER_OUTPUT_COLORS[player];
            int points = playerPoints[player];
            builder.append(playerOutputColor).append(player).append(":").append(points).append(RESET_OUTPUT_COLOR).append(" ");
        }

        builder.append("\n");
        return builder.toString();
    }
}
