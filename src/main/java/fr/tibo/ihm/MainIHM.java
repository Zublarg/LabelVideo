package fr.tibo.ihm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.tinylog.Logger;

import fr.tibo.bean.Film;
import fr.tibo.data.PropertiesLoader;
import net.miginfocom.swing.MigLayout;

public class MainIHM extends JFrame {

	private static final long serialVersionUID = 4650857383033626616L;
	private static List<Film> fullData = new ArrayList<>();

	public MainIHM(String path) {
		super("Video Finder");
		try {
			setIconImage(ImageIO.read(MainIHM.class.getResource("/icon.png")));
		} catch (Exception eee) {
			Logger.error(eee, "Unable to read Icon Image");
		}

		Logger.info("Load Data");
		loadFullData(new File(path));
		Logger.info("{} movies loaded", fullData.size());

		// operation par defaut
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Met la taille
		setSize(800, 600);
		// On interdit de changer la taille
		setResizable(false);
		// Met au milieu de la fenêtre
		setLocationRelativeTo(null);

		createContent();

	}

	private void loadFullData(File current) {
		if (current.isDirectory()) {
			for (File f : current.listFiles(f -> f.isDirectory() || f.getName().endsWith(".properties"))) {
				loadFullData(f);
			}
		} else if (current.isFile()) {
			Film film = PropertiesLoader.readFilmData(current);
			if (film != null) {
				fullData.add(film);
			}
		} else {
			Logger.warn("What is : {}", current.getAbsolutePath());
		}
	}

	private void createContent() {
		JPanel panel = new JPanel(new MigLayout());
		this.setContentPane(panel);

	}

}
