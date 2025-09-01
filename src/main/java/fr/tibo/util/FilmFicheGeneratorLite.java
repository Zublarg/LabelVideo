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

public class FilmFicheGeneratorLite {

	public static File drawFiche(String path, Film f) {
		try {
			int width = 1500;
			int height = 844;

			int imgHei = 600;
			int imgWid = 400;
			int startx = 22;
			int starty = 22;

			// empty image
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();

			// better rendering
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Background
			g.setColor(new Color(20, 20, 30)); // dark blue
			g.fillRect(0, 0, width, height);

			String file_name = Util.getEscapedName(f.getTitle_fr());

			// read image
			File posterFile = new File(path, file_name + " " + f.getRelease_year() + "_Poster.png");
			if (posterFile.isFile()) {
				BufferedImage poster = ImageIO.read(posterFile); // poster file
				g.drawImage(poster, startx, starty, null); // and Position
			} else {

				Path p = Util.dlImage(f.getPoster_path());
				if (p == null) {
					Logger.warn("Unable to read image : {}", f.getPoster_path());
					Logger.warn("Film without image : {}", f.getOriginal_title());
				} else {
					BufferedImage poster = ImageIO.read(p.toFile()); // poster_path
					BufferedImage scaledPoster = progressiveScale(poster, imgWid, imgHei); // Size
					g.drawImage(scaledPoster, startx, starty, null); // and Position

					// Save Poster
					ImageIO.write(scaledPoster, "png", posterFile);
				}
			}

			// POLICE
			g.setColor(Color.WHITE);

			int startytext = starty + imgWid + 40;
			g.setFont(new Font("SansSerif", Font.PLAIN, 40));
			int startxtext = startx + 40;
			g.drawString("Titre Original: " + f.getOriginal_title(), startytext, startxtext);

			startxtext += 70;
			g.drawString("Date de sortie: " + f.getRelease_date(), startytext, startxtext);

			startxtext += 70;
			// if we have some data
			if (!f.getRealDuree().isBlank() && !f.getRealDuree().equals("0h00")) {
				g.drawString("Durée: " + f.getRealDuree(), startytext, startxtext);
			} else {
				g.drawString("Durée: " + f.getDuree() + " min.", startytext, startxtext);
			}
			startxtext += 70;
			g.drawString("Genres: " + f.getGenres(), startytext, startxtext);
			startxtext += 70;
			g.drawString("Enfant : " + f.getClassification(), startytext, startxtext);

			g.setFont(new Font("SansSerif", Font.PLAIN, 20));
			g.drawString(f.getAudioAndSubs(), starty, startx + imgHei + 20);

			String note = f.getNote();
			if (note.length() > 3)
				note = f.getNote().substring(0, 3);
			g.drawString("Note: " + note, starty + imgWid - 90, startx + imgHei + 20);

			// Synopsis (multi-ligne)
			startxtext += 70;
			g.setFont(new Font("SansSerif", Font.PLAIN, 30));
			drawMultilineText(g, f.getOverview(), startytext, startxtext, 1000, 30);

			// Save image
			File outputFile = new File(path, file_name + " " + f.getRelease_year() + ".png");
			ImageIO.write(image, "png", outputFile);
			g.dispose();
			Logger.info("Fiche created for : {}", f.toString());
			return outputFile;
		} catch (IOException e) {
			Logger.error(e);
			return null;
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

	public static BufferedImage progressiveScale(BufferedImage img, int targetWidth, int targetHeight) {
		int currentWidth = img.getWidth();
		int currentHeight = img.getHeight();
		BufferedImage scaled = img;

		// Réduction progressive
		while (currentWidth > 2 * targetWidth || currentHeight > 2 * targetHeight) {
			currentWidth = Math.max(currentWidth / 2, targetWidth);
			currentHeight = Math.max(currentHeight / 2, targetHeight);

			BufferedImage temp = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = temp.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.drawImage(scaled, 0, 0, currentWidth, currentHeight, null);
			g2d.dispose();
			scaled = temp;
		}

		// Dernière passe vers la taille finale
		BufferedImage finalImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = finalImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(scaled, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();

		return finalImage;
	}

}
