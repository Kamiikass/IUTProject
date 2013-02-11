package vtplayer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author godeau
 */
public class Test {

    public static void main(String args[]) {
        try {
            // valide "/dev/char/180:0"
            VTPlayer vTPlayer = new VTPlayer("/dev/char/180:0"/*VTPlayer.findDevice()*/);
            if (vTPlayer.isOpen()) {
                for (int i = 0; i < 10; i++) {
                    vTPlayer.set("99 77 B9 5D");
                    Thread.sleep(1000);
                    vTPlayer.set("B9 5D 99 77");
                    Thread.sleep(1000);
                }
                vTPlayer.close();
            } else {
                System.err.println("Le périphérique a pu être ouvert.");
            }
        } catch (VTPlayerException | InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
