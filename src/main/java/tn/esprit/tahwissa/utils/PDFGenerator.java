package tn.esprit.tahwissa.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import tn.esprit.tahwissa.models.ReservationVoyage;

import java.awt.Color;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFGenerator {

    // Theme Colors (Matching UI: Indigo/Purple palette)
    private static final Color PRIMARY_INDIGO = new Color(79, 70, 229);   // #4F46E5
    private static final Color TEXT_DARK      = new Color(31, 41, 55);    // #1F2937
    private static final Color TEXT_MUTED     = new Color(107, 114, 128); // #6B7280
    private static final Color ROW_LIGHT      = new Color(249, 250, 251); // #F9FAFB
    private static final Color ROW_WHITE      = Color.WHITE;

    public static void generateReservationsReport(String filePath, List<ReservationVoyage> reservations) throws Exception {
        // Landscape orientation for data tables
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // ─── Header: Logo + Title + Date ───────────────────────────────────────
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1f, 3f}); // Logo takes 1/4, Text takes 3/4
        headerTable.setSpacingAfter(20);

        try {
            // Add Logo
            String logoPath = PDFGenerator.class.getResource("/images/logo.png").toExternalForm();
            Image logo = Image.getInstance(new java.net.URL(logoPath));
            logo.scaleToFit(80, 80);
            
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(logoCell);

        } catch (Exception e) {
            // Fallback if logo is missing
            PdfPCell emptyCell = new PdfPCell(new Phrase("Tahwissa"));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(emptyCell);
            System.err.println("Could not load logo for PDF: " + e.getMessage());
        }

        // Title and Date Info
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_INDIGO);
        Font dateFont  = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_MUTED);

        Paragraph titleContainer = new Paragraph();
        titleContainer.add(new Chunk("Rapport des Réservations\n", titleFont));
        
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        titleContainer.add(new Chunk("Généré le : " + dateStr + "\nTotal : " + reservations.size() + " réservations", dateFont));

        PdfPCell textCell = new PdfPCell(titleContainer);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(textCell);

        document.add(headerTable);

        // ─── Separator Line ────────────────────────────────────────────────────
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingAfter(20);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderWidthBottom(2f);
        lineCell.setBorderColorBottom(PRIMARY_INDIGO);
        lineTable.addCell(lineCell);
        document.add(lineTable);

        // ─── Data Table ────────────────────────────────────────────────────────
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 3f, 2f, 2f, 1.5f, 2f, 2f}); // Relative column widths

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, TEXT_DARK);
        Font cellBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);

        // Headers
        String[] headers = {"Ref", "Voyage", "Client", "Date de Dépt", "Personnes", "Montant", "Statut"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(PRIMARY_INDIGO);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(8);
            cell.setBorderColor(Color.WHITE); // White borders between header cells
            table.addCell(cell);
        }

        // Data Rows
        boolean alternateRow = false;
        for (ReservationVoyage r : reservations) {
            Color rowColor = alternateRow ? ROW_LIGHT : ROW_WHITE;

            // Ref
            addStyledCell(table, String.valueOf(r.getId()), cellBoldFont, rowColor, Element.ALIGN_CENTER);
            // Voyage
            addStyledCell(table, r.getTitreVoyage() != null ? r.getTitreVoyage() : "-", cellFont, rowColor, Element.ALIGN_LEFT);
            // Client
            addStyledCell(table, "Client #" + r.getIdUtilisateur(), cellFont, rowColor, Element.ALIGN_CENTER);
            // Date
            String dateText = r.getDateReservation() != null ? r.getDateReservation().toLocalDate().toString() : "-";
            addStyledCell(table, dateText, cellFont, rowColor, Element.ALIGN_CENTER);
            // Personnes
            addStyledCell(table, String.valueOf(r.getNbrPersonnes()), cellFont, rowColor, Element.ALIGN_CENTER);
            // Montant
            addStyledCell(table, r.getMontantTotal().toString() + " DT", cellBoldFont, rowColor, Element.ALIGN_RIGHT);
            // Statut
            addStyledCell(table, r.getStatut().getLabel(), cellFont, rowColor, Element.ALIGN_CENTER);

            alternateRow = !alternateRow;
        }

        document.add(table);
        document.close();
    }

    /**
     * Helper to create styled data cells
     */
    private static void addStyledCell(PdfPTable table, String text, Font font, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setBorderColor(new Color(229, 231, 235)); // Light gray border (#E5E7EB)
        cell.setBorderWidth(1f);
        table.addCell(cell);
    }
}
