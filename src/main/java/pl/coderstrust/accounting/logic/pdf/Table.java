package pl.coderstrust.accounting.logic.pdf;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.List;

public class Table {

  private PDRectangle pageSize;
  private float margin;
  private float height;
  private float rowHeight;
  private boolean isLandScape;

  private List<Column> columns;
  private Integer numberOfRows;
  private float cellMargin;
  private String [][] content;

  private PDFont pdfTextFont;
  private float fontSize;

  public Table() {
  }

  public Integer getNumberOfColumns() {
    return this.getColumns().size();
  }

  public String[] getColumnsNames() {
    String[] columnNames = new String[getNumberOfColumns()];
    for (int i = 0; i < getNumberOfColumns() - 1; i++) {
      columnNames[i] = columns.get(i).getName();
    }
    return columnNames;
  }

  public float getWidth() {
    float tableWidth = 0f;
    for (Column column : columns) {
      tableWidth += column.getWidth();
    }
    return tableWidth;
  }

  public PDRectangle getPageSize() {
    return pageSize;
  }

  public void setPageSize(PDRectangle pageSize) {
    this.pageSize = pageSize;
  }

  public float getMargin() {
    return margin;
  }

  public void setMargin(float margin) {
    this.margin = margin;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public float getRowHeight() {
    return rowHeight;
  }

  public void setRowHeight(float rowHeight) {
    this.rowHeight = rowHeight;
  }

  public boolean isLandScape() {
    return isLandScape;
  }

  public void setLandScape(boolean landScape) {
    isLandScape = landScape;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public void setColumns(List<Column> columns) {
    this.columns = columns;
  }

  public Integer getNumberOfRows() {
    return numberOfRows;
  }

  public void setNumberOfRows(Integer numberOfRows) {
    this.numberOfRows = numberOfRows;
  }

  public float getCellMargin() {
    return cellMargin;
  }

  public void setCellMargin(float cellMargin) {
    this.cellMargin = cellMargin;
  }

  public String[][] getContent() {
    return content;
  }

  public void setContent(String[][] content) {
    this.content = content;
  }

  public PDFont getPdfTextFont() {
    return pdfTextFont;
  }

  public void setPdfTextFont(PDFont pdfTextFont) {
    this.pdfTextFont = pdfTextFont;
  }

  public float getFontSize() {
    return fontSize;
  }

  public void setFontSize(float fontSize) {
    this.fontSize = fontSize;
  }
}
