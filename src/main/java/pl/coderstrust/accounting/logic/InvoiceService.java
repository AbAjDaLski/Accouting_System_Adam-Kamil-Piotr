package pl.coderstrust.accounting.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.database.InMemoryDatabase;
import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;
import pl.coderstrust.accounting.model.validator.InvoiceValidator;
import pl.coderstrust.accounting.model.validator.exception.InvoiceValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

  public static final String INVOICE_TO_UPDATE_CANNOT_BE_NULL_MESSAGE = "Invoice to update cannot"
      + " be null";
  public static final String INVOICE_TO_UPDATE_MUST_HAVE_A_VALID_ID_MESSAGE = "Invoice to update"
      + " must have a valid ID";
  public static final String CANNOT_UPDATE_AN_INVOICE_WITH_GIVEN_ID_MESSAGE = "Cannot update: An"
      + " invoice with given ID : ";
  public static final String CANNOT_REMOVE_AN_INVOICE_WITH_GIVEN_ID_MESSAGE = "Cannot remove: An"
      + " invoice with given ID : ";
  private static Logger logger = LoggerFactory.getLogger(InMemoryDatabase.class);

  private final Database database;
  private final InvoiceValidator invoiceValidator;

  public InvoiceService(Database database,
      InvoiceValidator invoiceValidator) {
    this.database = database;
    this.invoiceValidator = invoiceValidator;
  }

  public int saveInvoice(Invoice invoice) {
    return database.saveInvoice(invoice);
  }

  public int updateInvoice(Invoice invoice) {
    if (invoice == null) {
      logger.error(INVOICE_TO_UPDATE_CANNOT_BE_NULL_MESSAGE);
      throw new IllegalArgumentException(INVOICE_TO_UPDATE_CANNOT_BE_NULL_MESSAGE);
    }
    if (invoice.getId() == null) {
      logger.error(INVOICE_TO_UPDATE_MUST_HAVE_A_VALID_ID_MESSAGE);
      throw new IllegalArgumentException(INVOICE_TO_UPDATE_MUST_HAVE_A_VALID_ID_MESSAGE);
    }
    Optional<Invoice> current = database.get(invoice.getId());
    if (!current.isPresent()) {
      logger.error(CANNOT_UPDATE_AN_INVOICE_WITH_GIVEN_ID_MESSAGE + invoice.getId() + " doesn't "
          + "exist");
      throw new IllegalArgumentException(
          CANNOT_UPDATE_AN_INVOICE_WITH_GIVEN_ID_MESSAGE + invoice.getId() + " doesn't exist");
    }
    Invoice invoiceToUpdate = prepareInvoiceToUpdate(invoice, current.get());
    Collection<InvoiceValidationException> validationExceptions = invoiceValidator
        .validateInvoiceForUpdate(invoiceToUpdate);
    if (validationExceptions.isEmpty()) {
      database.updateInvoice(invoiceToUpdate);
    } else {
      StringBuilder sb = new StringBuilder("The updated invoice is not correct: ");
      for (InvoiceValidationException exception : validationExceptions) {
        sb.append(exception.getMessage());
        sb.append("\n");
      }
      logger.error(sb.toString());
      throw new IllegalArgumentException(sb.toString());
    }
    return invoice.getId();
  }

  private Invoice prepareInvoiceToUpdate(Invoice invoice, Invoice current) {
    String identifier = invoice.getIdentifier() == null ? current.getIdentifier() : invoice
        .getIdentifier();
    LocalDate issuedDate =
        invoice.getIssuedDate() == null ? current.getIssuedDate() : invoice.getIssuedDate();
    List<InvoiceEntry> entries =
        invoice.getEntries() == null ? current.getEntries() : invoice.getEntries();
    Company buyer = invoice.getBuyer() == null ? current.getBuyer() : invoice.getBuyer();
    Company seller = invoice.getSeller() == null ? current.getSeller() : invoice.getSeller();
    return new Invoice(invoice.getId(), identifier, issuedDate, buyer, seller,
        entries);
  }

  public void removeInvoice(int id) {
    if (database.get(id) != null) {
      database.removeInvoice(id);
    } else {
      logger.error(CANNOT_REMOVE_AN_INVOICE_WITH_GIVEN_ID_MESSAGE + id + " doesn't exist");
      throw new IllegalArgumentException(
          CANNOT_REMOVE_AN_INVOICE_WITH_GIVEN_ID_MESSAGE + id + " doesn't exist");
    }
  }

  public Optional<Invoice> findById(int id) {
    return database.get(id);
  }

  public Collection<Invoice> findInvoices(Invoice searchParams, LocalDate issuedDateFrom,
      LocalDate issuedDateTo) {
    return database.find(searchParams, issuedDateFrom, issuedDateTo);
  }

  public Collection<Invoice> getAll() {
    return database.getAll();
  }
}
