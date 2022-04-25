package top.kkoishi.screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author KKoishi_
 */
public final class InitialDispaly extends JFrame {

    private static InitialDispaly instance = new InitialDispaly();

    public static InitialDispaly getInstance() {
        return instance;
    }

    public static void clearDisplay () {
        instance.removeAll();
        instance.dispose();
        instance = null;
    }

    private InitialDispaly () throws HeadlessException {
        final var dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - 600, dimension.height / 2 - 400);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setOpacity(0.8f);
        setSize(1200, 796);
        setVisible(true);
    }

    @Override
    public void paint (Graphics g) {
        try {
            final var bi = ImageIO.read(new File("./data/initial.jpg"));
            g.drawImage(bi, 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
