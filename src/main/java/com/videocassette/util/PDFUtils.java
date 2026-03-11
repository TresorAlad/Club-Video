package com.videocassette.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.videocassette.model.Abonne;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Desktop;

/**
 * PDFUtils est une classe qui s'occupe de créer des documents PDF.
 * Ici, on l'utilise surtout pour fabriquer les cartes de membre des abonnés.
 */
public class PDFUtils {

    /**
     * Cette méthode fabrique un fichier PDF contenant une carte de membre.
     * @param abonne L'abonné pour qui on crée la carte.
     * @return Le chemin (l'adresse) du fichier créé sur l'ordinateur.
     */
    public static String generateMemberCard(Abonne abonne) {
        // 1. On crée un dossier appelé 'cards' pour ranger les cartes
        File directory = new File("cards");
        if (!directory.exists()) {
            boolean created = directory.mkdirs(); // Crée le dossier s'il n'existe pas
            if (!created) {
                System.err.println("Impossible de créer le dossier 'cards'");
                return null;
            }
        }

        // 2. On choisit un nom de fichier unique pour la carte
        String fileName = "Carte_Abonne_" + abonne.getNomAbonne().replaceAll("[^a-zA-Z0-9]", "_") + "_"
                + abonne.getIdAbonne()
                + ".pdf";
        File file = new File(directory, fileName);
        String filePath = file.getAbsolutePath();

        // 3. On définit la taille de la page (Format A6 tourné sur le côté)
        Document document = new Document(PageSize.A6.rotate());
        try {
            // On prépare l'écriture du fichier
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open(); // On ouvre le document pour écrire dedans

            // 4. On définit les polices d'écriture (gras, taille, couleur)
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.NORMAL, java.awt.Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, java.awt.Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, java.awt.Color.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, java.awt.Color.GRAY);

            // 5. On ajoute le logo et le titre du club
            Paragraph header = new Paragraph("🎬 VIDEO CLUB", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("CARTE DE MEMBRE", headerFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(15);
            document.add(subHeader);

            // 6. On écrit les informations de l'abonné
            document.add(new Paragraph("Nom : " + abonne.getNomAbonne(), headerFont));
            document.add(new Paragraph("Code d'abonné : " + abonne.getCodeAbonne(), headerFont));
            document.add(new Paragraph("Adresse : " + abonne.getAdresseAbonne(), normalFont));

            // On formate la date pour qu'elle soit lisible (ex: 25/12/2023)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            document.add(new Paragraph("Date d'adhésion : " + abonne.getDateAbonement().format(formatter), normalFont));

            document.add(new Paragraph("\n")); // Un peu d'espace

            // 7. Petit texte en bas de la carte
            Paragraph footer = new Paragraph("Cette carte est personnelle et obligatoire pour toute location.",
                    smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.add(new Paragraph("123 Rue du Cinéma, Bd 30 Août ADIDOGOME | +228 99 70 70 99", smallFont));

            // 8. On ferme le document, c'est fini !
            document.close();

            // Bonus : On essaie d'ouvrir le PDF automatiquement pour que l'utilisateur le voie
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
