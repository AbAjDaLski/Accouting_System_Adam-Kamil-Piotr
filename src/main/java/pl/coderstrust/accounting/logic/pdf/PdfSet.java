
package pl.coderstrust.accounting.logic.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;
import pl.coderstrust.accounting.model.Vat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PdfSet {

  private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
  private static final float MARGIN = 150;
  private static final boolean IS_LANDSCAPE = true;

  private static final PDFont TEXT_FONT = PDType1Font.HELVETICA;
  private static final float FONT_SIZE = 10;

  private static final float ROW_HEIGHT = 30;
  private static final float CELL_MARGIN = 4;

  public static Table createContent(Invoice invoice) throws IOException {

    invoice.getEntries().add(new InvoiceEntry("Computer", BigDecimal.TEN, Vat.REGULAR_23));
    ArrayList<Column> columns = new ArrayList<Column>();
    columns.add(new Column("description", 200));
    columns.add(new Column("price", 100));
    columns.add(new Column("vat", 80));

    String[][] content = {
        {invoice.getEntries().get(0).getDescription(),
            String.valueOf(invoice.getEntries().get(0).getPrice()),
            String.valueOf(invoice.getEntries().get(0).getVat()),
        },
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