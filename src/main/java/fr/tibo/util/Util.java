package fr.tibo.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.tinylog.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

	private static final String BASE_URL = "https://api.themoviedb.org/";

	public static JsonNode getData(String query) {
		try {

			String url = BASE_URL + query;

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
					.header("Authorization", "Bearer " + TokenProvider.getToken()).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				Logger.info("Response received");
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readTree(response.body());
			} else {
				Logger.warn("No Response !\nMessage : {}", response.body());
				return null;
			}

		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}

	public static Path dlImage(String ImagePath) {
		try {

			String imageUrl = "https://image.tmdb.org/t/p/original/" + ImagePath;
			Path destination = Paths.get("downloaded-image.jpg");

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(imageUrl)).build();

			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(destination));

			if (response.statusCode() == 200) {
				Path usedPath = response.body();
				Logger.info("Image Downloaded : {}", usedPath);
				return usedPath;
			} else {
				Logger.warn("No Response !\nMessage : {}", response.body());
				return null;
			}
		} catch (Exception e) {
			Logger.error(e);
			return null;
		}
	}
}
