package pl.coderstrust.accounting.database;

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
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@RunWith(JUnitParamsRunner.class)
public class InMemoryDatabaseTest {

  private InMemoryDatabase database;

  @Before
  public void init() {
    database = new InMemoryDatabase();
  }

  @Test
  public void shouldIncrementIdWhenInvoiceWithNullIdIsPassed() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithNullId();

    //when
    database.saveInvoice(invoice);
    database.saveInvoice(invoice);
    database.saveInvoice(invoice);

    //then
    assertNotNull(database.get(1));
    assertNotNull(database.get(2));
    assertNotNull(database.get(3));
  }

  @Test
  public void shouldSaveSampleInvoices() {
    //given
    Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();
    Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId2();
    Invoice invoice3 = InvoiceHelper.getSampleInvoiceWithId3();
    Invoice invoice4 = InvoiceHelper.getSampleInvoiceWithId4();

    //when
    database.saveInvoice(invoice1);
    database.saveInvoice(invoice2);
    database.saveInvoice(invoice3);
    database.saveInvoice(invoice4);

    //then
    assertThat(database.get(1).get().getIdentifier(), is(invoice1.getIdentifier()));
    assertThat(database.get(2).get().getIdentifier(), is(invoice2.getIdentifier()));
    assertThat(database.get(3).get().getIdentifier(), is(invoice3.getIdentifier()));
    assertThat(database.get(4).get().getIdentifier(), is(invoice4.getIdentifier()));
  }

  @Test
  public void shouldAdd3AndRemove2SampleInvoices() {
    //given
    Invoice invoice1 = InvoiceHelper.getSampleInvoiceWithId1();
    Invoice invoice2 = InvoiceHelper.getSampleInvoiceWithId2();
    Invoice invoice3 = InvoiceHelper.getSampleInvoiceWithId3();

    //when
    database.saveInvoice(invoice1);
    database.saveInvoice(invoice2);
    database.saveInvoice(invoice3);
    database.removeInvoice(1);
    database.removeInvoice(2);

    //then
    assertEquals(Optional.empty(), database.get(1));
    assertEquals(Optional.empty(), database.get(2));
    assertNotNull(database.get(3));
  }


  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenTryingToUpdateWithNullId() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithNullId();

    //when
    database.updateInvoice(invoice);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionWhenTryingToUpdateInvoiceThatDoesNotExist() {
    //given
    Invoice invoice = InvoiceHelper.getSampleInvoiceWithId1();

    //when
    database.updateInvoice(invoice);
  }

  @Test
  public void shouldUpdateInvoice() {
    //given
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
    assertThat(actual.get().getIdentifier(), is(newIdentifier));
    assertThat(actual.get().getIssuedDate(), is(newDate));
  }

  @Test
  @Parameters(method = "findParameters")
  public void shouldFindInvoiceByEveryParameter(Invoice searchParams) {
    //given
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
    database.saveInvoice(sampleInvoice);
    database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = database.find(null, sampleInvoice.getIssuedDate().minusDays(1),
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
    database.saveInvoice(sampleInvoice);
    database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = database.find(null, sampleInvoice.getIssuedDate().minusDays(1),
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
    database.saveInvoice(sampleInvoice);
    database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId5());

    //when
    Collection<Invoice> result = database.find(null, null,
        LocalDate.of(2018, 3, 20));

    //then
    assertNotNull(result);
    assertFalse(result.isEmpty());
    Invoice actual = result.iterator().next();
    assertEquals(2, (int) actual.getId());
  }

  @Test
  public void shouldGetAllInvoices() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    database.saveInvoice(sampleInvoice);
    database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId3());
    database.saveInvoice(InvoiceHelper.getSampleInvoiceWithId4());

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