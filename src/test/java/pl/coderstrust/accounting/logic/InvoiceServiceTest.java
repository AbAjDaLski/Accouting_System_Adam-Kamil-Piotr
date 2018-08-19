package pl.coderstrust.accounting.logic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.helpers.InvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.validator.InvoiceValidator;
import pl.coderstrust.accounting.model.validator.exception.InvoiceValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(JUnitParamsRunner.class)
public class InvoiceServiceTest {

  @Mock
  private Database databaseMock;

  @Mock
  private InvoiceValidator invoiceValidatorMock;

  private InvoiceService invoiceService;

  @Before
  public void setUp() {
    initMocks(this);
    invoiceService = new InvoiceService(databaseMock, invoiceValidatorMock);
  }

  @Test
  public void shouldSaveCorrectInvoice() {

    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithId1();

    //when
    invoiceService.saveInvoice(sampleInvoice);

    //then
    verify(databaseMock, Mockito.times(1)).saveInvoice(sampleInvoice);
  }

  @Test
  public void shouldNotSaveIncorrectInvoice() {
    //given
    Invoice incorrectInvoice = InvoiceHelper.getSampleInvoiceWithIncorrectId();

    //when
    when(invoiceValidatorMock.validateInvoiceForSave(incorrectInvoice))
        .thenReturn(Collections.singletonList(new InvoiceValidationException("List Exceptions")));
    invoiceService.saveInvoice(incorrectInvoice);

    //then
    verify(databaseMock, Mockito.times(1)).saveInvoice(incorrectInvoice);
    assertTrue(!invoiceValidatorMock.validateInvoiceForSave(incorrectInvoice).isEmpty());
    verify(invoiceValidatorMock, Mockito.times(1)).validateInvoiceForSave(incorrectInvoice);
  }

  @Test
  public void shouldRemoveInvoice() throws IOException {
    //given
    when(databaseMock.get(anyInt())).thenReturn(java.util.Optional.of(InvoiceHelper.getSampleInvoiceWithId1()));

    //when
    invoiceService.removeInvoice(1);

    //then
    verify(databaseMock).removeInvoice(1);
  }

  @Test
  public void shouldThrowExceptionWhenInvoiceDoesNotExistWhenRemoving() throws IOException {
    //given
    when(databaseMock.get(anyInt())).thenReturn(null);

    //when
    try {
      invoiceService.removeInvoice(1);
      fail("should throw exception");
    } catch (IllegalArgumentException error) {

      //then
      assertTrue(error.getMessage().contains("An invoice with given ID : "));
    }
  }

  @Test
  @Parameters(method = "updateParameters")
  public void shouldUpdateOnlyGivenParam(Invoice invoice) throws IOException {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithId1();
    when(databaseMock.get(anyInt())).thenReturn(java.util.Optional.ofNullable(sampleInvoice));

    //when
    invoiceService.updateInvoice(invoice);

    //then
    verify(databaseMock)
        .updateInvoice(argThat(passed -> verifyArguments(passed, invoice, sampleInvoice)));
    verify(invoiceValidatorMock).validateInvoiceForUpdate(any());
  }

  private boolean verifyArguments(Invoice passedToDatabase, Invoice passedToMethod,
      Invoice fromDatabase) {
    if (!passedToDatabase.getIdentifier().equals(
        passedToMethod.getIdentifier() == null ? fromDatabase.getIdentifier()
            : passedToMethod.getIdentifier())) {
      return false;
    }
    if (!passedToDatabase.getIssuedDate().equals(
        passedToMethod.getIssuedDate() == null ? fromDatabase.getIssuedDate()
            : passedToMethod.getIssuedDate())) {
      return false;
    }
    if (!passedToDatabase.getBuyer().equals(
        passedToMethod.getBuyer() == null ? fromDatabase.getBuyer()
            : passedToMethod.getBuyer())) {
      return false;
    }
    if (!passedToDatabase.getSeller().equals(
        passedToMethod.getSeller() == null ? fromDatabase.getSeller()
            : passedToMethod.getSeller())) {
      return false;
    }
    return passedToDatabase.getEntries().equals(
        passedToMethod.getEntries() == null ? fromDatabase.getEntries()
            : passedToMethod.getEntries());
  }

