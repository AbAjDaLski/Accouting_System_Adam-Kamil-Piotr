package pl.coderstrust.accounting.model.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.validator.exception.InvoiceValidationException;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Service
public class InvoiceValidator {

  private final InvoiceEntryValidator invoiceEntryValidator;

  private final CompanyValidator companyValidator;

  private static Logger logger = LoggerFactory.getLogger(InvoiceEntryValidator.class);

  List<InvoiceValidationException> validationExceptions = new LinkedList<>();

  public InvoiceValidator(InvoiceEntryValidator invoiceEntryValidator,
      CompanyValidator companyValidator) {
    this.invoiceEntryValidator = invoiceEntryValidator;
    this.companyValidator = companyValidator;
  }

  public List<InvoiceValidationException> validateInvoiceForUpdate(Invoice invoice) {
    return validate(invoice, true);
  }

  public List<InvoiceValidationException> validateInvoiceForSave(Invoice invoice) {
    return validate(invoice, false);
  }

  private List<InvoiceValidationException> validate(Invoice invoice, boolean checkForId) {
    List<InvoiceValidationException> validationExceptions = new LinkedList<>();
    if (checkForId) {
      if (invoice.getId() == null && checkForId) {
        logger.error("Expected not empty Id");
        validationExceptions.add(new InvoiceValidationException("Expected not empty Id"));
      } else {
        if (invoice.getId() < 0) {
          logger.error("Expected Id to be greater than 0, got: " + String.valueOf(invoice.getId()));
          validationExceptions.add(new InvoiceValidationException(
              "Expected Id to be greater than 0, got: " + String.valueOf(invoice.getId())));
        }
      }
    }
    if (invoice.getIssuedDate() == null) {
      logger.error("Expected not empty issue date");
      validationExceptions
          .add(new InvoiceValidationException("Expected not empty issue date"));
    } else {
      if (invoice.getIssuedDate().isAfter(LocalDate.now())) {
        logger.error("Expected issue date not greater than today date, got: " + String
            .valueOf(invoice.getIssuedDate()));
        validationExceptions.add(new InvoiceValidationException(
            "Expected issue date not greater than today date, got: " + String
                .valueOf(invoice.getIssuedDate())));
      }
    }
    if (invoice.getIdentifier() == null) {
      logger.error(("Expected not empty invoice identifier"));
      validationExceptions.add(
          new InvoiceValidationException("Expected not empty invoice identifier"));
    }
    if (invoice.getEntries() == null) {
      logger.error(("Expected not empty invoice identifier"));
      validationExceptions.add(new InvoiceValidationException(
          "Expected not empty invoice entries"));
    } else {
      invoice.getEntries().forEach(invoiceEntry -> invoiceEntryValidator.validate(invoiceEntry)
          .forEach(validationException -> validationExceptions.add(new InvoiceValidationException(
              "Validation of entry failed, message: " + validationException))));
    }
    if (invoice.getBuyer() == null) {
      logger.error(("Expected not empty invoice identifier"));
      validationExceptions.add(new InvoiceValidationException("Expected not empty buyer"));
    } else {
      companyValidator.validate(invoice.getBuyer())
          .forEach((validationException -> validationExceptions.add(new InvoiceValidationException(
              "Validation of buyer failed, message: " + validationException.getMessage()))));
    }
    if (invoice.getSeller() == null) {
      logger.error("Expected not empty seller");
      validationExceptions.add(new InvoiceValidationException("Expected not empty seller"));
    } else {
      companyValidator.validate(invoice.getSeller())
          .forEach((validationException -> validationExceptions.add(new InvoiceValidationException(
              "Validation of seller failed, message: " + validationException.getMessage()))));
    }
    return validationExceptions;
  }
}