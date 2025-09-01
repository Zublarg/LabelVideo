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

public class FilmFicheGeneratorHD {

	public static File drawFiche(String path, Film f) {
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

			String file_name = Util.getEscapedName(f.getTitle_fr());

			// read image
			Path p = Util.dlImage(f.getPoster_path());
			if (p == null) {
				Logger.warn("Unable to read image : {}", f.getPoster_path());
				Logger.warn("Film without image : {}", f.getOriginal_title());
			} else {
				BufferedImage poster = ImageIO.read(p.toFile()); // poster_path
				BufferedImage scaledPoster = progressiveScale(poster, 400, 600); // Size
				g.drawImage(scaledPoster, 100, 100, null); // and Position

				// Save Poster
				File outputPosterFile = new File(path, file_name + " " + f.getRelease_year() + "_Poster.png");
				ImageIO.write(scaledPoster, "png", outputPosterFile);
			}

			// Police
			g.setFont(new Font("SansSerif", Font.BOLD, 60));
			g.setColor(Color.WHITE);
			g.drawString(f.getTitle_fr(), 550, 150);

			g.setFont(new Font("SansSerif", Font.PLAIN, 40));
			g.drawString("Titre Original: " + f.getOriginal_title(), 550, 220);
			g.drawString("Date de sortie: " + f.getRelease_date(), 550, 290);
			// if we have some data
			if (!f.getRealDuree().isBlank() && !f.getRealDuree().equals("0h00")) {
				g.drawString("Durée: " + f.getRealDuree(), 550, 360);
			} else {
				g.drawString("Durée: " + f.getDuree() + " min.", 550, 360);
			}
			g.drawString("Genres: " + f.getGenres(), 550, 430);
			g.drawString("Enfant : " + f.getClassification(), 550, 500);

			g.setFont(new Font("SansSerif", Font.PLAIN, 20));
			g.drawString(f.getAudioAndSubs(), 100, 720);

			String note = f.getNote();
			if (note.length() > 3)
				note = f.getNote().substring(0, 3);
			g.drawString("Note: " + note, 410, 720);

			// Synopsis (multi-ligne)
			g.setFont(new Font("SansSerif", Font.PLAIN, 30));
			drawMultilineText(g, f.getOverview(), 550, 570, 1000, 30);

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
