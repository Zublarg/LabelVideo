package fr.tibo.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.tinylog.Logger;

import fr.tibo.bean.Film;
import fr.tibo.util.Classification;

public class PropertiesLoader {

	public static final Film readFilmData(File propertieFile) {
		Properties props = new Properties();

		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(propertieFile), "UTF-8")) {
			props.load(reader);

			String title_fr = props.getProperty("title");
			String original_title = props.getProperty("originaltitle");
			String note = props.getProperty("userrating");
			String overview = props.getProperty("plot");
			String mpaa = props.getProperty("mpaa");
			Classification classification = Classification.valueOf(mpaa);
			String id = props.getProperty("uniqueid");
			String genres = props.getProperty("genre");
			String saga = props.getProperty("saga");
			int sagaOrder = Integer.parseInt(props.getProperty("sagaorder", "0"));
			String json_release_date = props.getProperty("premiered");
			String duree = props.getProperty("duration");
			String realduree = props.getProperty("realduration");
			String audioAndSubs = props.getProperty("language");
			String poster_path = props.getProperty("poster");
			String extension = props.getProperty("extension");

			Film f = new Film(id, title_fr, original_title, overview, poster_path, json_release_date, note, genres,
					saga, sagaOrder, classification, duree, realduree, audioAndSubs, extension);
			return f;

		} catch (IOException e) {
			Logger.error(e, "Unable to create Film data from {}", propertieFile.getAbsolutePath());
		}
		return null;
	}
}
