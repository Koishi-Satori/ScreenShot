package top.kkoishi.screen;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import top.kkoishi.hook.HookManager;
import top.kkoishi.io.Files;
import top.kkoishi.mail.util.MessageBodyManager;
import top.kkoishi.mail.util.MessageBuilder;
import top.kkoishi.proc.ini.INIPropertiesLoader;
import top.kkoishi.proc.ini.Section;
import top.kkoishi.proc.property.BuildFailedException;
import top.kkoishi.proc.property.LoaderException;
import top.kkoishi.proc.property.TokenizeException;
import top.kkoishi.ser.lang.BadSerializationFormatException;
import top.kkoishi.ser.lang.Builder;
import top.kkoishi.ser.lang.ClassRegisterException;
import top.kkoishi.ser.lang.IllegalSerializationTypeException;
import top.kkoishi.util.KoishiLogger;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.swing.*;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * @author KKoishi_
 */
public class Main {

    private static final int CLOSE_FRAME = 0;

    private static final int SHOW_FRAME = 1;

    private static final int SCREEN_SHOT = 2;

    private static final int SETTINGS = 3;

    private static final String USER_HOME = System.getProperty("user.home");

    private static String passCode = null;

    private static final INIPropertiesLoader LOADER = new INIPropertiesLoader();

    public static Robot robot;

    public static String imageDir = USER_HOME + "/Pictures/";

    private static final Session SESSION = MessageBuilder.getSession(true, "smtp", "smtp.qq.com");

    static {
        InitialDispaly.getInstance().setFocusable(true);
        //use KKoishi Logger.
        KoishiLogger.errFile = "./error.log";
        KoishiLogger.outFile = "./output.log";
        KoishiLogger.toFile = true;
        final var out = KoishiLogger.getInstance(System.out);
        final var err = KoishiLogger.getInstance(System.err);
        err.useErr = true;
        System.setErr(err);
        System.setOut(out);

        SESSION.setDebug(false);
        try {
            passCode = getString(Builder.arraySupport(Character.class).buildArray(Files.readRaw("./data/assignmentCode.kkoishi")));
            LOADER.loadUtf(new File("./data/proc.ini"));
        } catch (BadSerializationFormatException | IllegalSerializationTypeException | ClassRegisterException |
                NoSuchFieldException | IllegalAccessException | InstantiationException | IOException | TokenizeException
                | BuildFailedException | LoaderException e) {
            e.printStackTrace();
            System.exit(114514);
        }
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(114514);
        }
    }

    public static void main (String[] args) {
        final File dir = new File(System.getProperty("user.home") + getProc("files", "file_store_path") + '/');
        if (dir.exists()) {
            imageDir = dir.getAbsolutePath();
            System.out.println("Redirect out to:" + imageDir);
        } else {
            System.out.println("Attempt to create target directory:" + dir);
            if (dir.mkdirs()) {
                imageDir = dir.getAbsolutePath();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to initial target directory:" + dir);
                System.exit(514);
            }
        }
        HookManager.register(CLOSE_FRAME, JIntellitype.MOD_SHIFT + JIntellitype.MOD_CONTROL, KeyEvent.VK_HOME);
        HookManager.register(SHOW_FRAME, JIntellitype.MOD_ALT + JIntellitype.MOD_CONTROL, KeyEvent.VK_P);
        HookManager.register(SCREEN_SHOT, JIntellitype.MOD_CONTROL + JIntellitype.MOD_SHIFT, KeyEvent.VK_O);
        HookManager.register(SETTINGS, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT + JIntellitype.MOD_SHIFT, KeyEvent.VK_U);
        final HookScreenListener listener = new HookScreenListener();
        HookManager.addListener(listener);
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InitialDispaly.clearDisplay();
//        final Serializer serializer = new Serializer("sjvbvfuidwmojhjb".toCharArray());
//        final var bits = serializer.serialize().buildAndClose();
//        final FileOutputStream fos = new FileOutputStream("./data/assignmentCode.kkoishi");
//        fos.write(bits);
//        fos.close();
//        System.out.println(bits);
//        final var builder = Builder.arraySupport(Character.class);
//        System.out.println(Arrays.toString(builder.buildArray(bits)));
    }

    private static final class HookScreenListener implements HotkeyListener {

        private final ScreenFrame f = new ScreenFrame();

        public HookScreenListener () {
        }

        @Override
        public void onHotKey (int i) {
            switch (i) {
                case CLOSE_FRAME:
                    f.reset();
                    break;
                case SHOW_FRAME:
                    f.setVisible(true);
                    break;
                case SCREEN_SHOT:
                    captureScreen(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    break;
                case SETTINGS:
                    final SettingFrame sf = new SettingFrame();
                    sf.setVisible(true);
                    break;
                default:
                    uoe();
                    break;
            }
        }
    }

    static void captureScreen (Rectangle size) {
        System.out.println("Start capture screen.");
        final var img = robot.createScreenCapture(size);
        try {
            final File imgFile = new File(imageDir + "/" +
                    getProc("files", "file_name_head")
                    + System.currentTimeMillis() +
                    getProc("files", "file_name_end")
                    + "." + getProc("files", "file_store_type"));
            System.out.println("State:" + ImageIO.write(img, getProc("files", "file_store_type"), imgFile));
            sendImage(imgFile);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        } finally {
            System.gc();
            System.runFinalization();
        }
    }

    private static void uoe () {
        throw new UnsupportedOperationException();
    }

    private static void sendImage (File src) throws MessagingException, IOException {
        try (Transport transport = SESSION.getTransport()) {
            final String from = getProc("mail", "provider");
            transport.connect(from, passCode);
            final Message msg = MessageBuilder.builder(SESSION, "image")
                    .append(Message.RecipientType.TO, getProc("mail", "receiver"))
                    .setRoot(MessageBodyManager.mix(MessageBodyManager.attachment(src),
                            getPlainText(getProc("mail", "content"))))
                    .setSubject(getProc("mail", "subject")).build();
            msg.setFrom(new InternetAddress(from));
            transport.sendMessage(msg, msg.getAllRecipients());
        }
    }

    private static BodyPart getPlainText (String content) throws MessagingException {
        final BodyPart part = new MimeBodyPart();
        part.setText(content);
        return part;
    }

    public static String getProc (String sectionName, String key) {
        final Section section = LOADER.get(sectionName);
        for (Section.INIEntry entry : section.entries) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        throw new NoSuchElementException();
    }

    private static String getString (Character[] cs) {
        final StringBuilder sb = new StringBuilder();
        for (final char c : cs) {
            sb.append(c);
        }
        return sb.toString();
    }
}
