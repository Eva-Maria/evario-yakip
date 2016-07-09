/**
 * Created by eve on 6/13/16.
 */
public class Board {

    static final int MAX_Y = 32;
    static final int MAX_X = 32;

    static final int EMPTY = -2;
    static final int WALL = -1;

    final static String RESET_OUTPUT_COLOR = (char) 27 + "[0m";
    final static String UNDERLINE_OUTPUT_COLOR = (char) 27 + "[4m";
    final static String[] PLAYER_OUTPUT_COLORS = new String[]{
            (char) 27 + "[31m", // red
            (char) 27 + "[34m", // blue
            (char) 27 + "[93m", // yellow
            (char) 27 + "[32m", // green
    };

    private final int myPlayerNumber; // 0..3 players

    private int fields[][] = new int[MAX_Y][MAX_X]; // 0..31 by 0..31 field coordinates
    private int heatMap[][] = new int[MAX_Y][MAX_X]; // 0..31 by 0..31 field coordinates

    private float[][] stonePosition = new float[3][]; // 0..2 stones by 0..31 field coordinates
    private int playerPoints[] = new int[4]; // 0..3 players by points

    Board(int myPlayerNumber) {
        this.myPlayerNumber = myPlayerNumber;
        for (int y = 0; y < Board.MAX_Y; y++) {
            for (int x = 0; x < Board.MAX_X; x++) {
                setField(x, y, EMPTY);
            }
        }
    }

    int[][] getFields() {
        // NB: not synchronized / read ONLY!
        return fields;
    }

    float[] getStonePosition(int stone) {
        return stonePosition[stone];
    }

    void setStonePosition(int stoneNumber, float x, float y) {
        stonePosition[stoneNumber] = new float[]{x, y};
    }

    public int[][] getHeatMap() {
        return heatMap;
    }

    void setField(int x, int y, int value) {
        synchronized (fields) {
//            if (x == 0 && y == 0 && value == 0) {
//                return;
//            }

            if (value >= 0 && value <= 3) {
                playerPoints[value]++;

                int oldValue = fields[y][x];
                if (oldValue >= 0 && oldValue <= 3) {
                    playerPoints[oldValue]--;
                }
            }

            fields[y][x] = value;
            if (value == myPlayerNumber) {
                heatMap[y][x] = heatMap[y][x] + 1;
            }
        }
    }

    boolean isSelfColored(int stone) {
        final float[] stonePosition = this.stonePosition[stone];
        return fields[(int) stonePosition[1]][(int) stonePosition[0]] == myPlayerNumber;
    }

    public int getPlayerNumber() {
        return myPlayerNumber;
    }

    @Override
    public String toString() {
        return toString(new int[0][]);
    }

    public String toString(final int[][] path) {
        StringBuilder builder = new StringBuilder();

        try {
//            for (int y = 0; y < Board.MAX_Y; y++) {
            for (int y = Board.MAX_Y - 1; y >= 0; y--) {
                for (int x = 0; x < Board.MAX_X; x++) {
                    int field = fields[y][x];
                    //////////

                    boolean isInPath = false;
                    for (int i = 0; i < path.length; i++) {
                        int[] shortestPathFieldCoordinate = path[i];
                        final int spX = shortestPathFieldCoordinate[0];
                        final int spY = shortestPathFieldCoordinate[1];
                        if (spX == x && spY == y) {
                            isInPath = true;
                            break;
                        }
                    }

                    if (isInPath) {
                        builder.append(UNDERLINE_OUTPUT_COLOR);
                    }

                    //////////

                    if (field == WALL) {
                        builder.append("x");
                    } else if (field == EMPTY) {
                        builder.append(" ");
                    } else {
                        String playerOutputColor = PLAYER_OUTPUT_COLORS[field];
                        builder.append(playerOutputColor).append('#');
                    }

                    //////////

                    String stoneOutput = null;

                    for (int stone = 0; stone < 3; stone++) {
                        float[] position = stonePosition[stone];
                        int playerPosX = (int) position[0];
                        int playerPosY = (int) position[1];

                        if (playerPosX == x && playerPosY == y) {
                            String playerOutputColor = PLAYER_OUTPUT_COLORS[myPlayerNumber];
                            stoneOutput = playerOutputColor + stone;
                            break;
                        }
                    }

                    //////////
                    if (stoneOutput != null) {
                        builder.append(stoneOutput);
                    } else {
                        builder.append(" ");
                    }
                    builder.append(RESET_OUTPUT_COLOR);
                }
                builder.append("\n");
            }

            for (int player = 0; player < 4; player++) {
                String playerOutputColor = PLAYER_OUTPUT_COLORS[player];
                int points = playerPoints[player];
                builder.append(playerOutputColor).append(player).append(":").append(points).append(RESET_OUTPUT_COLOR).append(" ");
            }

            builder.append("\n");

        } catch (Exception e) {
            return e.getMessage() + " during toString()";
        }

        return builder.toString();
    }
}
