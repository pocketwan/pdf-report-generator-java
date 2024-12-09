/*
 * Copyright © 2024 Thabo Pokothoane. All rights reserved.
 *
 * Unauthorized copying, distribution, or modification is strictly prohibited.
 *
 * Project: PDF Report Generator in Java using Apache PDFBox
 */

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.LocalDate;

public class PDFReportGenerator {

  // Student information
  private static String docTitle = "University College Academic Report ";
  private static String studentName = "John Doe";
  private static String studentNumber = "JHNDOE001";
  private static String programmeName = "Civil Engineering";
  private static String degreeName = programmeName;
  private static int currentYear = 2024;
  private static int acadYear = 1;

  // Course marks
  private static int course1Mark = 75;
  private static int course2Mark = 84;
  private static int course3Mark = 78;
  private static int course4Mark = 80;
  private static int course5Mark = 88;
  private static int course6Mark = 82;
  private static int course7Mark = 96;
  private static int course8Mark = 77;
  private static int course9Mark = 70;

  // Course credits
  private static int course1Credits = 16;
  private static int course2Credits = 24;
  private static int course3Credits = 16;
  private static int course4Credits = 18;
  private static int course5Credits = 18;
  private static int course6Credits = 18;
  private static int course7Credits = 8;
  private static int course8Credits = 8;
  private static int course9Credits = 18;

  // Average GPA
  private static double avgGPA;

  // Decimal formatting for GPA
  private static DecimalFormatSymbols symbol = new DecimalFormatSymbols();
  private static PDDocument document = new PDDocument();
  private static PDType0Font font;
  static {
    symbol.setDecimalSeparator('.');

    try {
      // Load the font safely
      font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Black.ttf"));
    } catch (IOException e) {
      e.printStackTrace(); // Log or handle the exception
      font = null; // Fallback: Set to null or use a default font
    }

  }
  private static DecimalFormat df = new DecimalFormat("0.00", symbol);
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("PDF Report Generator");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(600, 400);

      // Create table with sample data
      String[] columns = {
        "Course ID",
        "Credits",
        "Course Name",
        "Mark"
      };
      Object[][] data = {
        {
          "CEM1008F",
          course1Credits,
          "Chemistry for Engineers",
          course1Mark
        },
        {
          "CIV1005W",
          course2Credits,
          "Introduction to Engineering",
          course2Mark
        },
        {
          "CIV1007S",
          course3Credits,
          "Engineering Mechanics",
          course3Mark
        },
        {
          "CSC1015S",
          course4Credits,
          "Computer Science",
          course4Mark
        },
        {
          "MAM1020F",
          course5Credits,
          "Mathematics IA for Engineers",
          course5Mark
        },
        {
          "MAM1021S",
          course6Credits,
          "Mathematics IB for Engineers",
          course6Mark
        },
        {
          "MEC1010F",
          course7Credits,
          "Introduction to Engineering Drawing A",
          course7Mark
        },
        {
          "MEC1011S",
          course8Credits,
          "Introduction to Design",
          course8Mark
        },
        {
          "PHY1012S",
          course9Credits,
          "Physics A for Engineers",
          course9Mark
        },
      };

      DefaultTableModel model = new DefaultTableModel(data, columns);
      JTable table = new JTable(model);

      int creditsReceived = (course1Credits * course1Mark) + (course2Credits * course2Mark) + (course3Credits * course3Mark) +
      (course4Credits * course4Mark) + (course5Credits * course5Mark) + (course6Credits * course6Mark) +
      (course7Credits * course7Mark) + (course8Credits * course8Mark) + (course9Credits * course9Mark);

      int totalCredits = (course1Credits * 100) + (course2Credits * 100) + (course3Credits * 100) +
      (course4Credits * 100) + (course5Credits * 100) + (course6Credits * 100) +
      (course7Credits * 100) + (course8Credits * 100) + (course9Credits * 100);
      avgGPA = (double) creditsReceived / totalCredits * 100;

