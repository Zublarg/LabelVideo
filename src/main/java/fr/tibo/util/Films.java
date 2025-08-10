package fr.tibo.util;

import static fr.tibo.util.Util.getData;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import fr.tibo.bean.Film;

public class Films {

	private static final String BASE_QUERY = "3/search/movie?query=";

	public static Film getFilm(String film, String year) {
		String encodedQuery = URLEncoder.encode(film, StandardCharsets.UTF_8);
		String fullquery = BASE_QUERY + encodedQuery + "&language=fr&year=" + year;
		JsonNode rootNode = getData(fullquery);
		if (rootNode != null) {
			if (rootNode.get("total_results").asInt() == 0) {
				Logger.warn("No film found for {}", film);
				return null;
			} else if (rootNode.get("total_results").asInt() > 1) {
				Logger.warn("More than one film found for {}", film);
			}
		}
		// Only first film
		return new Film(rootNode.get("results").get(0));
	}
}
