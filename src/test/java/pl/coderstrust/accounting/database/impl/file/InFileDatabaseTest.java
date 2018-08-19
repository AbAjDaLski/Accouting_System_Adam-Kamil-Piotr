package pl.coderstrust.accounting.database.impl.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
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
import java.util.List;
import java.util.Optional;

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
  public void shouldThrowExceptionIfIdFilePathIsEmpty() {
    //given
    database = new InFileDatabase(DATABASE_FILE_PATH, "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionIfNoInvoiceWithGivenIdToRemove() throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();

      //when
      database.saveInvoice(invoice1);
      database.removeInvoice(5);
    } finally {
      cleanTestFiles();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionIfNoInvoiceToRemove() throws IOException {
    //given
    database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);

    //when
    database.removeInvoice(5);
  }

  @Test
  public void shouldUpdateInvoice() {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice = InvoiceHelper.getSampleInvoiceWithId1();
      String newIdentifier = "ABC";
      LocalDate newDate = LocalDate.now();
      Invoice updatedInvoice = new Invoice(1, newIdentifier, newDate,
          InvoiceHelper.getSampleBuyerCompany(), InvoiceHelper.getSampleSellerCompany(),
          InvoiceHelper.getSampleFourInvoiceEntriesList());

      //when
      database.saveInvoice(invoice);
      database.updateInvoice(updatedInvoice);

      //then
      Optional<Invoice> actual = database.get(1);
      assertThat(actual.get().getIdentifier(), CoreMatchers.is(newIdentifier));
      assertThat(actual.get().getIssuedDate(), CoreMatchers.is(newDate));
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldRemoveInvoiceWithGivenIdWhenOneInvoiceInList() throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();

      //when
      database.saveInvoice(invoice1);
      database.removeInvoice(1);

      //then
      assertTrue(FileHelper.readFromFile(DATABASE_FILE_PATH).isEmpty());
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldRemoveInvoiceWithGivenIdWhenManyInvoicesInList() throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();
      Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId2();
      Invoice invoice3 = InvoiceHelper.getSampleInvoiceWithId3();

      //when
      database.saveInvoice(invoice1);
      database.saveInvoice(invoice2);
      database.saveInvoice(invoice3);
      database.removeInvoice(1);

      //then
      List afterRemoveLines = FileHelper.readFromFile(DATABASE_FILE_PATH);
      String actual1 = afterRemoveLines.get(0).toString();
      String expected1 = "{\"id\":2"
          + ",\"identifier\":\"TestIdentifier2\""
          + ",\"issuedDate\":\"" + LocalDate.now() + "\""
          + ",\"buyer\":{\"name\":\"CompanyBuyerTest2\""
          + ",\"taxId\":\"2222222222\""
          + ",\"streetAndNumber\":\"Test Buyer Street 2\""
          + ",\"postalCode\":\"22-222\",\"location\":\"TestLocationBuyer2\"}"
          + ",\"seller\":{\"name\":\"CompanySellerTest2\",\"taxId\":\"2222222222\""
          + ",\"streetAndNumber\":\"Test Seller Street 2\",\"postalCode\":\"22-222\""
          + ",\"location\":\"TestLocationSeller2\"},\"entries\":[{\"description\":\"Test Entry #2\""
          + ",\"price\":10,\"vat\":8}]}";
      assertThat(actual1, is(equalTo(expected1)));
      String actual2 = afterRemoveLines.get(1).toString();
      String expected2 = "{\"id\":3"
          + ",\"identifier\":\"TestIdentifier3\""
          + ",\"issuedDate\":\"" + LocalDate.now() + "\""
          + ",\"buyer\":{\"name\":\"CompanyBuyerTest3\""
          + ",\"taxId\":\"3333333333\""
          + ",\"streetAndNumber\":\"Test Buyer Street 3\""
          + ",\"postalCode\":\"33-333\",\"location\":\"TestLocationBuyer3\"}"
          + ",\"seller\":{\"name\":\"CompanySellerTest3\",\"taxId\":\"3333333333\""
          + ",\"streetAndNumber\":\"Test Seller Street 3\",\"postalCode\":\"33-333\""
          + ",\"location\":\"TestLocationSeller3\"},\"entries\":[{\"description\":\"Test Entry #3\""
          + ",\"price\":10,\"vat\":8}]}";
      assertThat(actual2, is(equalTo(expected2)));
    } finally {
      cleanTestFiles();
    }
  }

  @Test
  public void shouldGetInvoiceById() throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();
      Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId2();
      Invoice invoice3 = InvoiceHelper.getSampleInvoiceWithId3();

      //when
      database.saveInvoice(invoice1);
      database.saveInvoice(invoice2);
      database.saveInvoice(invoice3);

      //then
      Invoice actual1 = invoice1;
      Optional<Invoice> expected1 = database.get(1);
      invoicesAreIdentical(actual1, expected1.get());
      Invoice actual2 = invoice3;
      Optional<Invoice> expected2 = database.get(3);
      invoicesAreIdentical(actual2, expected2.get());
    } finally {
      cleanTestFiles();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionIfNoInvoiceWithGivenIdToGet() throws IOException {
    try {
      //given
      database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);
      Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();

      //when
      database.saveInvoice(invoice1);
      database.get(5);
    } finally {
      cleanTestFiles();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionIfNoInvoiceToGet() throws IOException {
    //given
    database = new InFileDatabase(DATABASE_FILE_PATH, ID_FILE_PATH);

    //when
    database.get(5);
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
    return new Object[] {
        new Object[] {new Invoice(1, null, null, null, null, null)},
        new Object[] {new Invoice(null, sampleInvoice.getIdentifier(), null, null, null, null)},
        new Object[] {new Invoice(null, null, sampleInvoice.getIssuedDate(), null, null, null)},
        new Object[] {new Invoice(null, null, null, sampleInvoice.getBuyer(), null, null)},
        new Object[] {new Invoice(null, null, null, null, sampleInvoice.getSeller(), null)},
        new Object[] {new Invoice(null, null, null, null, null, sampleInvoice.getEntries())}
    };
  }

  @Test
  @Parameters(method = "giveParameters")
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

  private Object[] giveParameters() {
    Invoice sample1 = InvoiceHelper.getSampleInvoiceWithId7();
    Invoice sample2 = InvoiceHelper.getSampleInvoiceWithId8_WithSameBuyerWhatId7();
    Invoice sample3 = InvoiceHelper.getSampleInvoiceWithId9_WithSameSellerWhatId8();
    return new Object[] {
        new Object[] {
            new Invoice(null, null, null, sample1.getBuyer(), null, null)},
        new Object[] {
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

  public static void invoicesAreIdentical(Invoice invoice, Invoice invoiceToCompare) {
    MatcherAssert.assertThat(invoice.getId(), CoreMatchers.is(invoiceToCompare.getId()));
    MatcherAssert
        .assertThat(invoice.getIdentifier(), CoreMatchers.is(invoiceToCompare.getIdentifier()));
    MatcherAssert
        .assertThat(invoice.getIssuedDate(), CoreMatchers.is(invoiceToCompare.getIssuedDate()));
    MatcherAssert.assertThat(invoice.getBuyer(), CoreMatchers.is(invoiceToCompare.getBuyer()));
    MatcherAssert.assertThat(invoice.getSeller(), CoreMatchers.is(invoiceToCompare.getSeller()));
    assertTrue(invoice.getEntries().equals(invoiceToCompare.getEntries()));
  }
}