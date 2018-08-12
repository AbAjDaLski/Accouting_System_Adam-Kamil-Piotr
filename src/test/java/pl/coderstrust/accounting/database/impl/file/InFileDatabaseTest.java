package pl.coderstrust.accounting.database.impl.file;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.coderstrust.accounting.helpers.FileHelper;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@RunWith(JUnitParamsRunner.class)
public class InFileDatabaseTest {

  private static final String DATABASE_FILE_PATH = "test_database_invoices.txt";
  private static final String ID_FILE_PATH = "test_id_last_invoice.txt";
  private InFileDatabase database;

  public void cleanTestFiles() {
    File fileDatabaseInvoice = new File(DATABASE_FILE_PATH);
    File fileIdInvoices = new File(ID_FILE_PATH);
    if (!fileDatabaseInvoice.delete()) {
      System.out.println("Test file was not deleted.");
    }
    if (!fileIdInvoices.delete()) {
      System.out.println("Test file was not deleted.");
    }
  }

  @Test
  public void shouldSaveCorrectInvoice() throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId0();
      Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId1ForFindByDateRangeTest();

      //when
      database.saveInvoice(invoice1);
      database.saveInvoice(invoice2);

      //then
      String actual1 = FileHelper.readFromFile(DATABASE_FILE_PATH).get(0);
      String expected1 = "{\"id\":1"
          + ",\"identifier\":\"TestIdentifier0\""
          + ",\"issuedDate\":\"" + LocalDate.now() + "\""
          + ",\"buyer\":{\"name\":\"CompanyBuyerTest0\""
          + ",\"taxId\":\"000000000\""
          + ",\"streetAndNumber\":\"Test Buyer Street 0\""
          + ",\"postalCode\":\"00-000\",\"location\":\"TestLocationBuyer0\"}"
          + ",\"seller\":{\"name\":\"CompanySellerTest0\",\"taxId\":\"0000000000\""
          + ",\"streetAndNumber\":\"Test Seller Street 0\",\"postalCode\":\"00-000\""
          + ",\"location\":\"TestLocationSeller0\"},\"entries\":[{\"description\":\"Test Entry #1\""
          + ",\"price\":10,\"vat\":23},{\"description\":\"Test Entry #2\""
          + ",\"price\":10,\"vat\":8},{\"description\":\"Test Entry #3\""
          + ",\"price\":10,\"vat\":5},{\"description\":\"Test Entry #4\""
          + ",\"price\":10,\"vat\":0}]}";
      assertThat(actual1, is((expected1)));
      String actual2 = FileHelper.readFromFile(DATABASE_FILE_PATH).get(1);
      String expected2 = "{\"id\":2"
          + ",\"identifier\":\"TestIdentifier1\""
          + ",\"issuedDate\":\"" + LocalDate.now().minusDays(5) + "\""
          + ",\"buyer\":{\"name\":\"CompanyBuyerTest1\""
          + ",\"taxId\":\"1111111111\""
          + ",\"streetAndNumber\":\"Test Buyer Street 1\""
          + ",\"postalCode\":\"11-111\",\"location\":\"TestLocationBuyer1\"}"
          + ",\"seller\":{\"name\":\"CompanySellerTest1\",\"taxId\":\"1111111111\""
          + ",\"streetAndNumber\":\"Test Seller Street 1\",\"postalCode\":\"11-111\""
          + ",\"location\":\"TestLocationSeller1\"},\"entries\":[{\"description\":\"Test Entry #1\""
          + ",\"price\":10,\"vat\":23},{\"description\":\"Test Entry #2\""
          + ",\"price\":10,\"vat\":8},{\"description\":\"Test Entry #3\""
          + ",\"price\":10,\"vat\":5},{\"description\":\"Test Entry #4\""
          + ",\"price\":10,\"vat\":0}]}";
      assertThat(actual2, is((expected2)));
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldIncrementIdWhenInvoiceWithNullIdIsPassed()
      throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice = InvoiceHelper.getSampleInvoiceWithNullId();

      //when
      database.saveInvoice(invoice);
      database.saveInvoice(invoice);

