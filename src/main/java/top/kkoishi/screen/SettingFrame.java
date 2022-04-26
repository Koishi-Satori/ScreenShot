package top.kkoishi.screen;

import top.kkoishi.swing.JVMStateDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * @author KKoishi_
 */
public class SettingFrame extends Frame {

    private static JButton createBut (String text, Consumer<ActionEvent> action) {
        final JButton but = new JButton(text);
        but.addActionListener(action::accept);
        return but;
    }

    private int state = 0;

    private final JPanel displayFiles = new JPanel(null);

    private final JPanel displayMail = new JPanel(null);

    private final JPanel displayLog = new JPanel(null);

    private final JPanel helpPanel = new JPanel(null);

    private final JButton[] buts = new JButton[4];

    public SettingFrame () throws HeadlessException {
        super("Setting");
        setUndecorated(true);
        setOpacity(0.9f);
        final int width = getToolkit().getScreenSize().width / 2;
        setSize(width / 2, width / 3);
        setResizable(false);
        setLayout(new BorderLayout());
        final JPanel switchBar = new JPanel(new GridLayout(1, 4));
        final String[] titles = {"guidance", "files", "mail", "logs"};
        IntStream.range(0, 4).forEach(i -> buts[i] = createBut(titles[i], e -> updateBut(i, state)));
        Arrays.stream(buts).forEach(switchBar::add);
        add(switchBar, BorderLayout.NORTH);
        switchBar.setSize(width, width / 24);
        initField(width, width * 11 / 24);
        add(helpPanel, BorderLayout.CENTER);
        add(new JVMStateDisplay(), BorderLayout.SOUTH);
    }

    private void initField (int width, int height) {
        final JTextPane help = new JTextPane();
        help.setText("Test");
        helpPanel.add(help);
        add(helpPanel);
        final JPanel fileHead = new JPanel(new BorderLayout());
        fileHead.setBounds(0, 0, width, height / 5);
        fileHead.add(new JLabel("file name head:"), BorderLayout.WEST);
        fileHead.add(new JTextField(), BorderLayout.CENTER);
        displayFiles.add(fileHead);
        add(displayFiles);

    }

    private void updateBut (int but, int oldIndex) {
        buts[but].setEnabled(false);
        buts[state].setEnabled(true);
        state = but;
        switchDisplay();
    }

    void switchDisplay () {
        final var page = getPage(state);
        add(page, BorderLayout.CENTER);
        System.out.println("Switch to:" + page);
        System.out.println("Update comp to:" + Arrays.toString(page.getComponents()));
        super.repaint();
    }

    private JPanel getPage (int index) {
        return switch (index) {
            case 0:
                yield helpPanel;
            case 1:
                yield displayFiles;
            case 2:
                yield displayMail;
            case 3:
                yield displayLog;
            default:
                throw new NoSuchElementException();
        };
    }

    @Override
    public void paint (Graphics g) {
        super.paint(g);
    }
}
