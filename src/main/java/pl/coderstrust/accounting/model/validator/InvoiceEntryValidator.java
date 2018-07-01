package pl.coderstrust.accounting.model.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.model.InvoiceEntry;
import pl.coderstrust.accounting.model.validator.exception.InvoiceEntryValidationException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class InvoiceEntryValidator {

  private static Logger logger = LoggerFactory.getLogger(InvoiceEntryValidator.class);

  public Collection<InvoiceEntryValidationException> validate(InvoiceEntry invoiceEntry) {
    List<InvoiceEntryValidationException> validationExceptions = new LinkedList<>();
  public Collection<String> validate(InvoiceEntry invoiceEntry) {
    List<String> validationExceptions = new LinkedList<>();
    if (invoiceEntry.getDescription() == null) {
      logger.error("Expected not empty description ");
      validationExceptions.add(new InvoiceEntryValidationException(
          "Expected not empty description "));
      validationExceptions.add("Expected not empty description ");
    }

    if (invoiceEntry.getPrice() == null) {
      logger.error("Expected not empty price");
      validationExceptions.add(new InvoiceEntryValidationException(
          "Expected not empty price"));
      validationExceptions.add("Expected not empty price");
    }

    if (invoiceEntry.getVat() == null) {
      logger.error("Expected not empty vat");
      validationExceptions.add(new InvoiceEntryValidationException(
          "Expected not empty vat"));
      validationExceptions.add("Expected not empty vat");
    }
    return validationExceptions;
  }
}