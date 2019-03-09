package pl.coderstrust.accounting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.validator.CompanyValidator;
import pl.coderstrust.accounting.model.validator.InvoiceEntryValidator;
import pl.coderstrust.accounting.model.validator.InvoiceValidator;
import pl.coderstrust.accounting.model.validator.exception.CompanyValidationException;
import pl.coderstrust.accounting.model.validator.exception.InvoiceValidationException;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InvoiceValidatorTest {

  @Mock
  private CompanyValidator companyValidatorMock;

  @Mock
  private InvoiceEntryValidator invoiceEntryValidatorMock;

  private InvoiceValidator invoiceValidator;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    invoiceValidator = new InvoiceValidator(invoiceEntryValidatorMock, companyValidatorMock);
  }

  @Test
  public void shouldCheckIfIdFieldGreaterThenOrEqualZero() {

    Invoice testInvoice = new Invoice(-9, "Sample Identifier", LocalDate.of(2017, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(),
        InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("Id"));
  }

  @Test
  public void shouldCheckIfIdFieldIsNotNull() {

    Invoice testInvoice = new Invoice(null, "Sample Identifier",
        LocalDate.of(2017, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(),
        InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("Id"));
  }

  @Test
  public void shouldCheckIfIssueDateFieldIsNotLaterThanToday() {

    Invoice testInvoice = new Invoice(9, "Sample Identifier", LocalDate.of(2019, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(),
        InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void shouldCheckIfIssueDateFieldIsNotNull() {

    Invoice testInvoice = new Invoice(9, "Sample Identifier", null,
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(),
        InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("date"));
  }

  @Test
  public void shouldCheckIfIdentifierFieldIsNotNull() {

    Invoice testInvoice = new Invoice(9, null, LocalDate.of(2017, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(),
        InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForSave(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("identifier"));
  }

  @Test
  public void shouldCheckIfBuyerFieldIsNotNull() {

    Invoice testInvoice = new Invoice(9, "Sample Identifier", LocalDate.of(2017, Month.JANUARY, 1),
        null, InvoiceHelper.getSampleSeller(), InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForSave(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("buyer"));
  }

  @Test
  public void shouldCheckIfSellerFieldIsNotNull() {

    Invoice testInvoice = new Invoice(9, "Sample Identifier", LocalDate.of(2017, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), null, InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("seller"));
  }

  @Test
  public void shouldCheckIfEntriesFieldIsNotNull() {

    Invoice testInvoice = new Invoice(9, "Sample Identifier", LocalDate.of(2017, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(), null);

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().getMessage().contains("entries"));
  }

  @Test
  public void shouldReturnMultipleValidationExceptions() {
    Invoice testInvoice = new Invoice(null, null, null, null, null);

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(6, result.size());
  }

  @Test
  public void shouldValidateCorrectInvoice() {
    Invoice testInvoice = InvoiceHelper.getSampleInvoiceWithId1();

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void shouldValidateBuyerAndSeller() {
    Company sampleBuyer = InvoiceHelper.getSampleBuyer();

    List<CompanyValidationException> buyerValidationExceptions = new ArrayList<>();
    buyerValidationExceptions.add(new CompanyValidationException("TEST_BUYER"));
    when(companyValidatorMock.validate(sampleBuyer)).thenReturn(buyerValidationExceptions);

    Company sampleSeller = InvoiceHelper.getSampleSeller();

    List<CompanyValidationException> sellerValidationExcetpions = new ArrayList<>();
    sellerValidationExcetpions.add(new CompanyValidationException("TEST_SELLER"));
    when(companyValidatorMock.validate(sampleSeller)).thenReturn(sellerValidationExcetpions);

    Invoice testInvoice = new Invoice(9, "Sample Identifier", LocalDate.of(2017, Month.JANUARY, 1),
        sampleBuyer, sampleSeller, InvoiceHelper.getSampleOneInvoiceEntryList());

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    Mockito.verify(companyValidatorMock).validate(sampleBuyer);
    Mockito.verify(companyValidatorMock).validate(sampleSeller);
    assertEquals(2, result.size());
  }

  @Test
  public void shouldValidateInvoiceEntries() {
    List<InvoiceEntry> sampleListOfEntries = InvoiceHelper.getSampleOneInvoiceEntryList();
    Invoice testInvoice = new Invoice(9, "Sample Identifier", LocalDate.of(2017, Month.JANUARY, 1),
        InvoiceHelper.getSampleBuyer(), InvoiceHelper.getSampleSeller(),
        sampleListOfEntries);

    List<String> validationExceptions = new ArrayList<>();
    validationExceptions.add("TEST_BUYER");
    when(invoiceEntryValidatorMock.validate(sampleListOfEntries.get(0)))
        .thenReturn(validationExceptions);

    Collection<InvoiceValidationException> result = invoiceValidator
        .validateInvoiceForUpdate(testInvoice);

    assertNotNull(result);
    assertEquals(1, result.size());
    Mockito.verify(invoiceEntryValidatorMock).validate(sampleListOfEntries.get(0));
    assertTrue(result.iterator().next().getMessage().contains("entry"));
  }
}