      //then
      assertNotNull(FileHelper.readFromFile(DATABASE_FILE_PATH).get(0));
      assertNotNull(FileHelper.readFromFile(DATABASE_FILE_PATH).get(0));
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldThrowExceptionIfPathToIdFilePathIsNotExist() {
    try {
      // /given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      File fileIdInvoices = new File(ID_FILE_PATH);
      fileIdInvoices.setReadOnly();
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId0();
      database.saveInvoice(invoice1);
    } finally {
      cleanTestFiles();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDatabaseFilePathIsNull() {
    //given
    database = new InFileDatabase(null, ID_FILE_PATH);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfDatabaseFilePathIsEmpty() {
    //given
    database = new InFileDatabase("", ID_FILE_PATH);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfIdFilePathIsNull() {
    //given
    database = new InFileDatabase(DATABASE_FILE_PATH, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfIdFilePathhIsEmpty() {
    //given
    database = new InFileDatabase(DATABASE_FILE_PATH, "");
  }

  @Test
  @Parameters(method = "findParameters")
  public void shouldFindInvoiceByEveryParameter(Invoice searchParams) {
    //given
    try {
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
      database.saveInvoice(sampleInvoice);
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

      //when
      Collection<Invoice> result = database.find(searchParams, null, null);

      //then
      assertNotNull(result);
      assertFalse(result.isEmpty());
      Invoice actual = result.iterator().next();
      assertEquals(1, (int) actual.getId());
      assertThat(actual.getIdentifier(), CoreMatchers.is(sampleInvoice.getIdentifier()));
    } finally {
      cleanTestFiles();
    }
  }

  @SuppressWarnings("unused")
  private Object[] findParameters() {
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    return new Object[]{
        new Object[]{new Invoice(1, null, null, null, null, null)},
        new Object[]{new Invoice(null, sampleInvoice.getIdentifier(), null, null, null, null)},
        new Object[]{new Invoice(null, null, sampleInvoice.getIssuedDate(), null, null, null)},
        new Object[]{new Invoice(null, null, null, null, sampleInvoice.getSeller(), null)},
        new Object[]{new Invoice(null, null, null, null, null, sampleInvoice.getEntries())}
    };
  }

  @Test
  @Parameters(method = "giverParameters")
  public void shouldFindInvoicesInFileByBuyerOrSeller(Invoice searchParams) {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithId7();
      database.saveInvoice(sampleInvoice);
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId8_WithSameBuyerWhatId7());
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId9_WithSameSellerWhatId8());

      //when
      Collection<Invoice> result = database.find(searchParams, null, null);

      //then
      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertTrue(result.size() == 2);

    } finally {
      cleanTestFiles();
    }
  }

  private Object[] giverParameters() {
    Invoice sample1 = InvoiceHelper.getSampleInvoiceWithId7();
    Invoice sample2 = InvoiceHelper.getSampleInvoiceWithId8_WithSameBuyerWhatId7();
    Invoice sample3 = InvoiceHelper.getSampleInvoiceWithId9_WithSameSellerWhatId8();
    return new Object[]{
        new Object[]{
            new Invoice(null, null, null, sample1.getBuyer(), null, null)},
        new Object[]{
            new Invoice(null, null, null, null, sample2.getSeller(),
                null)}
    };
  }

  @Test
  public void shouldFindInvoiceByDateRange() {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithId2();
      database.saveInvoice(sampleInvoice);
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId1ForFindByDateRangeTest());
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId3ForFindByDateRangeTest());

      //when
      Collection<Invoice> result = database.find(null, sampleInvoice.getIssuedDate().minusDays(1),
          sampleInvoice.getIssuedDate().plusDays(1));

      //then
      assertNotNull(result);
      assertFalse(result.isEmpty());
      Invoice actual = result.iterator().next();
      assertEquals(1, (int) actual.getId());
      assertThat(actual.getIdentifier(), is(sampleInvoice.getIdentifier()));
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldFindInvoiceByIssuedDateFrom() {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
      database.saveInvoice(sampleInvoice);
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId1());
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId7());

      //when
      Collection<Invoice> result = database.find(null, sampleInvoice.getIssuedDate().minusDays(1),
          null);

      //then
      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertTrue(result.size() == 2);
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldFindInvoiceByIssuedDateTo() {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithNullId());
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId9_WithSameSellerWhatId8());

      //when
      Collection<Invoice> result = database.find(null, null,
          LocalDate.of(2018, Month.MAY, 2).plusDays(1));

      //then
      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertTrue(result.size() == 2);
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldGetAllInvoicesFromFile() {
    try {
      // /given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId3());
      database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId4());

      //when
      Collection<Invoice> result = database.getAll();

      //then
      assertNotNull(result);
      assertFalse(result.isEmpty());
      assertTrue(result.size() == 2);
    } finally {
      cleanTestFiles();
    }
  }
}
