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
		int selectedMovie = 0;
		if (rootNode != null) {
			if (rootNode.get("total_results").asInt() == 0) {
				Logger.warn("No film found for {}", film);
				return null;
			} else if (rootNode.get("total_results").asInt() > 1) {
				Logger.warn("More than one film found for {}", film);
				JsonNode results = rootNode.get("results");
				for (JsonNode n : results) {
					Logger.warn(" - {} // {}", n.get("title").asText(), n.get("release_date").asText());
				}
				if (!results.get(0).get("release_date").asText().startsWith(year)) {
					for (int i = 1; i < results.size(); i++)
						if (results.get(i).get("release_date").asText().startsWith(year)) {
							selectedMovie = i;
							break;
						}
				}
			}
		}

		// Only first film
		JsonNode movieJSonNode = rootNode.get("results").get(selectedMovie);
		Logger.info("Choosen movie : {} // {}", movieJSonNode.get("title").asText(),
				movieJSonNode.get("release_date").asText());
		Film ffilm = new Film(movieJSonNode);

		upgradeData(ffilm);
		return ffilm;
	}

	public static void upgradeData(Film film) {
		film.setDuree(retrieveDuration(film.getId()));
		Classification choosenClassification = createClassification(film.getId());
		Logger.info("Choosen Classification => {}", choosenClassification);
		film.setClassification(choosenClassification);
	}

	private static final String retrieveDuration(String id) {
		JsonNode fullData = Util.getData("3/movie/" + id + "?append_to_response=runtime&language=fr-FR");
		int timeMinutes = fullData.get("runtime").asInt();
		long durationHour = (long) (timeMinutes / 60.0);
		long durationMinutes = timeMinutes - 60 * durationHour;
		String duration = String.format("%dh%02d", durationHour, durationMinutes);
		return duration;
	}

	private static final Classification createClassification(String id) {
		JsonNode fullData = Util.getData("3/movie/" + id + "/release_dates");
		Classification classifications[] = new Classification[8];

		JsonNode results = fullData.get("results");
		for (JsonNode releaseNode : results) {
			String code_pays = releaseNode.get("iso_3166_1").asText();
			String certification = releaseNode.get("release_dates").get(0).get("certification").asText();
			Classification classification = null;

			switch (certification) {
			case "T":
			case "G":
			case "PG":
			case "TP":
			case "U":
			case "A":
			case "APTA":
				classification = Classification.TOUS_PUBLICS;
				break;
			case "6":
			case "7":
			case "7+":
			case "7i":
			case "K-7":
				classification = Classification.DECONSEILLE_7_ANS;
				break;
			case "N-13":
			case "PG13":
			case "PG-13":
			case "10":
			case "11":
			case "12":
			case "13":
			case "14":
			case "12+":
			case "12A":
			case "M/12":
			case "K-12":
				classification = Classification.ACCOMPAGNE_12_ANS;
				break;
			case "15":
			case "15+":
			case "16":
			case "16+":
			case "16A":
			case "R":
			case "18":
			case "18+":
			case "M":
			case "M/16":
				classification = Classification.INTERDIT_16_ANS;
				break;
			default:
				classification = Classification.NA;
				break;
			}

			switch (code_pays) {
			case "FR":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[2] = classification;
				break;
			case "CZ":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[1] = classification;
				break;
			case "GB":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[4] = classification;
				break;
			case "UA":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[3] = classification;
				break;
			case "ES":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[0] = classification;
				break;
			case "US":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[5] = classification;
				break;
			case "BE":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[6] = classification;
				break;
			case "CH":
				Logger.info("Classification Pays {} : {}", code_pays, classification);
				classifications[7] = classification;
				break;
			default:
				// Logger.info("{} - {}", code_pays, certification);
				break;
			}
		}

		Integer[] count = { 0, 0, 0, 0 };
		int total = 0;

		// count only not empty data
		for (Classification c : classifications) {
			if (c != null && c != Classification.NA) {
				total++;
				switch (c) {
				case TOUS_PUBLICS:
					count[0]++;
					break;
				case DECONSEILLE_7_ANS:
					count[1]++;
					break;
				case ACCOMPAGNE_12_ANS:
					count[2]++;
					break;
				case INTERDIT_16_ANS:
					count[3]++;
					break;
				}
			}
		}

		// If one is more present than the other
		for (int i = 0; i < count.length; i++) {
			if (count[i] >= total / 2.0d) {
				switch (i) {
				case 0:
					return Classification.TOUS_PUBLICS;
				case 1:
					return Classification.DECONSEILLE_7_ANS;
				case 2:
					return Classification.ACCOMPAGNE_12_ANS;
				case 3:
					return Classification.INTERDIT_16_ANS;
				}
			}
		}

		for (Classification c : classifications) {
			if (c != null && c != Classification.NA)
				return c;
		}

		// If we have realy no data..
		return Classification.NA;
	}

}
