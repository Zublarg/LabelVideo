package fr.tibo;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatLightLaf;

import fr.tibo.ihm.MainIHM;

public class FilmFinder {

	public static void main(String[] args) {
		Logger.info("--- --- --- Starting Video Finder --- --- ---");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame.setDefaultLookAndFeelDecorated(true);
				try {
					UIManager.setLookAndFeel(new FlatLightLaf());
				} catch (Exception ex) {
					Logger.error(ex, "Failed to initialize LaF");
				}

				String path = ".";
				if (args.length > 0)
					path = args[0];

				MainIHM mi = new MainIHM(path);

				mi.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						Logger.info("--- --- --- Ending Video Finder --- --- ---");
					}
				});

				mi.setVisible(true);
			}
		});

	}

}
