package pl.coderstrust.accounting.model.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.model.InvoiceEntry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class InvoiceEntryValidator {

  public static final String EXPECTED_NOT_EMPTY_DESCRIPTION = "Expected not empty description ";
  public static final String EXPECTED_NOT_EMPTY_PRICE = "Expected not empty price";
  public static final String EXPECTED_NOT_EMPTY_VAT = "Expected not empty vat";
  private static Logger logger = LoggerFactory.getLogger(InvoiceEntryValidator.class);

  public Collection<String> validate(InvoiceEntry invoiceEntry) {
    List<String> validationExceptions = new LinkedList<>();
    if (invoiceEntry.getDescription() == null) {
      logger.error(EXPECTED_NOT_EMPTY_DESCRIPTION);
      validationExceptions.add(EXPECTED_NOT_EMPTY_DESCRIPTION);
    }

    if (invoiceEntry.getPrice() == null) {
      logger.error(EXPECTED_NOT_EMPTY_PRICE);
      validationExceptions.add(EXPECTED_NOT_EMPTY_PRICE);
    }

    if (invoiceEntry.getVat() == null) {
      logger.error(EXPECTED_NOT_EMPTY_VAT);
      validationExceptions.add(EXPECTED_NOT_EMPTY_VAT);
    }
    return validationExceptions;
  }
}