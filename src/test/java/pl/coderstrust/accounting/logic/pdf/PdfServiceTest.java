package pl.coderstrust.accounting.logic.pdf;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class PdfServiceTest {

  @Test
  public void shouldCheckGenerateRightPdfFile() throws IOException {

    //given
    Invoice givenInvoice = InvoiceHelper.getSampleInvoiceWithId41();
    Table givenTable = PdfSet.createContent(givenInvoice);

    //when
    ByteArrayOutputStream invoiceActualPdf = new PdfService().generatePdf(givenTable, givenInvoice, "outputInvoicePdf.pdf");

    //then
    Assert.assertTrue(FileUtils.contentEquals(new File("invoice.pdf"), new File("outputInvoicePdf.pdf")));
  }
}