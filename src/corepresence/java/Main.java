package corepresence.java;

import corepresence.java.logreader.LogManager;
import corepresence.java.managers.GameStateManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class Main {

	private static final String version = "beta-1";
	private static final String appname = "Omega Strikers CorePresence";

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		setupWindow();

		System.out.println("Starting " + appname + " (" + version + ")");
		LogManager.init();
		GameStateManager.updateTime();
		initDiscord();

		System.out.println("Running callbacks...");
		DiscordRPC.discordRunCallbacks();

		LogManager.postInit();
	}

	private static void setupWindow() {
		JFrame frame = new JFrame(appname);
		JPanel pane = new JPanel();
		pane.setLayout(null);
		frame.setContentPane(pane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(0x2C2F33));
		setWindowIcon(frame);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Closing Discord hook.");
			DiscordRPC.discordShutdown();
		}));
		frame.setVisible(true);
		frame.setResizable(false);

		JLabel banner = new JLabel(new ImageIcon(Main.class.getClassLoader().getResource("assets/banner.png")));
		banner.setBounds(2, 0, 548, 90);
		pane.add(banner);
		JLabel instructions = new JLabel("First run? Set your Discord game status to: '" + frame.getTitle() + "'", SwingConstants.CENTER);
		instructions.setFont(instructions.getFont().deriveFont(Font.BOLD, 14f));
		instructions.setBounds(2, 65, 548, 65);
		pane.add(instructions);
		JLabel ver = new JLabel("Version " + version, SwingConstants.CENTER);
		ver.setBounds(2, 80, 548, 80);
		pane.add(ver);

		frame.setSize(550, 190);
	}

	//https://github.com/Roadhog360/CorePresence/releases/download/<version>/CorePresence_<version>.jar

	static void initDiscord() {
		DiscordEventHandlers handler = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			StringBuilder initMsg = new StringBuilder("Connecting to Discord user: " + user.username);
			if(user.discriminator.length() == 4) {
				initMsg.append("#").append(user.discriminator);
			}
			initMsg.append(".");
			System.out.println(initMsg);
		}).build();
		DiscordRPC.discordInitialize("1197750595042418768", handler, false);
		DiscordRPC.discordRegister("1197750595042418768", "");
	}

	private static void setWindowIcon(JFrame frame) {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		Vector<ImageIcon> icons = new Vector<ImageIcon>();
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