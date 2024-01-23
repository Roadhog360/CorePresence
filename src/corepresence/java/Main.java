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

	private static final String version = "alpha-1";

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		String appname = "Omega Strikers CorePresence";
		JFrame frame = new JFrame(appname);

		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().setBackground(new Color(0x5865F2));
		frame.getContentPane().add(new JLabel("Now goto Discord and set your active game to: '" + frame.getTitle() + "'"), SwingConstants.CENTER);
		frame.getContentPane().add(new JLabel("Version " + version));

		setWindowIcon(frame);

		frame.setResizable(true);
		frame.setSize(550, 100);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setVisible(true);
		frame.setResizable(false);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Closing Discord hook.");
			DiscordRPC.discordShutdown();
		}));

		System.out.println("Starting " + appname + " (" + version + ")");
		LogManager.init();
		GameStateManager.updateTime();
		initDiscord();

		System.out.println("Running callbacks...");
		DiscordRPC.discordRunCallbacks();

		LogManager.postInit();
	}

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