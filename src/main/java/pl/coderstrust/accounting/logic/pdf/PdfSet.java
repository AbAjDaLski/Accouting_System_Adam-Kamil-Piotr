
package pl.coderstrust.accounting.logic.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;

public class PdfSet {

  private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
  private static final float MARGIN = 20;
  private static final boolean IS_LANDSCAPE = true;

  private static final PDFont TEXT_FONT = PDType1Font.HELVETICA;
  private static final float FONT_SIZE = 10;

  private static final float ROW_HEIGHT = 15;
  private static final float CELL_MARGIN = 2;

  public static void main(String[] args) throws IOException {
    new PdfWithTableGenerate().generatePdf(createContent());
  }

  private static Table createContent() {

    ArrayList<Column> columns = new ArrayList<Column>();
    columns.add(new Column("id", 100));
    columns.add(new Column("identifier", 100));
    columns.add(new Column("issuedDate", 230));
    columns.add(new Column("buyer", 43));
    columns.add(new Column("seller", 50));
    columns.add(new Column("entries", 80));

    String[][] content = {
        {"id", "identifier", "issuedDate", "buyer", "seller", "entries"},
        {"id", "identifier", "issuedDate", "buyer", "seller", "entries"},
        {"id", "identifier", "issuedDate", "buyer", "seller", "entries"}
    };

    float tableHeight =
        IS_LANDSCAPE ? PAGE_SIZE.getWidth() - (2 * MARGIN) : PAGE_SIZE.getHeight() - (2 * MARGIN);

    Table table = new TableBuilder()
        .setCellMargin(CELL_MARGIN)
        .setColumns(columns)
        .setContent(content)
        .setHeight(tableHeight)
        .setNumberOfRows(content.length)
        .setRowHeight(ROW_HEIGHT)
        .setMargin(MARGIN)
        .setPageSize(PAGE_SIZE)
        .setLandscape(IS_LANDSCAPE)
        .setTextFont(TEXT_FONT)
        .setFontSize(FONT_SIZE)
        .build();
    return table;
  }
}