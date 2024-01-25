package corepresence.java.window;

import corepresence.java.FrameIconList;
import corepresence.java.Main;
import net.arikia.dev.drpc.DiscordRPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class GUI {
	public static final String appname = "Omega Strikers CorePresence";

	private static final JFrame mainWindow = new JFrame(appname);
	private static final JPanel pane = new JPanel();

	private static final JLabel banner = new JLabel(new ImageIcon(Main.class.getClassLoader().getResource("assets/banner.png")));
	private static final JLabel instructions = new JLabel("First run? Set your Discord game status to: '" + mainWindow.getTitle() + "'", SwingConstants.CENTER);
	private static final JLabel ver = new JLabel("Version: " + Main.version, SwingConstants.CENTER);
	private static final JButton update = new JButton("Update");
	private static final JButton copyConsole = new JButton("Copy Logs");
//	private static final JButton showConsole = new JButton("Version " + Main.version);

	public static void setupWindow() {
		pane.setLayout(null);
		mainWindow.setContentPane(pane);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.getContentPane().setBackground(new Color(0x2C2F33));
		setWindowIcon(mainWindow);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Closing Discord hook.");
			DiscordRPC.discordShutdown();
		}));
		mainWindow.setVisible(true);
		mainWindow.setResizable(false);

		banner.setBounds(2, 0, 548, 90);
		pane.add(banner);
		instructions.setFont(instructions.getFont().deriveFont(Font.BOLD, 14f));
		instructions.setBounds(2, 65, 548, 65);
		pane.add(instructions);
		ver.setBounds(2, 80, 548, 80);
		pane.add(ver);

		pane.add(update);
		update.setBounds(225, 140, 275, 140);
		update.setSize(100, 30);
		update.setEnabled(false);
		update.setToolTipText("Checking for updates...");

		mainWindow.setSize(550, 220);
	}

	public static void setUpdateStatus(String tooltip) {
		update.setToolTipText(tooltip);
	}

	public static void setUpdateVersion(String version) {
		update.setEnabled(true);
		String url = "https://github.com/Roadhog360/CorePresence/releases/download/" + version + "/CorePresence_" + version + ".jar";
		ActionListener actionListener = event -> {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
					copyToClipboard(url);
				}
			} else {
				copyToClipboard(url);
			}
		};
		update.addActionListener(actionListener);
	}

	private static void copyToClipboard(String URL) {
		StringSelection stringSelection = new StringSelection(URL);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		update.setText("Copied");
		update.setToolTipText("Could not open URL. Copied to clipboard instead");
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		update.setText("Update");
	}

	private static void setWindowIcon(JFrame frame) {
		ArrayList<BufferedImage> images = new ArrayList<>();
		Vector<ImageIcon> icons = new Vector<>();
		BufferedImage image = null;

		URL resource = frame.getClass().getResource("/assets/logo.png");
		if (resource != null) {
			try {
				image = ImageIO.read(resource);
			} catch (IOException e) {
				e.printStackTrace();
			}

			int s = 64;
			final int[] sizes = new int[s];

			for (int ii=0; ii<sizes.length; ii++) {
				sizes[ii] = 16+(ii*2);
			}

			for (int ii=0; ii< sizes.length; ii++) {
				BufferedImage bi = FrameIconList.getImage(
						sizes[ii], image);
				images.add(bi);
				ImageIcon imi = new ImageIcon(bi);
				icons.add(imi);
			}

			frame.setIconImages(images);
		} else {
			System.err.println("Couldn't find 'logo.png', falling back to default Java logo");
		}
	}
}