  @Test
  public void shouldThrowExceptionWhenTryingToUpdateNullInvoice() {
    //given
    boolean thrown = false;

    //when
    try {
      invoiceService.updateInvoice(null);
    } catch (Exception ex) {
      thrown = true;
      assertTrue(ex instanceof IllegalArgumentException);
    }

    //then
    assertTrue(thrown);
    verify(databaseMock, never()).updateInvoice(any());
  }

  @Test
  public void shouldThrowExceptionWhenTryingToUpdateInvoiceWithNullId() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    boolean thrown = false;

    //when
    try {
      invoiceService.updateInvoice(sampleInvoice);
    } catch (Exception ex) {
      thrown = true;
      assertTrue(ex instanceof IllegalArgumentException);
    }

    //then
    assertTrue(thrown);
    verify(databaseMock, never()).updateInvoice(any());
  }

  @Test
  public void shouldThrowExceptionWhenTryingToUpdateInvoiceThatDoesNotExist() {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithId2();
    boolean thrown = false;

    //when
    try {
      invoiceService.updateInvoice(sampleInvoice);
    } catch (Exception ex) {
      thrown = true;
      assertTrue(ex instanceof IllegalArgumentException);
    }

    //then
    assertTrue(thrown);
    verify(databaseMock, never()).updateInvoice(any());
  }

  @Test
  public void shouldThrowExceptionWhenTryingToUpdateInvoiceThatDoesNotValidate()
      throws IOException {
    //given
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithId2();
    List<InvoiceValidationException> validationExceptions = new ArrayList<>();
    validationExceptions.add(new InvoiceValidationException("TEST"));
    when(invoiceValidatorMock.validateInvoiceForUpdate(any())).thenReturn(validationExceptions);
    when(databaseMock.get(anyInt())).thenReturn(java.util.Optional.of(InvoiceHelper.getSampleInvoiceWithId1()));
    boolean thrown = false;

    //when
    try {
      invoiceService.updateInvoice(sampleInvoice);
    } catch (Exception ex) {
      thrown = true;
      assertTrue(ex instanceof IllegalArgumentException);
      assertTrue(ex.getMessage().contains("TEST"));
    }

    //then
    assertTrue(thrown);
    verify(databaseMock, never()).updateInvoice(any());
  }

  @Test
  public void shouldDelegateFindingInvoiceToDatabase() {
    //given
    Invoice searchParams = new Invoice(null, null, null, null, null);
    LocalDate dateFrom = LocalDate.now();
    LocalDate dateTo = LocalDate.now();

    //when
    invoiceService.findInvoices(searchParams, dateFrom, dateTo);

    //then
    verify(databaseMock).find(searchParams, dateFrom, dateTo);
  }

  @Test
  public void shouldGetAllInvoices() {
    //given
    when(databaseMock.get(anyInt())).thenReturn(java.util.Optional.of(InvoiceHelper.getSampleInvoiceWithId1()));
    when(databaseMock.get(anyInt())).thenReturn(java.util.Optional.of(InvoiceHelper.getSampleInvoiceWithId2()));

    //when
    invoiceService.getAll();

    //then
    verify(databaseMock).getAll();
  }

  @SuppressWarnings("unused")
  private Object[] updateParameters() {
    Invoice sampleInvoice = InvoiceHelper.getSampleInvoiceWithNullId();
    return new Object[] {
        new Object[] {new Invoice(1, sampleInvoice.getIdentifier(), null, null, null, null)},
        new Object[] {new Invoice(1, null, sampleInvoice.getIssuedDate(), null, null, null)},
        new Object[] {new Invoice(1, null, null, sampleInvoice.getBuyer(), null, null)},
        new Object[] {new Invoice(1, null, null, null, sampleInvoice.getSeller(), null)},
        new Object[] {new Invoice(1, null, null, null, null, sampleInvoice.getEntries())}
    };
  }
}