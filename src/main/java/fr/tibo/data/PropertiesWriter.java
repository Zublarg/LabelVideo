package fr.tibo.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.tinylog.Logger;

import fr.tibo.bean.Film;
import fr.tibo.util.Util;

public class PropertiesWriter {

	public static void writeFilmData(String path, Film f) {

		Properties props = new Properties();

		String filename = Util.getEscapedName(f.getTitle_fr());
		File outputFile = new File(path, filename + " " + f.getRelease_year() + ".properties");

		props.setProperty("title", f.getTitle_fr());
		props.setProperty("originaltitle", f.getOriginal_title());
		props.setProperty("userrating", f.getNote());
		props.setProperty("plot", f.getOverview());
		props.setProperty("mpaa", f.getClassification().name());
		props.setProperty("uniqueid", f.getId());
		props.setProperty("genre", f.getGenres());
		props.setProperty("saga", f.getSaga());
		props.setProperty("sagaorder", Integer.toString(f.getSagaOrder()));
		props.setProperty("premiered", f.getJson_release_date());
		props.setProperty("duration", f.getDuree());
		props.setProperty("realduration", f.getRealDuree());
		props.setProperty("language", f.getAudioAndSubs());
		props.setProperty("poster", f.getPoster_path());
		props.setProperty("extension", f.getFileExtension());

		try {
			try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile),
					StandardCharsets.UTF_8)) {
				props.store(writer, "Fichier en UTF-8");
			}
		} catch (IOException e) {
			Logger.error(e, "Unable to save Film data from {}", outputFile.getAbsolutePath());
		}
	}
}
