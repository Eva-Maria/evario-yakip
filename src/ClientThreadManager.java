import lenz.htw.yakip.net.NetworkClient;

/**
 * Created by eve on 5/30/16.
 */
public class ClientThreadManager implements Runnable {

    static final int STONE_COUNT = 3;
    static final int PREVIOUS_POSITIONS = 5;
    private final String hostname;

    ClientThreadManager(final String hostname) {
        this.hostname = hostname;
        new Thread(this).start();
    }

    @Override
    public void run() {
        final NetworkClient network = new NetworkClient(hostname, Config.TEAM_NAME);

        Board board = new Board(network.getMyPlayerNumber());
        initBoardWithWall(board, network);

        for (int stone = 0; stone < STONE_COUNT; stone++) {
            final StoneClient target = new StoneClient(stone, network, board);
            new Thread(target).start();
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
