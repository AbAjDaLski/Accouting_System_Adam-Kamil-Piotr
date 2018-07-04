package pl.coderstrust.accounting.model.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.database.InMemoryDatabase;
import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.validator.exception.CompanyValidationException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class CompanyValidator {

  public static final String COMPANY_NAME_FIELD_FAILED = "Validation of company name field failed."
      + " Expected name field not empty, got: ";
  public static final String COMPANY_TAX_ID_FIELD = "Validation of company tax id field failed."
      + " Expected tax id field not empty, got: ";
  public static final String COMPANY_TAX_ID_FIELD_FAILED = "Validation of company tax id field "
      + "failed. Expected tax id should consist of 10 digits, got: ";
  public static final String COMPANY_POSTAL_CODE_FIELD_FAILED = "Validation of company postal"
      + " code field failed : ";
  public static final String COMPANY_STREET_AND_NUMBER_FIELD_FAILED = "Validation of company"
      + " street and number field failed. Expected street and number field not empty, got: ";
  public static final String COMPANY_LOCATION_FIELD_FAILED = "Validation of company location field"
      + " failed. Expected location field not empty, got: ";
  private static Logger logger = LoggerFactory.getLogger(InMemoryDatabase.class);

  public Collection<CompanyValidationException> validate(Company company) {
    List<CompanyValidationException> validationExceptions = new LinkedList<>();
    if (company.getName() == null) {
      logger.error(COMPANY_NAME_FIELD_FAILED
          + String.valueOf(company.getName()));
      validationExceptions.add(new CompanyValidationException(
          COMPANY_NAME_FIELD_FAILED
              + String.valueOf(company.getName())));
    }

    if (company.getTaxId() == null) {
      logger.error(
          COMPANY_TAX_ID_FIELD + String.valueOf(company.getTaxId()));
      validationExceptions.add(new CompanyValidationException(
          COMPANY_TAX_ID_FIELD
              + String.valueOf(company.getTaxId())));
    } else {
      if (company.getTaxId().length() != 10) {
        logger.error(
            COMPANY_TAX_ID_FIELD_FAILED + String.valueOf(company.getTaxId()));
        validationExceptions.add(new CompanyValidationException(
            COMPANY_TAX_ID_FIELD_FAILED + String.valueOf(company.getTaxId())));
      }
    }
    if (company.getPostalCode() == null || !company.getPostalCode().matches("\\d{2}-\\d{3}")) {
      logger.error(COMPANY_POSTAL_CODE_FIELD_FAILED
          + String.valueOf(company.getPostalCode()));
      validationExceptions.add(new CompanyValidationException(
          COMPANY_POSTAL_CODE_FIELD_FAILED
              + String.valueOf(company.getPostalCode())));
    }

    if (company.getStreetAndNumber() == null) {
      logger.error(
          COMPANY_STREET_AND_NUMBER_FIELD_FAILED
              + String.valueOf(company.getStreetAndNumber()));
      validationExceptions.add(new CompanyValidationException(
          COMPANY_STREET_AND_NUMBER_FIELD_FAILED
              + String.valueOf(company.getStreetAndNumber())));
    }

    if (company.getLocation() == null) {
      logger.error(
          COMPANY_LOCATION_FIELD_FAILED + String.valueOf(company.getStreetAndNumber()));
      validationExceptions.add(new CompanyValidationException(
          COMPANY_LOCATION_FIELD_FAILED
              + String.valueOf(company.getStreetAndNumber())));
    }
    return validationExceptions;
  }
}