import lenz.htw.yakip.net.NetworkClient;

/**
 * Created by eve on 5/30/16.
 */
public class ClientThreadManager implements Runnable {

    static final int STONE_COUNT = 3;
    static final int PREVIOUS_POSITIONS = 3;
    private final String hostname;

    ClientThreadManager(final String hostname) {
        this.hostname = hostname;
        new Thread(this).start();
    }

    @Override
    public void run() {
        final NetworkClient network = new NetworkClient(hostname, Config.TEAM_NAME);

        for (int stone = 0; stone < STONE_COUNT; stone++) {
            final StoneClient target = new StoneClient(stone, network);
            new Thread(target).start();
        }
    }

}
