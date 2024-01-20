import gamedata.*;
import gamedata.Striker;
import logreader.LogManager;
import managers.GameStateManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Scanner;

public class Main {

	private static boolean ready = false;

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		JFrame frame = new JFrame("Omega Strikers CorePresence test");
		JLabel text = new JLabel("Now goto Discord and set your active game to: '" + frame.getTitle() + "'");
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().setBackground(new Color(0x5865F2));
		frame.getContentPane().add(text, SwingConstants.CENTER);

		frame.setResizable(true);
		frame.setSize(550, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		addDebugButtons(frame);

		frame.setVisible(true);
		frame.setResizable(false);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Closing Discord hook.");
			DiscordRPC.discordShutdown();
		}));

		LogManager.init();
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
		ready = true;
		GameStateManager.setInMenus();
		DiscordRPC.discordInitialize("1197750595042418768", handler, false);
		DiscordRPC.discordRegister("1197750595042418768", "");
	}

	private static void addDebugButtons(JFrame frame) {
		JLabel striker = new JLabel("Choose a Striker");
		JLabel location = new JLabel("Choose a Location");
		JLabel status = new JLabel("Choose a Status");
		{
			JPanel panel  = new JPanel();

			frame.add(panel);
			striker.setVisible(true);

			panel.add(striker);

			final JComboBox<Striker> cb = new JComboBox<>(Striker.values());

			cb.setVisible(true);
			cb.addActionListener(e -> {
				GameStateManager.menuCharacter = (Striker) cb.getSelectedItem();
				System.out.println(cb.getSelectedItem());
				GameStateManager.updateStatus();
			});
			panel.add(cb);
		}

		{
			JPanel panel  = new JPanel();

			frame.add(panel);
			location.setVisible(true);

			panel.add(location);

			final JComboBox<Location> cb = new JComboBox<>(Location.values());

			cb.setVisible(true);
			cb.addActionListener(e -> {
				if(Objects.equals(cb.getSelectedItem(), Location.MENUS)) {
					GameStateManager.setInMenus();
				} else {
					GameStateManager.location = (Location) cb.getSelectedItem();
					if(GameStateManager.arena == Arena.MENU) {
						GameStateManager.arena = Arena.AHTEN_CITY;
						GameStateManager.updateTime();
					}
					Scoreboard.INSTANCE.setGameState(GameProgress.IN_GAME);
					System.out.println(cb.getSelectedItem());
					GameStateManager.updateStatus();
				}
			});
			panel.add(cb);
		}

		{
			JPanel panel  = new JPanel();

			frame.add(panel);
			status.setVisible(true);

			panel.add(status);

			final JComboBox<GameProgress> cb = new JComboBox<>(GameProgress.values());

			cb.setVisible(true);
			cb.addActionListener(e -> {
				Scoreboard.INSTANCE.setGameState((GameProgress) cb.getSelectedItem());
				GameStateManager.updateStatus();
			});
			panel.add(cb);
		}
	}
}