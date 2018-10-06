package pl.coderstrust.accounting.logic.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.Arrays;

public class PdfWithTableGenerate {

  public void generatePdf(Table table) throws IOException {
    PDDocument doc = null;
    try {
      doc = new PDDocument();
      drawTable(doc, table);
      doc.save("txt.pdf");
    } finally {
      if (doc != null) {
        doc.close();
      }
    }
  }

  public static void drawTable(PDDocument doc, Table table) throws IOException {
    Integer rowsPerPage =
        new Double(Math.floor(table.getHeight() / table.getRowHeight())).intValue() - 1;
    Integer numberOfPages = new Double(
        Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();

    for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
      PDPage page = generatePage(doc, table);
      PDPageContentStream contentStream = generateContentStream(doc, page, table);
      String[][] currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount);
      drawCurrentPage(table, currentPageContent, contentStream);
    }
  }

  private static void drawCurrentPage(Table table, String[][] currentPageContent,
      PDPageContentStream contentStream)
      throws IOException {
    float tableTopY = table.isLandScape() ? table.getPageSize().getWidth() - table.getMargin()
        : table.getPageSize().getHeight() - table.getMargin();

    drawTableGrid(table, currentPageContent, contentStream, tableTopY);

    float nextTextX = table.getMargin() + table.getCellMargin();
    float nextTextY = tableTopY - (table.getRowHeight() / 2)
        - ((table.getPdfTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table
        .getFontSize()) / 4);

    writeContentLine(table.getColumnsNamesAsArray(), contentStream, nextTextX, nextTextY, table);
    nextTextY -= table.getRowHeight();
    nextTextX = table.getMargin() + table.getCellMargin();

    for (int i = 0; i < currentPageContent.length; i++) {
      writeContentLine(currentPageContent[i], contentStream, nextTextX, nextTextY, table);
      nextTextY -= table.getRowHeight();
      nextTextX = table.getMargin() + table.getCellMargin();
    }
    contentStream.close();
  }

  private static void writeContentLine(String[] lineContent, PDPageContentStream contentStream,
      float nextTextX, float nextTextY,
      Table table) throws IOException {
    for (int i = 0; i < table.getNumberOfColumns(); i++) {
      String text = lineContent[i];
      contentStream.beginText();
      contentStream.moveTextPositionByAmount(nextTextX, nextTextY);
      contentStream.drawString(text != null ? text : "");
      contentStream.endText();
      nextTextX += table.getColumns().get(i).getWidth();
    }
  }

  private static void drawTableGrid(Table table, String[][] currentPageContent,
      PDPageContentStream contentStream, float tableTopY)
      throws IOException {
    float nextY = tableTopY;
    for (int i = 0; i <= currentPageContent.length + 1; i++) {
      contentStream.drawLine(table.getMargin(), nextY, table.getMargin() + table.getWidth(), nextY);
      nextY -= table.getRowHeight();
    }

    final float tableYLength =
        table.getRowHeight() + (table.getRowHeight() * currentPageContent.length);
    final float tableBottomY = tableTopY - tableYLength;
    float nextX = table.getMargin();
    for (int i = 0; i < table.getNumberOfColumns(); i++) {
      contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
      nextX += table.getColumns().get(i).getWidth();
    }
    contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
  }

  private static String[][] getContentForCurrentPage(Table table, Integer rowsPerPage,
      int pageCount) {
    int startRange = pageCount * rowsPerPage;
    int endRange = (pageCount * rowsPerPage) + rowsPerPage;
    if (endRange > table.getNumberOfRows()) {
      endRange = table.getNumberOfRows();
    }
    return Arrays.copyOfRange(table.getContent(), startRange, endRange);
  }

  private static PDPage generatePage(PDDocument doc, Table table) {
    PDPage page = new PDPage();
    page.setMediaBox(table.getPageSize());
    page.setRotation(table.isLandScape() ? 100 : 0);
    doc.addPage(page);
    return page;
  }

  private static PDPageContentStream generateContentStream(PDDocument doc, PDPage page, Table table)
      throws IOException {
    PDPageContentStream contentStream = new PDPageContentStream(doc, page, false, false);
    if (table.isLandScape()) {
      contentStream.concatenate2CTM(0, 1, -1, 0, table.getPageSize().getWidth(), 0);
    }
    contentStream.setFont(table.getPdfTextFont(), table.getFontSize());
    return contentStream;
  }
}
