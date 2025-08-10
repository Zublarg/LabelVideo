package fr.tibo.bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import fr.tibo.util.Genres;
import fr.tibo.util.Util;

public class Film {
	private static final DateTimeFormatter un_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter re_formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);
	private String id;
	private String title_fr;
	private String original_title;
	private String overview;
	private String poster_path;
	private String release_date;
	private String note;
	private String genres;
	private String classification;
	private String duree;

	public Film(JsonNode jsonNode) {
		id = jsonNode.get("id").asText();
		System.out.println(id);
		title_fr = jsonNode.get("title").asText();
		original_title = jsonNode.get("original_title").asText();
		overview = jsonNode.get("overview").asText();
		poster_path = jsonNode.get("poster_path").asText();
		note = jsonNode.get("vote_average").asText();
		LocalDate release_date = LocalDate.parse(jsonNode.get("release_date").asText(), un_formatter);
		this.release_date = re_formatter.format(release_date);
		JsonNode genre_ids = jsonNode.get("genre_ids");
		List<String> genres = new ArrayList<>();
		for (JsonNode genreNode : genre_ids) {
			int id = genreNode.asInt();
			genres.add(Genres.get(id));
		}
		this.genres = genres.stream().collect(Collectors.joining(", "));
		classification = createClassification(id);
		duree = createRuntime(id);
	}

	public String getId() {
		if (id.isBlank())
			return "??";
		return id;
	}

	public String getTitle_fr() {
		if (title_fr.isBlank())
			return "??";
		return title_fr;
	}

	public String getOriginal_title() {
		if (original_title.isBlank())
			return "??";
		return original_title;
	}

	public String getOverview() {
		if (overview.isBlank())
			return "??";
		return overview;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public String getRelease_date() {
		if (release_date.isBlank())
			return "??";
		return release_date;
	}

	public String getNote() {
		if (note.isBlank())
			return "??";
		return note;
	}

	public String getDuree() {
		if (duree.isBlank())
			return "??";
		return duree;
	}

	public String getGenres() {
		if (genres.isBlank())
			return "??";
		return genres;
	}

	public String getClassification() {
		if (classification.isBlank())
			return "??";
		return classification;
	}

	private static final String createRuntime(String id) {
		JsonNode fullData = Util.getData("3/movie/" + id + "?append_to_response=runtime&language=fr-FR");
		return fullData.get("runtime").asText();
	}

	private static final String createClassification(String id) {
		JsonNode fullData = Util.getData("3/movie/" + id + "/release_dates");
		String certification1 = "";
		String certification2 = "";
		String certification3 = "";

		JsonNode results = fullData.get("results");
		for (JsonNode releaseNode : results) {
			String code_pays = releaseNode.get("iso_3166_1").asText();
			String certification = releaseNode.get("release_dates").get(0).get("certification").asText();

			switch (certification) {
			case "G":
			case "PG":
			case "TP":
			case "U":
				certification = "Tous publics";
				break;
			case "PG13":
			case "PG-13":
				certification = "Accompagné d'un adulte (~13)";
				break;
			case "R":
				certification = "Public averti (~16)";
				break;
			}

			switch (code_pays) {
			case "FR":
				certification2 = certification;
				break;
			case "CZ":
				certification1 = certification;
				break;
			case "GB":
				certification3 = certification;
				break;
			}
		}

		Logger.info("Classification Pays FR : {}", certification2);
		Logger.info("Classification Pays CZ : {}", certification1);
		Logger.info("Classification Pays GB : {}", certification3);

		if (certification1.trim().length() > 0)
			return certification1;
		if (certification3.trim().length() > 0)
			return certification3;
		if (certification2.trim().length() > 0)
			return certification2;
		return "";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTitle_fr());
		sb.append(" // ");
		sb.append(getRelease_date());
		sb.append(" // ");
		sb.append(getGenres());
		return sb.toString();
	}
}
