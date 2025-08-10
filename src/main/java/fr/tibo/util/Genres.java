package fr.tibo.util;

import static fr.tibo.util.Util.getData;

import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class Genres {

	private static final String BASE_QUERY = "3/genre/movie/list?language=fr";
	private static final Map<Integer, String> genreNameByID = new HashMap<>();

	public static final void load() {
		if (genreNameByID.keySet().size() == 0) {
			JsonNode allGenres = getData(BASE_QUERY);
			if (allGenres == null) {
				Logger.error("Unable to load all genres");
				return;
			}

			JsonNode genresArray = allGenres.get("genres");

			for (JsonNode genreNode : genresArray) {
				int id = genreNode.get("id").asInt();
				String name = genreNode.get("name").asText();
				genreNameByID.put(id, name);
			}
		}
	}

	public static final String get(int id) {
		return genreNameByID.get(id);
	}
}
