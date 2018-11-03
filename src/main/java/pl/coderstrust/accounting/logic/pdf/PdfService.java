package pl.coderstrust.accounting.logic.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.model.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

@Service
public class PdfService {

  public ByteArrayOutputStream generatePdf(Table table, Invoice invoice) throws IOException {
    PDDocument doc = null;

    try {
      doc = new PDDocument();
      drawTable(doc, table, invoice);
      doc.save("invoice.pdf");
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      doc.save(output);
      return output;
    } finally {
      if (doc != null) {
        doc.close();
      }
    }
  }

  public static void drawTable(PDDocument doc, Table table, Invoice invoice) throws IOException {
    Integer rowsPerPage =
        new Double(Math.floor(table.getHeight() / table.getRowHeight())).intValue() - 1;
    Integer numberOfPages = new Double(
        Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();

    for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
      PDPage page = generatePage(doc, table);

      PDPageContentStream contentStream = generateContentStream(doc, page, table, invoice);
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
      contentStream.newLineAtOffset(nextTextX, nextTextY);
      contentStream.showText(text != null ? text : "");
      contentStream.endText();
      nextTextX += table.getColumns().get(i).getWidth();
    }
  }

  private static void drawTableGrid(Table table, String[][] currentPageContent,
      PDPageContentStream contentStream, float tableTopY)
      throws IOException {
    float nextY = tableTopY;
    for (int i = 0; i <= currentPageContent.length + 1; i++) {
      contentStream.moveTo(table.getMargin(), nextY);
      contentStream.lineTo(table.getMargin() + table.getWidth(), nextY);
      contentStream.stroke();
      nextY -= table.getRowHeight();
    }

    final float tableYLength = table.getRowHeight() + (table.getRowHeight() * currentPageContent.length);
    final float tableBottomY = tableTopY - tableYLength;
    float nextX = table.getMargin();
    for (int i = 0; i < table.getNumberOfColumns(); i++) {
      contentStream.moveTo(nextX, tableTopY);
      contentStream.lineTo(nextX, tableBottomY);
      contentStream.stroke();
      nextX += table.getColumns().get(i).getWidth();
    }
    contentStream.moveTo(nextX, tableTopY);
    contentStream.lineTo(nextX, tableBottomY);
    contentStream.stroke();
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
    page.setRotation(table.isLandScape() ? 90 : 0);
    doc.addPage(page);
    return page;
  }

  private static PDPageContentStream generateContentStream(PDDocument doc, PDPage page, Table table, Invoice invoice)
      throws IOException {

    PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, false);
    contentStream.beginText();
    contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
    contentStream.setTextMatrix(Matrix.getRotateInstance(1.570796326795, 100, 60));
    contentStream.showText(String.valueOf(invoice.getSeller().getName()));
    contentStream.setLeading(20);
    contentStream.newLine();
    contentStream.showText(String.valueOf(invoice.getSeller().getStreetAndNumber()));
    contentStream.setLeading(20);
    contentStream.newLine();
    contentStream.showText(String.valueOf(invoice.getSeller().getPostalCode() + " " + invoice.getSeller().getLocation()));
    contentStream.setFont(PDType1Font.TIMES_BOLD, 30);
    contentStream.setTextMatrix(Matrix.getRotateInstance(1.570796326795, 50, 350));
    contentStream.showText("INVOICE");
    contentStream.setFont(PDType1Font.TIMES_BOLD, 17);
    contentStream.setTextMatrix(Matrix.getRotateInstance(1.570796326795, 180, 60));
    contentStream.showText("BILL TO");
    contentStream.setLeading(20);
    contentStream.newLine();
    contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
    contentStream.showText(String.valueOf(invoice.getBuyer().getName()));
    contentStream.setLeading(20);
    contentStream.newLine();
    contentStream.showText(String.valueOf(invoice.getBuyer().getStreetAndNumber()));
    contentStream.setLeading(20);
    contentStream.newLine();
    contentStream.showText(String.valueOf(invoice.getBuyer().getPostalCode() + " " + invoice.getBuyer().getLocation()));
    contentStream.endText();
    contentStream.close();

    if (table.isLandScape()) {
      contentStream.transform(new Matrix(0, 1, -1, 0, table.getPageSize().getWidth(), 0));
    }
    contentStream.setFont(table.getPdfTextFont(), table.getFontSize());
    return contentStream;
  }
}
