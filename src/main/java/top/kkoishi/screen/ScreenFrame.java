package top.kkoishi.screen;

import top.kkoishi.concurrent.DefaultThreadFactory;
import top.kkoishi.swing.JVMStateDisplay;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author KKoishi_
 */
public final class ScreenFrame extends Frame implements MouseInputListener, KeyListener {

    private Point p1;

    private Point p2;

    private final Rectangle rectangle = new Rectangle(getToolkit().getScreenSize());

    private final JPopupMenu popupMenu = new JPopupMenu();

    public ScreenFrame () throws HeadlessException {
        setTitle("ScreenShotTool_by_KKoishi_");
        final var mem = new JVMStateDisplay();
        final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory());
        pool.scheduleAtFixedRate(mem.get(), 0, 30, TimeUnit.MILLISECONDS);
        popupMenu.add(mem);
        popupMenu.add("Press 'Ctrl+Shift+O' to capture the whole screen.");
        popupMenu.add("Press 'Ctrl+Alt+P' to select capture size.");
        popupMenu.add("Press 'Esc' to exit selection and 'Enter' to capture while selecting.");
        popupMenu.add("Press 'Ctrl+Shift+Home' to close me.");
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
        p2 = e.getPoint();
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
        if (e.getButton() == MouseEvent.BUTTON3) {
            popupMenu.show(this, e.getX(), e.getY());
            return;
        }
        super.getGraphics().clearRect(0, 0, getWidth(), getHeight());
        p1 = getMousePosition();
        p2 = p1;
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
            case KeyEvent.VK_ESCAPE: {
                reset();
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void keyReleased (KeyEvent e) {
    }
}
