
import lenz.htw.yakip.Server;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Created by eve on 4/11/16.
 */
public class Launcher {

    static final String SERVER_IS_ALREADY_RUNNING_DO_NOTHING = "Server is already running. Do nothing.";
    static final String MODE_AUTO = "auto";
    static final String MODE_FULLAUTO = "fullauto";

    static final Runnable serverLauncher = () -> {
        try {
            Server.main(Config.SERVER_ARGS);
        } catch (Exception e) {
            System.out.println(SERVER_IS_ALREADY_RUNNING_DO_NOTHING);
        }
    };

    public static void main(String... args) throws IOException {
        String hostName = Config.HOSTNAME;
        if (args.length >= 1) {
            hostName = args[0];
        }

        if (args.length < 2) {
            new ClientThreadManager(hostName);
            return;
        }

        if (args[1].equals(MODE_AUTO)) {
            new Thread(serverLauncher).start();
            waitSomeTime();
            new ClientThreadManager(hostName);
            new ClientThreadManager(hostName);
            new ClientThreadManager(hostName);
            pressSpace();
            waitSomeTime();
            return;
        }

        if (args[1].equals(MODE_FULLAUTO)) {
            new Thread(serverLauncher).start();
            waitSomeTime();
            new ClientThreadManager(hostName);
            new ClientThreadManager(hostName);
            new ClientThreadManager(hostName);
            new ClientThreadManager(hostName);
            waitSomeTime();
            pressSpace();
        }
    }

    private static void pressSpace() {
        try {
            final Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_SPACE);
            waitSomeTime();
            robot.keyRelease(KeyEvent.VK_SPACE);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void waitSomeTime() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
