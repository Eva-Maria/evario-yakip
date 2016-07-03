/**
 * Created by eve on 5/30/16.
 */
public class Config {

    public static final int[] MOVE_RANDOM_PLAYERS = new int[]{1, 2, 3};

    public static final int[] CLUSTER_SIZE = new int[]{5, 5, 5};
    public static final int[] TIMEOUT_STONES = new int[]{350, 750, 1100};
    public static final long SEED = 8;

    // stones: 0 fast, 1 middle, 2 fast
    public static final int[] WEIGHT_EMPTY_FIELD = new int[]{1, 3, 3};
    public static final int[] WEIGHT_SELF_COLORED_FIELD = new int[]{5, 5, 5}; //+HEAT
    public static final int[] WEIGHT_OPPONENT_COLORED_FIELD = new int[]{3, 1, 1};

    public static final String HOSTNAME = "127.0.0.1";
    public static final String[] SERVER_ARGS = {"800", "600", "60", SEED + ""};
    public static final String TEAM_NAME = "evario";
}
