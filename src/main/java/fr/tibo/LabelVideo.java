package fr.tibo;

import java.io.File;
import java.time.LocalDate;

import org.tinylog.Logger;

import fr.tibo.util.AnalyzeFolder;
import fr.tibo.util.Genres;

public class LabelVideo {

	public static void main(String[] args) {
		Logger.info("--- --- --- Starting LabelVideo {} --- ---", LocalDate.now().toString());

		Genres.load();

		File folder = new File("P:\\Videos\\A trier");
		// Films.getFilm("antigang", "2015");
		// File folder = new File("P:\\Videos\\FILM RANGE\\Pirates Des Caraibes");

		// File propertieFile = new File(folder, "Pokémon - Pikachu quelle est cette clé
		// ! 2014.properties");
		// Film film = PropertiesLoader.readFilmData(propertieFile);
		// FilmFicheGeneratorLite.drawFiche(folder.getAbsolutePath(), film);

		AnalyzeFolder.analyze(folder);

		Logger.info("--- --- --- Ending LabelVideo --- --- --- ---");
	}

}
