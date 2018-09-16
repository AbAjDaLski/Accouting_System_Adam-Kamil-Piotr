package pl.coderstrust.accounting.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import pl.coderstrust.accounting.model.validator.InvoiceEntryValidator;

import java.math.BigDecimal;
import java.util.Collection;

public class InvoiceEntryValidatorTest {

  private InvoiceEntryValidator invoiceEntryValidator;

  @Before
  public void setUp() {
    invoiceEntryValidator = new InvoiceEntryValidator();
  }

  @Test
  public void shouldCheckIfDescriptionFieldIsNotNull() {

    InvoiceEntry testInvoiceEntry = new InvoiceEntry(null, BigDecimal.TEN, Vat.REGULAR_23);

    Collection<String> result = invoiceEntryValidator
        .validate(testInvoiceEntry);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().contains("description"));
  }

  @Test
  public void shouldCheckIfPriceFieldIsNotNull() {

    InvoiceEntry testInvoiceEntry = new InvoiceEntry("description", null, Vat.REGULAR_23);

    Collection<String> result = invoiceEntryValidator
        .validate(testInvoiceEntry);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().contains("price"));
  }

  @Test
  public void shouldCheckIfVatFieldIsNotNull() {

    InvoiceEntry testInvoiceEntry = new InvoiceEntry("description", BigDecimal.TEN, null);

    Collection<String> result = invoiceEntryValidator
        .validate(testInvoiceEntry);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.iterator().next().contains("vat"));
  }
}