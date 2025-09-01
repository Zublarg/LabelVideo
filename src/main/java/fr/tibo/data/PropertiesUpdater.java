package fr.tibo.data;

import java.io.File;
import java.io.FileFilter;

import org.tinylog.Logger;

import fr.tibo.bean.Film;

public class PropertiesUpdater {

	private static final FileName filenameFilter = new FileName();

	private static class FileName implements FileFilter {

		@Override
		public boolean accept(File f) {

			return f.isDirectory() || f.getName().toLowerCase().endsWith("properties");
		}

	}

	public static void main(String[] args) {
		Logger.info("--- --- --- Starting PropertiesUpdater --- --- ---");

		File folder = new File("P:\\Videos\\FILMS");
		for (File f : folder.listFiles(filenameFilter))
			// if (f.isDirectory())
			recur(f);

		Logger.info("--- --- --- Ending PropertiesUpdater --- --- ---");
	}

	private static final void recur(File f) {
		if (f == null) {
			return;
		} else if (f.isDirectory()) {
			if (f.getAbsolutePath().toLowerCase().contains("system")) {
				Logger.info("System Directory {}", f.getAbsolutePath());
				return;
			}
			Logger.info("==> Work on folder {}", f.getName());
			// Not a system folder
			for (File subFile : f.listFiles(filenameFilter))
				recur(subFile);
		} else if (f.isFile()) {
			Film film = PropertiesLoader.readFilmData(f);
			update(f, film);
		}
	}

	private static void update(File f, Film film) {
		// PropertiesWriter.writeFilmData(f.getParent(), film);
		// FilmFicheGeneratorLite.drawFiche(f.getParent(), film);
	}
}
