package pl.coderstrust.accounting.logic.pdf;


import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.List;

public class CreatingTable {

  private Table table = new Table();

  public CreatingTable setHeight(float height) {
    table.setHeight(height);
    return this;
  }

  public CreatingTable setNumberOfRows(Integer numberOfRows) {
    table.setNumberOfRows(numberOfRows);
    return this;
  }

  public CreatingTable setRowHeight(float rowHeight) {
    table.setRowHeight(rowHeight);
    return this;
  }

  public CreatingTable setContent(String[][] content) {
    table.setContent(content);
    return this;
  }

  public CreatingTable setColumns(List<Column> columns) {
    table.setColumns(columns);
    return this;
  }

  public CreatingTable setCellMargin(float cellMargin) {
    table.setCellMargin(cellMargin);
    return this;
  }

  public CreatingTable setMargin(float margin) {
    table.setMargin(margin);
    return this;
  }

  public CreatingTable setPageSize(PDRectangle pageSize) {
    table.setPageSize(pageSize);
    return this;
  }

  public CreatingTable setLandscape(boolean landscape) {
    table.setLandscape(landscape);
    return this;
  }

  public CreatingTable setTextFont(PDFont textFont) {
    table.setPdfTextFont(textFont);
    return this;
  }

  public CreatingTable setFontSize(float fontSize) {
    table.setFontSize(fontSize);
    return this;
  }

  public Table build() {
    return table;
  }
}
