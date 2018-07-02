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
  private static final String expectedNotEmptyId = "Expected not empty Id";
  private static final String idShouldBeGratedThanZero = "Expected Id to be greater than 0, got: ";
  private static final String expectedNotEmptyIssueDate = "Expected not empty issue date";
  private static final String expectedDateNotGraterThanToday = "Expected issue date not greater"
      + " than today date, got: ";
  private static final String expNotEmptyInvIdentifier = "Expected not empty invoice identifier";

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
        logger.error(expectedNotEmptyId);
        validationExceptions.add(new InvoiceValidationException(expectedNotEmptyId));
      } else {
        if (invoice.getId() < 0) {
          logger.error(idShouldBeGratedThanZero + String.valueOf(invoice.getId()));
          validationExceptions.add(new InvoiceValidationException(
              idShouldBeGratedThanZero + String.valueOf(invoice.getId())));
        }
      }
    }
    if (invoice.getIssuedDate() == null) {

      logger.error(expectedNotEmptyIssueDate);
      validationExceptions
          .add(new InvoiceValidationException(expectedNotEmptyIssueDate));
    } else {
      if (invoice.getIssuedDate().isAfter(LocalDate.now())) {
        logger.error(expectedDateNotGraterThanToday + String
            .valueOf(invoice.getIssuedDate()));
        validationExceptions.add(new InvoiceValidationException(
            expectedDateNotGraterThanToday + String
                .valueOf(invoice.getIssuedDate())));
      }
    }

    if (invoice.getIdentifier() == null) {
      logger.error(expNotEmptyInvIdentifier);
      validationExceptions.add(
          new InvoiceValidationException(expNotEmptyInvIdentifier));
    }
    if (invoice.getEntries() == null) {
      logger.error(expNotEmptyInvIdentifier);
      validationExceptions.add(new InvoiceValidationException(
          "Expected not empty invoice entries"));
    } else {
      invoice.getEntries().forEach(invoiceEntry -> invoiceEntryValidator.validate(invoiceEntry)
          .forEach(validationException -> validationExceptions.add(new InvoiceValidationException(
              "Validation of entry failed, message: " + validationException))));
    }
    if (invoice.getBuyer() == null) {
      logger.error(expNotEmptyInvIdentifier);
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