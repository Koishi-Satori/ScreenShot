package top.kkoishi.screen;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

/**
 * @author KKoishi_
 */
public final class ScreenFrame extends Frame implements MouseInputListener, KeyListener {

    private Point p1;

    private Point p2;

    private final Rectangle rectangle = new Rectangle(getToolkit().getScreenSize());

    public ScreenFrame () throws HeadlessException {
        setBackground(new Color(0, 0, 0));
        setSize(rectangle.getSize());
        p1 = new Point(0, 0);
        p2 = new Point(getWidth(), getHeight());
        super.addMouseMotionListener(this);
        super.addMouseListener(this);
        super.addKeyListener(this);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setOpacity(Float.parseFloat(Main.getProc("frame", "opacity")));
        setLayout(new BorderLayout());
        super.toBack();
    }

    public void display () {
        super.setVisible(true);
        super.toFront();
    }

    @Override
    public void mouseDragged (MouseEvent e) {
        super.getGraphics().clearRect(0, 0, getWidth(), getHeight());
        p2 = getMousePosition();
        rectangle.width = StrictMath.abs(p2.x - p1.x);
        rectangle.height = StrictMath.abs(p2.y - p1.y);
        drawRectOnScreen();
    }

    @Override
    public void mouseMoved (MouseEvent e) {
    }

    @Override
    public void mouseClicked (MouseEvent e) {
    }

    @Override
    public void mousePressed (MouseEvent e) {
        super.getGraphics().clearRect(0, 0, getWidth(), getHeight());
        p1 = getMousePosition();
        rectangle.setLocation(p1);
        drawRectOnScreen();
    }

    @Override
    public void mouseReleased (MouseEvent e) {
        p1 = p2 = new Point(0, 0);
    }

    @Override
    public void mouseEntered (MouseEvent e) {
    }

    @Override
    public void mouseExited (MouseEvent e) {
    }

    @Override
    public void paint (Graphics g) {
        super.paint(g);
    }

    public void drawRectOnScreen () {
        final var g = super.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    @Override
    public void keyTyped (KeyEvent e) {
    }

    void reset () {
        super.dispose();
        super.toBack();
        p1 = p2 = new Point(0, 0);
        rectangle.setRect(0, 0, 0, 0);
        super.setExtendedState(JFrame.NORMAL);
    }

    @Override
    public void keyPressed (KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: {
                if (rectangle.height > 0 && rectangle.width > 0) {
                    super.setOpacity(0);
                    Main.captureScreen(rectangle);
                    super.setOpacity(Float.parseFloat(Main.getProc("frame", "opacity")));
                } else {
                    JOptionPane.showMessageDialog(this,
                            "AWT-EventQueue-0->java.lang.IllegalArgumentException:" +
                                    "Rectangle width and height must be > 0, but got:" + rectangle);
                    break;
                }
            }
            case KeyEvent.VK_ESCAPE:{
                reset();
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    public void keyReleased (KeyEvent e) {
    }
}
