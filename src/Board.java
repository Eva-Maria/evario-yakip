/**
 * Created by eve on 6/13/16.
 */
public class Board {

    public static final int MAX_Y = 32;
    public static final int MAX_X = 32;
    private static int fields[][] = new int[MAX_Y][MAX_X];

    public static final int WALL = -1;

    public static void setField(int x, int y, int value) {
        fields[y][x] = value;
    }

    public static String draw() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < Board.MAX_Y; y++) {
            for (int x = 0; x < Board.MAX_X; x++) {
                int field = fields[y][x];
                if (field == WALL) {
                    builder.append("x");
                } else {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }
}