      // Add table to scroll pane
      JScrollPane scrollPane = new JScrollPane(table);
      frame.add(scrollPane, BorderLayout.CENTER);

      // Add Export button
      JButton exportButton = new JButton("Generate PDF Report");
      frame.add(exportButton, BorderLayout.SOUTH);

      // Button action to export table to PDF
      exportButton.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF File");
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
          File fileToSave = fileChooser.getSelectedFile();
          try {
            generatePDF(table, fileToSave.getAbsolutePath() + ".pdf");
            JOptionPane.showMessageDialog(frame, "Successfully generated PDF!");
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error saving PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      });

      frame.setVisible(true);
    });
  }

  /**
   * Exports a JTable to a PDF file.
   */
  public static void generatePDF(JTable table, String filePath) throws IOException {
    // Create a new PDF document

    PDPage page = new PDPage(PDRectangle.A4);
    document.addPage(page);
    PDPageContentStream contentStream = new PDPageContentStream(document, page);
    addDateStamp(contentStream, page, getDate());

    // Add header (University logo and title)
    addHeaderImage(contentStream, document, page);

    // Add footer
    addFooter(contentStream, page);

    float yStart = page.getMediaBox().getHeight() - 150;
    float xStart = 150;

    // Report title
    contentStream.beginText();
    contentStream.setFont(font, 16);
    contentStream.newLineAtOffset(xStart, yStart);
    contentStream.showText(docTitle + Integer.toString(currentYear));

    // Student name and surname
    font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Regular.ttf"));
    contentStream.setFont(font, 11);
    contentStream.newLineAtOffset(-100, -30);
    contentStream.showText("Student Name: " + studentName);

    // Student number
    contentStream.newLineAtOffset(0, -15);
    contentStream.showText("Student Number: " + studentNumber);

    // Degree name
    contentStream.newLineAtOffset(0, -15);
    contentStream.showText("Degree Programme: BSc. in " + degreeName);

    // Academic year of study
    contentStream.newLineAtOffset(0, -15);
    contentStream.showText("Year of Study: " + Integer.toString(acadYear));
    contentStream.endText();

    // Define table dimensions
    yStart -= 100;
    xStart = 50;

    float tableWidth = 450f;
    float[] columnWidths = {
      100f,
      50f,
      200f,
      50f
    };
    float cellMargin = 5f;
    float rowHeight = 20f;
    float yPosition = yStart;

    // Draw table headers
    font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Black.ttf"));
    contentStream.setFont(font, 12);
    contentStream.beginText();
    contentStream.newLineAtOffset(xStart + cellMargin, yPosition - 15);

    for (int col = 0; col < table.getColumnCount(); col++) {
      contentStream.showText(table.getColumnName(col));
      contentStream.newLineAtOffset(columnWidths[col], 0);
    }
    contentStream.endText();

    // Draw rows
    font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Regular.ttf"));
    contentStream.setFont(font, 11);
    yPosition -= rowHeight;

    for (int row = 0; row < table.getRowCount(); row++) {
      float xPosition = xStart;
      for (int col = 0; col < table.getColumnCount(); col++) {
        String text = table.getValueAt(row, col).toString();
        contentStream.beginText();
        contentStream.newLineAtOffset(xPosition + cellMargin, yPosition - 15);
        contentStream.showText(text);
        contentStream.endText();
        xPosition += columnWidths[col];
      }
      yPosition -= rowHeight;
    }

    // Draw horizontal lines
    contentStream.setLineWidth(1f);
    for (int i = 0; i <= table.getRowCount() + 1; i++) {
      float lineY = yStart - (i * rowHeight);
      contentStream.moveTo(xStart, lineY);
      contentStream.lineTo(xStart + tableWidth, lineY);
      contentStream.stroke();
    }

    // Draw vertical lines
    float xPosition = xStart;
    for (int col = 0; col < table.getColumnCount(); col++) {
      contentStream.moveTo(xPosition, yStart);
      contentStream.lineTo(xPosition, yStart - (table.getRowCount() + 1) * rowHeight);
      contentStream.stroke();
      xPosition += columnWidths[col];
    }

    // Add the last vertical line on the right edge of the table
    contentStream.moveTo(xStart + tableWidth, yStart);
    contentStream.lineTo(xStart + tableWidth, yStart - (table.getRowCount() + 1) * rowHeight);
    contentStream.stroke();

    contentStream.beginText();
    contentStream.newLineAtOffset(xStart, yPosition - 30);
    font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Black.ttf"));
    contentStream.setFont(font, 11);
    contentStream.showText("Average GPA: " + df.format(avgGPA));
    contentStream.newLineAtOffset(0, -15);
    contentStream.showText("Result: Pass with Distinction");
    contentStream.endText();

    contentStream.close();
    document.save(filePath);
    document.close();
  }

  private static void addHeaderImage(PDPageContentStream contentStream, PDDocument document, PDPage page) throws IOException {
    // Load and draw the logo
    PDImageXObject logo = PDImageXObject.createFromFile("../images/logo/vecteezy_crest_1201769.png", document);
    contentStream.drawImage(logo, 250, page.getMediaBox().getHeight() - 120, 100, 100);
  }

  private static void addFooter(PDPageContentStream contentStream, PDPage page) throws IOException {
    // Draw a footer line
    contentStream.setNonStrokingColor(Color.LIGHT_GRAY);
    contentStream.setLineWidth(1f);
    contentStream.moveTo(50, 50);
    contentStream.lineTo(page.getMediaBox().getWidth() - 50, 50);
    contentStream.stroke();

    // Add footer text
    contentStream.setNonStrokingColor(Color.DARK_GRAY);
    font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Black.ttf"));

    contentStream.setFont(font, 10);
    contentStream.beginText();
    contentStream.newLineAtOffset(50, 40);
    contentStream.showText("Page 1 | Generated on: " + getDateAndTime());
    contentStream.endText();
  }

  private static String getDateAndTime() {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("GMT"));
    // Format the date and time
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
    String formattedDateTime = now.format(formatter);
    return formattedDateTime;
  }

  private static String getDate() {
    LocalDate currentDate = LocalDate.now();

    // Format the date
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = currentDate.format(formatter);
    return formattedDate;

  }

  private static void addDateStamp(PDPageContentStream contentStream, PDPage page, String dateText) throws IOException {
    // Dimensions for the stamp
    float xPosition = page.getMediaBox().getWidth() - 500; // Right-aligned
    float yPosition = page.getMediaBox().getHeight() - 600; // Top
    float stampWidth = 180;
    float stampHeight = 40;

    // Draw the rectangle (outer border of the stamp)
    //  contentStream.setNonStrokingColor(new Color(255, 69, 0)); // Professional red ink color
    //  contentStream.setStrokingColor(new Color(255, 69, 0)); // Border color
    contentStream.setLineWidth(2);
    contentStream.addRect(xPosition, yPosition, stampWidth, stampHeight);
    contentStream.stroke();

    // Draw the filled background (light faded ink-like effect)
    contentStream.setNonStrokingColor(new Color(255, 228, 225)); // Lighter ink fill

    // Add the date text inside the rectangle
    contentStream.beginText();
    font = PDType0Font.load(document, new File("../fonts/sourcesans3/static/SourceSans3-Black.ttf"));

    contentStream.setFont(font, 11);
    contentStream.setNonStrokingColor(new Color(139, 0, 0)); // Dark ink text
    float textX = xPosition + 10; // Padding from the left
    float textY = yPosition + stampHeight / 2; // Center vertically
    contentStream.newLineAtOffset(textX, textY);
    contentStream.showText("STAMPED: " + dateText);
    contentStream.endText();

    // Add a second line of text (optional)
    contentStream.beginText();
    contentStream.newLineAtOffset(textX, textY - 15); // Below the first line
    contentStream.showText("University College");
    contentStream.endText();
  }
}