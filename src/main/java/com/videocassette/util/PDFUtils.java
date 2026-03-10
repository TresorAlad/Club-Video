package com.videocassette.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.videocassette.model.Abonne;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.awt.Desktop;

public class PDFUtils {

    public static String generateMemberCard(Abonne abonne) {
        // Create a directory 'cards' in the project roots
        File directory = new File("cards");
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.err.println("Impossible de créer le dossier 'cards'");
                return null;
            }
        }

        String fileName = "Carte_Abonne_" + abonne.getNomAbonne().replaceAll("[^a-zA-Z0-9]", "_") + "_"
                + abonne.getIdAbonne()
                + ".pdf";
        File file = new File(directory, fileName);
        String filePath = file.getAbsolutePath();

        // Size like a business card (85mm x 55mm approx) but using A6 rotated for
        // simplicity in OpenPDF
        Document document = new Document(PageSize.A6.rotate());
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Font styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.NORMAL, java.awt.Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, java.awt.Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, java.awt.Color.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, java.awt.Color.GRAY);

            // Header
            Paragraph header = new Paragraph("🎬 VIDEO CLUB", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("CARTE DE MEMBRE", headerFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(15);
            document.add(subHeader);

            // Information
            document.add(new Paragraph("Nom : " + abonne.getNomAbonne(), headerFont));
            document.add(new Paragraph("Code d'abonné : " + abonne.getCodeAbonne(), headerFont));
            document.add(new Paragraph("Adresse : " + abonne.getAdresseAbonne(), normalFont));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            document.add(new Paragraph("Date d'adhésion : " + abonne.getDateAbonement().format(formatter), normalFont));

            document.add(new Paragraph("\n"));

            // Footer
            Paragraph footer = new Paragraph("Cette carte est personnelle et obligatoire pour toute location.",
                    smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.add(new Paragraph("123 Rue du Cinéma, Bd 30 Août ADIDOGOME | +228 99 70 70 99", smallFont));

            document.close();

            // Try to open the file automatically
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
