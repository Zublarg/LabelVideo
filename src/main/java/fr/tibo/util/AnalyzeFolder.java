package fr.tibo.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import fr.tibo.bean.Film;
import fr.tibo.data.PropertiesWriter;

public class AnalyzeFolder {

	private static Pattern patternFullDate = Pattern.compile("\\d{4} \\d{2} \\d{2}");
	private static Pattern patternYear = Pattern.compile("\\d{4}");

	public static void analyze(File folder) {
		recur(folder);
	}

	private static final void recur(File f) {
		if (f == null) {
			return;
		} else if (f.isDirectory()) {
			if (f.getAbsolutePath().toLowerCase().contains("system")) {
				Logger.info("System Directory {}", f.getAbsolutePath());
				return;
			}
			// Not a system folder
			for (File subFile : f.listFiles())
				recur(subFile);
		} else if (f.isFile()) {
			Logger.info("---- ---- ---- ----");
			String filepath = f.getParent();
			String filename = f.getName().toLowerCase();
			Logger.info("Working on -> {}", filename);
			String filmYear;
			String filmName;
			int totalSize = filename.length();
			String fileExtension = filename.substring(totalSize - 3);
			String filmExtension = retrieve(fileExtension);
			// Is it a film
			if (filmExtension != null) {
				// Test is a jacket already exist
				File pngFile = new File(f.getAbsolutePath().toLowerCase().replace("." + filmExtension, ".png"));
				if (pngFile.exists()) {
					// Already worked, nothing more to do here
					Logger.info("{} already exists", pngFile.getAbsolutePath());
					return;
				} else {
					filename = filename.replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('_', ' ')
							.replace('-', ' ').trim();

					Matcher matcherFull = patternFullDate.matcher(filename);
					if (matcherFull.find()) {
						filmYear = matcherFull.group().replace(' ', '-');
						filmName = filename.substring(0, matcherFull.start()).trim();
					} else {

						Matcher matcher = patternYear.matcher(filename);

						// if found year
						// Be careful at 1080p or 2160p, we take only the first group
						if (matcher.find()) {
							// Clean file name
							filmYear = matcher.group();
							filmName = filename.substring(0, matcher.start()).trim();
						} else {
							Logger.error("{} but malformed : {}", filmExtension, f.getAbsolutePath());
							return;
						}
					}
				}
			} else if (fileExtension.equals("png")) {
				return;
			} else if (fileExtension.equals("nfo")) {
				return;
			} else {
				Logger.warn("Not a film, not a fiche, not a folder : {}", f.getAbsolutePath());
				return;
			}

			Logger.info("Extension : {}", fileExtension);
			Logger.info("Year : {}", filmYear);
			Logger.info("Name : {}", filmName);

			Film myFilm = Films.getFilm(filmName, filmYear);
			if (myFilm == null) {
				// Not a film
				return;
			}
			myFilm.setFileExtension(filmExtension);
			FileInformationRetriever.enhanceFilmData(f, myFilm);
			File ficheFile = FilmFicheGeneratorLite.drawFiche(filepath, myFilm);
			// Save data
			PropertiesWriter.writeFilmData(filepath, myFilm);
			f.renameTo(new File(ficheFile.getAbsolutePath().replace(".png", "." + filmExtension)));
			Logger.info("Saving done");
			// Make a tempo
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		} else {
			Logger.error("{} -> Not a good file", f.getAbsolutePath());
		}

	}

	private static String retrieve(String fileExtension) {
		switch (fileExtension) {
		case "mkv":
			return "mkv";
		case "mp4":
			return "mp4";
		case "avi":
			return "avi";
		default:
			return null;
		}
	}
}
