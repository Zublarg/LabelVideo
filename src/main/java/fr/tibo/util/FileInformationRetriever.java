package fr.tibo.util;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Stream;

import fr.tibo.bean.Film;

/*
 * It could be replaced by any other way to get the duration and language 
 */
public class FileInformationRetriever {

	public static void main(String[] args) {
		// File folder = new File("P:\\Videos\\A trier");
		File file = new File("P:\\Videos\\A trier\\Bullet Train 2022.mkv");
		// for (File file : folder.listFiles()) {
		if (file.isFile()) {
			if (file.getAbsolutePath().endsWith("mkv"))
				enhanceFilmData(file, null);
		}
		// }
	}

	public static void enhanceFilmData(File movie, Film film) {
		try {
			if (film == null)
				Logger.info("Only show info from Movie {}", movie.getAbsolutePath());
			FFprobeResult result = FFprobe.atPath(Paths.get("C:\\Program Files\\ffmpeg\\bin")).setShowStreams(true)
					.setInput(movie.getAbsolutePath()).execute();

			Float durationSeconds = 0f;
			List<Stream> streams = result.getStreams();
			Set<String> audios = new HashSet<>();
			Set<String> subtitles = new HashSet<>();
			for (int i = 0; i < streams.size() && durationSeconds <= 0f; i++) {

				Stream sss = streams.get(i);

				// try to get information of duration on any stream
				if (sss.getDuration() != null)
					durationSeconds = Float.max(durationSeconds, sss.getDuration());

				StreamType type = sss.getCodecType();

				String lang = sss.getTag("language");
				if (lang == null)
					lang = sss.getTag("LANGUAGE");

				if (lang != null) {
					lang = lang.toUpperCase().substring(0, 2);
					if (type.equals(StreamType.AUDIO))
						audios.add(lang);
					else if (type.equals(StreamType.SUBTITLE))
						subtitles.add(lang);
					else if (type.equals(StreamType.VIDEO))
						audios.add(lang);
					else
						Logger.warn("Lang info on other codec type : {} -> {}", type, lang);
				}

			}

			if (audios.size() == 0) {
				Logger.warn("Unable to retrieve audio information from file : {}", movie.getAbsolutePath());
			}

			List<String> audioAndSubList = new ArrayList<>();
			if (audios.contains("FR")) {
				audioAndSubList.add("VF");
				audios.remove("FR");
			}
			if (audios.size() > 0) {
				audioAndSubList.add("VO");

				if (subtitles.contains("FR"))
					audioAndSubList.add("VOSTFR");
			}

			String audioAndSub = audioAndSubList.stream().collect(Collectors.joining(","));

			if (film != null) {
				film.setAudioAndSubs(audioAndSub);
			} else {
				Logger.info("-> Lang and ST read from real audio: {}", audioAndSub);
			}

			// Duration calculation
			long durationHour = (long) (durationSeconds / 3600.0);
			long durationMinutes = Math.round(durationSeconds / 60.0) - 60 * durationHour;
			String duration = String.format("%dh%02d", durationHour, durationMinutes);
			if (film != null)
				film.setRealDuree(duration);
			else
				Logger.info("-> Duration read from real file : {}", duration);

		} catch (Exception e) {
			Logger.error(e, "Unable to read file {}", movie.getAbsolutePath());
		}

	}
}
