package fr.tibo.bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import fr.tibo.util.Classification;
import fr.tibo.util.Genres;

public class Film {
	private static final DateTimeFormatter year_formatter = DateTimeFormatter.ofPattern("yyyy");
	private static final DateTimeFormatter un_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter re_formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);
	private String id;
	private String title_fr;
	private String original_title;
	private String overview;
	private String poster_path;
	private String json_release_date;
	private String release_date;
	private String release_year;
	private String note;
	private String genres;
	private String saga;
	private int sagaOrder = 0;
	private Classification classification = Classification.NA;
	private String duree;
	private String realDuree;
	private String audioAndSubs;
	private String fileExtension;

	public Film(JsonNode jsonNode) {
		id = jsonNode.get("id").asText();
		title_fr = jsonNode.get("title").asText();
		original_title = jsonNode.get("original_title").asText();
		overview = jsonNode.get("overview").asText().replace('"', '\'').replace('\n', ' ').replace('\r', ' ')
				.replaceAll(" {2,}", " ");
		poster_path = jsonNode.get("poster_path").asText();
		note = jsonNode.get("vote_average").asText();
		json_release_date = jsonNode.get("release_date").asText();
		LocalDate full_release_date = LocalDate.parse(json_release_date, un_formatter);
		release_date = re_formatter.format(full_release_date);
		release_year = year_formatter.format(full_release_date);
		saga = "";
		JsonNode genre_ids = jsonNode.get("genre_ids");
		List<String> genres = new ArrayList<>();
		for (JsonNode genreNode : genre_ids) {
			int id = genreNode.asInt();
			genres.add(Genres.get(id));
		}
		this.genres = genres.stream().collect(Collectors.joining(", "));
	}

	public Film(String id, String title_fr, String original_title, String overview, String poster_path,
			String json_release_date, String note, String genres, String saga, int sagaOrder,
			Classification classification, String duree, String realDuree, String audioAndSubs, String fileExtension) {
		this.id = id;
		this.title_fr = title_fr;
		this.original_title = original_title;
		this.overview = overview;
		this.poster_path = poster_path;
		this.json_release_date = json_release_date;
		LocalDate full_release_date = LocalDate.parse(json_release_date, un_formatter);
		release_date = re_formatter.format(full_release_date);
		release_year = year_formatter.format(full_release_date);
		this.note = note;
		this.saga = saga;
		this.sagaOrder = sagaOrder;
		this.genres = genres;
		this.classification = classification;
		this.duree = duree;
		this.realDuree = realDuree;
		this.audioAndSubs = audioAndSubs;
		this.fileExtension = fileExtension;
	}

	public String getId() {
		if (id.isBlank())
			return "-";
		return id;
	}

	public String getTitle_fr() {
		if (title_fr.isBlank())
			return "-";
		return title_fr;
	}

	public String getOriginal_title() {
		if (original_title.isBlank())
			return "-";
		return original_title;
	}

	public String getOverview() {
		if (overview.isBlank())
			return "-";
		return overview;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public String getJson_release_date() {
		return json_release_date;
	}

	public String getRelease_date() {
		if (release_date.isBlank())
			return "-";
		return release_date;
	}

	public String getRelease_year() {
		return release_year;
	}

	public String getNote() {
		if (note.isBlank())
			return "-";
		return note;
	}

	public String getDuree() {
		if (duree.isBlank())
			return "-";
		return duree;
	}

	public String getGenres() {
		if (genres.isBlank())
			return "-";
		return genres;
	}

	public Classification getClassification() {
		return classification;
	}

	public String getRealDuree() {
		if (realDuree == null)
			return "";
		return realDuree;
	}

	public void setRealDuree(String realDuree) {
		this.realDuree = realDuree;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public void setDuree(String duree) {
		this.duree = duree;
	}

	public String getAudioAndSubs() {
		return audioAndSubs;
	}

	public void setAudioAndSubs(String audioAndSubs) {
		this.audioAndSubs = audioAndSubs;
	}

	public void setFileExtension(String filmExtension) {
		this.fileExtension = filmExtension;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getSaga() {
		if (saga == null)
			return "";
		return saga;
	}

	public void setSaga(String saga) {
		this.saga = saga;
	}

	public int getSagaOrder() {
		return sagaOrder;
	}

	public void setSagaOrder(int sagaOrder) {
		this.sagaOrder = sagaOrder;
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
