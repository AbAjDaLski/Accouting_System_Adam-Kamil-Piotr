package pl.coderstrust.accounting.logic.pdf;

import com.testautomationguru.utility.PDFUtil;
import org.junit.Assert;
import org.junit.Test;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;

import java.io.IOException;

public class PdfServiceTest {

  @Test
  public void shouldCheckGenerateRightPdfFile() throws IOException {

    //given
    PDFUtil pdfUtil = new PDFUtil();
    Invoice givenInvoice = InvoiceHelper.getSampleInvoiceWithId41();
    Table givenTable = PdfSet.createContent(givenInvoice);

    //when
    new PdfService().generatePdf(givenTable, givenInvoice, "src\\test\\resources\\outputInvoicePdf.pdf");

    //then
    Assert.assertTrue(pdfUtil.compare("invoice.pdf", "src\\test\\resources\\outputInvoicePdf.pdf"));
  }
}