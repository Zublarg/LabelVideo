package fr.tibo.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tinylog.Logger;

import fr.tibo.bean.Film;
import fr.tibo.data.PropertiesLoader;
import fr.tibo.util.Classification;
import fr.tibo.util.Util;

public class FullJSONWriter {

	private static final String SERVER = "192.168.1.229:8080";

	private List<FilmData> fullData = new ArrayList<>();

	public static void main(String[] args) {
		Logger.info("--- --- --- Starting JSON Writer --- --- --- ---");
		FullJSONWriter fjw = new FullJSONWriter();
		Logger.info("Load Data");
		File movies_folder = new File("P:\\Videos\\FILMS");
		File folderMSX = new File(movies_folder, "MSX");
		// Starting folder is empty
		fjw.loadFullData(movies_folder, "");
		Logger.info("{} movies loaded", fjw.fullData.size());
		List<String> menu = new ArrayList<>();

		Collections.sort(fjw.fullData, (f1, f2) -> f1.f.getTitle_fr().compareTo(f2.f.getTitle_fr()));

		Logger.info("Write full list ...");
		createListFor(fjw.fullData, "full", folderMSX);

		Logger.info("Create Fiche ...");
		// STEP 1 solo Page with full image + play json
		for (FilmData fd : fjw.fullData) {
			createSoloPageForInfo(fd, new File(movies_folder, "JSON"));
		}

		Logger.info("Create Panel ...");
		// STEP 2 Create entries for menu

		menu.add("Famille");
		menu.add(createListFor(fjw.fullData.stream()
				.filter(f -> f.f.getGenres().contains("Action") && !f.f.getGenres().contains("Horreur")
						&& f.f.getClassification().ordinal() < Classification.ACCOMPAGNE_12_ANS.ordinal())
				.toList(), "Action", folderMSX));
		menu.add(createListFor(fjw.fullData.stream()
				.filter(f -> f.f.getGenres().contains("Animation") && !f.f.getGenres().contains("Horreur")
						&& f.f.getClassification().ordinal() < Classification.ACCOMPAGNE_12_ANS.ordinal())
				.toList(), "Animation", folderMSX));

		menu.add("Films");

		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getRelease_year().equals("2024")).toList(),
				"Films 2024", folderMSX));
		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getRelease_year().equals("2025")).toList(),
				"Films 2025", folderMSX));
		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getRelease_year().startsWith("202")).toList(),
				"Films 2020'", folderMSX));
		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getRelease_year().startsWith("201")).toList(),
				"Films 2010'", folderMSX));
		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getTitle_fr().charAt(0) < 'M').toList(),
				"Films A-L", folderMSX));

		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getTitle_fr().charAt(0) > 'L').toList(),
				"Films M-Z", folderMSX));

		menu.add("Recherche|http://192.168.1.229:8080/msx/search.json");

		menu.add("Saga");
		Set<String> sagas = new HashSet<>();
		for (FilmData fd : fjw.fullData) {
			if (!fd.f.getSaga().isEmpty())
				sagas.add(fd.f.getSaga());
		}

		for (String saga : sagas) {
			final List<FilmData> sagaFilm = new ArrayList<>(
					fjw.fullData.stream().filter(fd -> fd.f.getSaga().equals(saga)).toList());
			Collections.sort(sagaFilm, (fd1, fd2) -> fd1.f.getSagaOrder() - fd2.f.getSagaOrder());
			menu.add(createListFor(sagaFilm, saga, folderMSX));
		}

		menu.add("Adulte");
		menu.add(createListFor(fjw.fullData.stream()
				.filter(f -> f.f.getGenres().contains("Comédie")
						&& f.f.getClassification().ordinal() == Classification.INTERDIT_16_ANS.ordinal())
				.toList(), "Comédie", folderMSX));
		menu.add(createListFor(fjw.fullData.stream().filter(f -> f.f.getGenres().contains("Horreur")).toList(),
				"Horreur", folderMSX));
		menu.add(createListFor(fjw.fullData.stream()
				.filter(f -> !f.f.getGenres().contains("Comédie") && !f.f.getGenres().contains("Horreur")
						&& f.f.getClassification().ordinal() == Classification.INTERDIT_16_ANS.ordinal())
				.toList(), "Autre", folderMSX));

		Logger.info("Create Menu ...");

		createMenu(menu, folderMSX);

		Logger.info("--- --- --- Ending JSON Writer --- --- --- ---");

	}

	private static void createMenu(List<String> panels, File folder) {
		File jsonFile = new File(folder, "menu.json");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
			writer.write("{");
			writer.newLine();
			writer.write("\"headline\": \"Tibo Media Center\",");
			writer.newLine();
			writer.write("\"menu\": [");
			writer.newLine();

			Iterator<String> menuEntry = panels.iterator();
			while (menuEntry.hasNext()) {
				String menu = menuEntry.next();
				if (menu == null || menu.isEmpty())
					continue;

				writer.write("{");
				writer.newLine();

				if (menu.contains("|")) {
					String[] values = menu.split("\\|");
					writer.write("\"label\": \"" + values[0] + "\",");
					writer.newLine();
					writer.write("\"data\": \"" + values[1] + "\"");
					writer.newLine();
				} else if (menu.endsWith(".json")) {
					writer.write("\"label\": \"" + menu.replace(".json", "") + "\",");
					writer.newLine();
					writer.write("\"data\": \"http://192.168.1.229:8080/msx/" + menu + "\"");
					writer.newLine();
				} else {
					writer.write("\"type\": \"separator\",");
					writer.newLine();
					writer.write("\"label\": \"" + menu + "\"");
					writer.newLine();
				}

				writer.write("}");
				if (menuEntry.hasNext())
					writer.write(",");
				writer.newLine();
			}

			writer.write("]");
			writer.newLine();
			writer.write("}");
			writer.newLine();
		} catch (IOException e) {
			Logger.error(e, "Error during json writer {}", jsonFile.getAbsolutePath());
		}
	}

	private static void createSoloPageForInfo(FilmData fd, File folder) {
		String file_name = Util.getEscapedName(fd.f.getTitle_fr()) + " " + fd.f.getRelease_year();
		File jsonFile = new File(folder, file_name + ".json");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
			writer.write("{");
			writer.newLine();
			writer.write("\"type\": \"pages\",");
			writer.newLine();
			writer.write("\"headline\": \"" + fd.f.getTitle_fr() + "\",");
			writer.newLine();

			writer.write("\"pages\": [");
			writer.newLine();
			writer.write("{");
			writer.newLine();

			writer.write("\"items\": [");
			writer.newLine();
			writer.write("{");
			writer.newLine();

			writer.write("\"type\": \"default\",");
			writer.newLine();

			// full layout
			writer.write("\"layout\": \"0,0,12,6\",");
			writer.newLine();

			writer.write("\"color\": \"msx-black\",");
			writer.newLine();

			writer.write("\"image\": \"http://" + SERVER + "/");
			if (!fd.folder.isBlank() && !fd.folder.equals("FILMS"))
				writer.write(fd.folder + "/");
			String posterFile = file_name + ".png";
			writer.write(posterFile);
			writer.write("\",");
			writer.newLine();

			// VLC Action
			writer.write("\"action\": \"system:tvx:launch:org.videolan.vlc\",");
			writer.newLine();
			writer.write("\"data\": {");
			writer.newLine();
			writer.write("\"uri\": \"http://" + SERVER + "/");
			if (!fd.folder.isBlank() && !fd.folder.equals("FILMS"))
				writer.write(escapeForInternet(fd.folder) + "/");
			writer.write(escapeForInternet(file_name) + "." + fd.f.getFileExtension());
			writer.write("\",");
			writer.newLine();
			writer.write("\"type\": \"video/*\"");
			writer.newLine();
			writer.write("}");
			writer.newLine();

			writer.write("}");
			writer.newLine();

			writer.write("]");
			writer.newLine();
			writer.write("}");
			writer.newLine();

			writer.write("]");
			writer.newLine();
			writer.write("}");
			writer.newLine();

		} catch (IOException e) {
			Logger.error(e, "Error during json writer {}", jsonFile.getAbsolutePath());
		}
	}

	private static String escapeForInternet(String s) {
		String escaped = "";
		escaped = s.replace(" ", "%20");
		return escaped;
	}

	private static String createListFor(List<FilmData> data, String jsonName, File folder) {
		// No list for less than 4 movies
		if (data.size() < 5)
			return null;
		File jsonFile = new File(folder, jsonName + ".json");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile))) {
			writer.write("{");
			writer.newLine();

			writer.write("\"type\": \"pages\",");
			writer.newLine();
			writer.write("\"headline\": \"Tous les Films\",");
			writer.newLine();
			writer.write("\"template\": {");
			writer.newLine();
			writer.write("\"type\": \"separate\",");
			writer.newLine();
			writer.write("\"layout\": \"0,0,2,3\",");
			writer.newLine();
			writer.write("\"iconSize\": \"medium\",");
			writer.newLine();
			writer.write("\"image\": \"Image\",");
			writer.newLine();
			writer.write("\"title\": \"Title\",");
			writer.newLine();
			writer.write("\"alignment\" : \"title-center\",");
			writer.newLine();
			writer.write("\"imageFiller\": \"height-center\",");
			writer.newLine();
			writer.write("\"color\": \"msx-gray-soft\"");
			writer.newLine();
			writer.write("},");
			writer.newLine();

			writer.write("\"items\": [");
			writer.newLine();

			Iterator<FilmData> it = data.iterator();
			while (it.hasNext()) {
				FilmData fd = it.next();

				writer.write("{");
				writer.newLine();

				writer.write("\"title\": \"");
				writer.write(fd.f.getTitle_fr());
				writer.write("\",");
				writer.newLine();

				writer.write("\"image\": \"http://" + SERVER + "/");
				if (!fd.folder.isBlank() && !fd.folder.equals("FILMS"))
					writer.write(fd.folder + "/");
				String file_name = Util.getEscapedName(fd.f.getTitle_fr()) + " " + fd.f.getRelease_year();
				String posterFile = file_name + "_Poster.png";
				writer.write(posterFile);
				writer.write("\",");
				writer.newLine();

				writer.write("\"action\": \"content:http://" + SERVER + "/");
				String jsonPage = "JSON/" + file_name + ".json";
				writer.write(jsonPage);
				writer.write("\"");
				writer.newLine();

				writer.write("}");
				if (it.hasNext())
					writer.write(",");
				writer.newLine();
			}

			writer.write("]");
			writer.newLine();

			writer.write("}");
			writer.newLine();
			return jsonFile.getName();

		} catch (IOException e) {
			Logger.error(e, "Error during json writer {}", jsonFile.getAbsolutePath());
		}
		return null;
	}

	private void loadFullData(File current, String folder) {
		if (current.isDirectory()) {
			for (File f : current.listFiles(f -> f.isDirectory() || f.getName().endsWith(".properties"))) {
				loadFullData(f, f.getParentFile().getName());
			}
		} else if (current.isFile()) {
			Film film = PropertiesLoader.readFilmData(current);
			if (film != null) {
				fullData.add(new FilmData(film, folder));
			}
		} else {
			Logger.warn("What is : {}", current.getAbsolutePath());
		}
	}

	private static class FilmData {
		public Film f;
		public String folder;

		public FilmData(Film f, String folder) {
			this.f = f;
			this.folder = folder;
		}

	}
}
