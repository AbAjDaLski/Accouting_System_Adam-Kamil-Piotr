
package pl.coderstrust.accounting.logic.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PdfSet {

  private static final PDRectangle PAGE_SIZE = new PDRectangle(841.8898F, 595.27563F);
  private static final float MARGIN = 50;
  private static final boolean IS_LANDSCAPE = true;

  private static final PDFont TEXT_FONT = PDType1Font.HELVETICA;
  private static final float FONT_SIZE = 10;

  private static final float ROW_HEIGHT = 30;
  private static final float CELL_MARGIN = 4;

  public static Table createContent(Invoice invoice) throws IOException {

    String[][] content = new String[invoice.getEntries().size() + 1][6];

    ArrayList<Column> columns = createColumnHeaders();
    BigDecimal totalGrossValue = contentTableItems(invoice, content);
    totalValues(invoice, content, totalGrossValue);

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

  private static BigDecimal contentTableItems(Invoice invoice, String[][] content) {

    BigDecimal totalGrossValue = BigDecimal.ZERO;
    BigDecimal netPrice = BigDecimal.ZERO;
    BigDecimal valueVat = BigDecimal.ZERO;
    BigDecimal valueTax = BigDecimal.ZERO;

    for (int i = 0; i < invoice.getEntries().size(); i++) {
      String[] collectDataItem = new String[6];
      netPrice = invoice.getEntries().get(i).getPrice();

      collectDataItem[0] = Integer.toString(i + 1);
      collectDataItem[1] = invoice.getEntries().get(i).getDescription();
      collectDataItem[2] = netPrice.toString();
      valueVat = invoice.getEntries().get(i).getVat().getValue();
      collectDataItem[3] = valueVat.toString() + " %";
      BigDecimal hundred = new BigDecimal("100");
      valueTax = ((netPrice.multiply(valueVat))).divide(hundred);
      totalGrossValue = totalGrossValue.add(valueTax);
      collectDataItem[4] = String.valueOf(valueTax);
      collectDataItem[5] = String.valueOf(netPrice.add(valueTax));
      content[i] = collectDataItem;
    }
    return totalGrossValue;
  }

  private static BigDecimal getTotalNetValue(Invoice invoice) {
    BigDecimal totalNetPrice = BigDecimal.ZERO;

    for (InvoiceEntry entry : invoice.getEntries()) {
      totalNetPrice = totalNetPrice.add(entry.getPrice());
    }
    return totalNetPrice;
  }

  private static BigDecimal getTotalGrossValue(Invoice invoice) {
    BigDecimal grossPriceTotal = BigDecimal.ZERO;

    for (InvoiceEntry entry : invoice.getEntries()) {
      grossPriceTotal = grossPriceTotal.add(entry.getPrice().multiply(entry.getVat().getValue().divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE)));
    }
    return grossPriceTotal.setScale(2);
  }

  private static ArrayList<Column> createColumnHeaders() {

    ArrayList<Column> columns = new ArrayList<Column>();
    columns.add(new Column("no.", 20));
    columns.add(new Column("description", 150));
    columns.add(new Column("net price", 80));
    columns.add(new Column("vat", 40));
    columns.add(new Column("value vat", 80));
    columns.add(new Column("gross price", 80));
    return columns;
  }

  private static void totalValues(Invoice invoice, String[][] content, BigDecimal totalGrossValue) {
    content[content.length - 1][0] = null;
    content[content.length - 1][1] = "Total Value";
    content[content.length - 1][2] = getTotalNetValue(invoice).toString() + " PLN";
    content[content.length - 1][3] = null;
    content[content.length - 1][4] = totalGrossValue.toString() + " PLN";
    content[content.length - 1][5] = getTotalGrossValue(invoice).toString() + " PLN";
  }
}