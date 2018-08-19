package pl.coderstrust.accounting.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.coderstrust.accounting.database.InMemoryDatabase;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.logic.InvoiceService;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.validator.CompanyValidator;
import pl.coderstrust.accounting.model.validator.InvoiceEntryValidator;
import pl.coderstrust.accounting.model.validator.InvoiceValidator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@RunWith(JUnitParamsRunner.class)
public class InvoiceServiceAndDatabaseTest {

  private InMemoryDatabase database;
  private InvoiceService invoiceService;
  private InvoiceValidator invoiceValidator;
  private InvoiceEntryValidator invoiceEntryValidator;
  private CompanyValidator companyValidator;

  @Before
  public void init() {
    invoiceEntryValidator = new InvoiceEntryValidator();
    companyValidator = new CompanyValidator();
    invoiceValidator = new InvoiceValidator(invoiceEntryValidator, companyValidator);
    database = new InMemoryDatabase();
    invoiceService = new InvoiceService(database, invoiceValidator);
  }

  @Test
  public void shouldSaveInvoiceToDatabaseViaInvoiceBook() {
    //given
    Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();
    Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId2();
    Invoice invoice3 = InvoiceHelper.getSampleInvoiceWithId3();
    Invoice invoice4 = InvoiceHelper.getSampleInvoiceWithId4();

    //when
    invoiceService.saveInvoice(invoice1);
    invoiceService.saveInvoice(invoice2);
    invoiceService.saveInvoice(invoice3);
    invoiceService.saveInvoice(invoice4);

    //then
    assertThat(database.get(1).get().getIdentifier(), is(invoice1.getIdentifier()));
    assertThat(database.get(2).get().getIdentifier(), is(invoice2.getIdentifier()));
    assertThat(database.get(3).get().getIdentifier(), is(invoice3.getIdentifier()));
    assertThat(database.get(4).get().getIdentifier(), is(invoice4.getIdentifier()));
  }

  @Test
  public void shouldIncrementIdWhenInvoiceWithNullIdIsPassed() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithNullId();

    //when
    invoiceService.saveInvoice(invoice);
    invoiceService.saveInvoice(invoice);
    invoiceService.saveInvoice(invoice);

    //then
    assertThat(database.get(1).get().getId(), is(1));
    assertThat(database.get(2).get().getId(), is(2));
    assertThat(database.get(3).get().getId(), is(3));
  }

  @Test
  public void shouldAdd3AndRemove2SampleInvoices() {
    //given
    Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();
    Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId2();
    Invoice invoice3 = InvoiceHelper.getSampleInvoiceWithId3();

    //when
    invoiceService.saveInvoice(invoice1);
    invoiceService.saveInvoice(invoice2);
    invoiceService.saveInvoice(invoice3);
    invoiceService.removeInvoice(1);
    invoiceService.removeInvoice(2);

    //then
    assertEquals(Optional.empty(), database.get(1));
    assertEquals(Optional.empty(), database.get(2));
    assertThat(database.get(3).get().getId(), is(database.get(3).get().getId()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenTryingToUpdateWithNullId() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithNullId();

    //when
    invoiceService.updateInvoice(invoice);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenTryingToUpdateInvoiceThatDoesNotExist() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithId1();

    //when
    invoiceService.updateInvoice(invoice);
  }

  @Test
  @Parameters(method = "findParameters")
  public void shouldFindInvoiceByEveryParameter(Invoice searchParams) {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    invoiceService.saveInvoice(sampleInvoice);
    invoiceService.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = invoiceService.findInvoices(searchParams, null, null);

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    Invoice actual = result.iterator().next();
    assertEquals(1, (int) actual.getId());
    assertThat(actual.getIdentifier(), is(sampleInvoice.getIdentifier()));
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
  public void shouldFindInvoiceByDateRange() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    invoiceService.saveInvoice(sampleInvoice);
    invoiceService.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = invoiceService
        .findInvoices(null, sampleInvoice.getIssuedDate().minusDays(1),
            sampleInvoice.getIssuedDate().plusDays(1));

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    Invoice actual = result.iterator().next();
    assertEquals(1, (int) actual.getId());
    assertThat(actual.getIdentifier(), is(sampleInvoice.getIdentifier()));
  }

  @Test
  public void shouldFindInvoiceByIssuedDateFrom() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    invoiceService.saveInvoice(sampleInvoice);
    invoiceService.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = invoiceService
        .findInvoices(null, sampleInvoice.getIssuedDate().minusDays(1),
            null);

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    Invoice actual = result.iterator().next();
    assertEquals(1, (int) actual.getId());
    assertThat(actual.getIdentifier(), is(sampleInvoice.getIdentifier()));
  }

  @Test
  public void shouldFindInvoiceByIssuedDateTo() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    invoiceService.saveInvoice(sampleInvoice);
    invoiceService.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = invoiceService.findInvoices(null, null,
        LocalDate.of(2018, 3, 20));

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    Invoice actual = result.iterator().next();
    assertEquals(2, (int) actual.getId());
  }

  @Test
  public void shouldUpdateInvoice() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithId2();
    String newIdentifier = "ABC";
    LocalDate newDate = LocalDate.now();
    Invoice updatedInvoice = new Invoice(1, newIdentifier, newDate,
        InvoiceHelper.getSampleBuyerCompany(), InvoiceHelper.getSampleSellerCompany(),
        InvoiceHelper.getSampleOneInvoiceEntryList());

    //when
    invoiceService.saveInvoice(invoice);
    invoiceService.updateInvoice(updatedInvoice);

    //then
    Optional<Invoice> actual = database.get(1);
    assertThat(actual.get().getIdentifier(), is(newIdentifier));
    assertThat(actual.get().getIssuedDate(), is(newDate));
  }

  @Test
  public void shouldGetAllInvoices() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    invoiceService.saveInvoice(sampleInvoice);
    invoiceService.saveInvoice(InvoiceHelper.getSampleInvoiceWithId3());
    invoiceService.saveInvoice(InvoiceHelper.getSampleInvoiceWithId4());

    //when
    Collection<Invoice> result = database.getAll();

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    Invoice actual = result.iterator().next();
    assertEquals(1, (int) actual.getId());
    assertThat(actual.getIdentifier(), is(sampleInvoice.getIdentifier()));
  }
}
