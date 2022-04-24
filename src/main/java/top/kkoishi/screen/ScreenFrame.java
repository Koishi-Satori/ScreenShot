package top.kkoishi.screen;

import java.awt.Frame;
import java.awt.HeadlessException;

/**
 * @author KKoishi_
 */
public final class ScreenFrame extends Frame {
    public ScreenFrame () throws HeadlessException {
        setSize(getToolkit().getScreenSize());
        setUndecorated(true);
        setOpacity(Float.parseFloat(Main.getProc("frame", "opacity")));
        super.toBack();
    }

    private void display () {
        super.setVisible(true);
    }
}
