package fr.tibo;

import org.tinylog.Logger;

import fr.tibo.bean.Film;
import fr.tibo.util.Films;
import fr.tibo.util.Genres;

public class LabelVideo {

	public static void main(String[] args) {
		Logger.info("--- --- --- Starting LabelVideo --- --- ---");

		Genres.load();

		Film f = Films.getFilm("Romeo Must Die", "2000");
		System.out.println(f);

		Logger.info("--- --- --- Ending LabelVideo --- --- ---");
	}

}
