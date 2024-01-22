import gamedata.*;
import gamedata.Striker;
import logreader.LogManager;
import managers.GameStateManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new JFrame("Omega Strikers CorePresence");
		JLabel text = new JLabel("Now goto Discord and set your active game to: '" + frame.getTitle() + "'");
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().setBackground(new Color(0x5865F2));
		frame.getContentPane().add(text, SwingConstants.CENTER);

		frame.setResizable(true);
		frame.setSize(550, 100);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setVisible(true);
		frame.setResizable(false);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Closing Discord hook.");
			DiscordRPC.discordShutdown();
		}));

		LogManager.init();
		GameStateManager.updateTime();
		initDiscord();

		System.out.println("Running callbacks...");
		DiscordRPC.discordRunCallbacks();

		LogManager.postInit();
	}

	static void initDiscord() {
		DiscordEventHandlers handler = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			System.out.println("Connecting user " + user.username);
			if(user.discriminator.length() == 4) {
				System.out.print("#" + user.discriminator);
			}
		}).build();
		DiscordRPC.discordInitialize("1197750595042418768", handler, false);
		DiscordRPC.discordRegister("1197750595042418768", "");
	}
}