package fr.tibo.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.tinylog.Logger;

public class TokenProvider {

	private static TokenProvider tp;
	private final String token;

	private TokenProvider() {
		String data = "";
		try {
			// I create my account in TMDB
			// I save my token into a file
			// I not share this file ;)
			URL filePath = TokenProvider.class.getResource("/apiToken.txt");
			data = Files.readString(Paths.get(filePath.toURI())).trim();
			Logger.info("Token read");
		} catch (Exception e) {
			Logger.error(e, "Unable to read token");
			System.exit(1);
		}
		token = data;
	}

	public static String getToken() {
		if (tp == null)
			tp = new TokenProvider();
		return tp.token;
	}
}
