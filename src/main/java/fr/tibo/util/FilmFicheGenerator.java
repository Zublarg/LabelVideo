package fr.tibo.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

import fr.tibo.bean.Film;

public class FilmFicheGenerator {

	public static void drawFiche(Film f) {
		try {
			int width = 1920;
			int height = 1080;

			// empty image
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();

			// better rendering
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Background
			g.setColor(new Color(20, 20, 30)); // dark blue
			g.fillRect(0, 0, width, height);

			// read image
			Path p = Util.dlImage(f.getPoster_path());
			BufferedImage poster = ImageIO.read(p.toFile()); // poster_path
			g.drawImage(poster, 100, 100, 400, 600, null); // Size and Position

			// Police
			g.setFont(new Font("SansSerif", Font.BOLD, 60));
			g.setColor(Color.WHITE);
			g.drawString(f.getTitle_fr(), 550, 150);

			g.setFont(new Font("SansSerif", Font.PLAIN, 40));
			g.drawString("Titre Original: " + f.getOriginal_title(), 550, 220);
			g.drawString("Date de sortie: " + f.getRelease_date(), 550, 290);
			g.drawString("Durée: " + f.getDuree() + " min.", 550, 360);
			g.drawString("Genres: " + f.getGenres(), 550, 430);
			g.drawString("Classification : " + f.getClassification(), 550, 500);

			g.setFont(new Font("SansSerif", Font.PLAIN, 20));
			String note = f.getNote();
			if (note.length() > 3)
				note = f.getNote().substring(0, 2);
			g.drawString("Note: " + note, 410, 720);

			// Synopsis (multi-ligne)
			g.setFont(new Font("SansSerif", Font.PLAIN, 30));
			drawMultilineText(g, f.getOverview(), 550, 570, 1000, 30);

			// Save image
			String file_name = f.getOriginal_title().replace(",", "").replace(":", "").replace(" ", "_");
			ImageIO.write(image, "png", new File(file_name + ".png"));
			g.dispose();
			Logger.info("Fiche created for : {}", f.toString());
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	public static void drawMultilineText(Graphics2D g, String text, int x, int y, int maxWidth, int lineHeight) {
		FontMetrics fm = g.getFontMetrics();
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();
		for (String word : words) {
			String testLine = line + word + " ";
			if (fm.stringWidth(testLine) > maxWidth) {
				g.drawString(line.toString(), x, y);
				y += lineHeight;
				line = new StringBuilder(word + " ");
			} else {
				line.append(word).append(" ");
			}
		}
		g.drawString(line.toString(), x, y);
	}
}
