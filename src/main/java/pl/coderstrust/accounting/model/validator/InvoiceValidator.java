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
  private static final String EXPECTED_NOT_EMPTY_ID = "Expected not empty Id";
  private static final String ID_SHOULD_BE_GRATED_THAN_ZERO = "Expected Id to be greater than 0,"
      + " got: ";
  private static final String EXPECTED_NOT_EMPTY_ISSUE_DATE = "Expected not empty issue date";
  private static final String EXPECTED_DATE_NOT_GRATER_THAN_TODAY = "Expected issue date not"
      + " greater"
      + " than today date, got: ";
  private static final String EXP_NOT_EMPTY_INV_IDENTIFIER = "Expected not empty invoice "
      + "identifier";

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
      if (invoice.getId() == null) {
        logger.error(EXPECTED_NOT_EMPTY_ID);
        validationExceptions.add(new InvoiceValidationException(EXPECTED_NOT_EMPTY_ID));
      } else {
        if (invoice.getId() < 0) {
          logger.error(ID_SHOULD_BE_GRATED_THAN_ZERO + String.valueOf(invoice.getId()));
          validationExceptions.add(new InvoiceValidationException(
              ID_SHOULD_BE_GRATED_THAN_ZERO + String.valueOf(invoice.getId())));
        }
      }
    }
    if (invoice.getIssuedDate() == null) {

      logger.error(EXPECTED_NOT_EMPTY_ISSUE_DATE);
      validationExceptions
          .add(new InvoiceValidationException(EXPECTED_NOT_EMPTY_ISSUE_DATE));
    } else {
      if (invoice.getIssuedDate().isAfter(LocalDate.now())) {
        logger.error(EXPECTED_DATE_NOT_GRATER_THAN_TODAY + String
            .valueOf(invoice.getIssuedDate()));
        validationExceptions.add(new InvoiceValidationException(
            EXPECTED_DATE_NOT_GRATER_THAN_TODAY + String
                .valueOf(invoice.getIssuedDate())));
      }
    }

    if (invoice.getIdentifier() == null) {
      logger.error(EXP_NOT_EMPTY_INV_IDENTIFIER);
      validationExceptions.add(
          new InvoiceValidationException(EXP_NOT_EMPTY_INV_IDENTIFIER));
    }
    if (invoice.getEntries() == null) {
      logger.error(EXP_NOT_EMPTY_INV_IDENTIFIER);
      validationExceptions.add(new InvoiceValidationException(
          "Expected not empty invoice entries"));
    } else {
      invoice.getEntries().forEach(invoiceEntry -> invoiceEntryValidator.validate(invoiceEntry)
          .forEach(validationException -> validationExceptions.add(new InvoiceValidationException(
              "Validation of entry failed, message: " + validationException))));
    }
    if (invoice.getBuyer() == null) {
      logger.error(EXP_NOT_EMPTY_INV_IDENTIFIER);
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