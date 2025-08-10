package fr.tibo.bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

	public Film(JsonNode jsonNode) {
		id = jsonNode.get("id").asText();
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

		Util.dlImage(poster_path);
	}

	public String getId() {
		return id;
	}

	public String getTitle_fr() {
		return title_fr;
	}

	public String getOriginal_title() {
		return original_title;
	}

	public String getOverview() {
		return overview;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public String getRelease_date() {
		return release_date;
	}

	public String getNote() {
		return note;
	}

	public String getGenres() {
		return genres;
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
