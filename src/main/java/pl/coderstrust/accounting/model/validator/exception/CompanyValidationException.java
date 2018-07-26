package pl.coderstrust.accounting.model.validator.exception;

// TODO please remove all those exceptions from validators - validator should simply return list of strings
// - now it works terribly - just check Swagger output
public class CompanyValidationException extends Exception {

  public CompanyValidationException(String message) {
    super(message);
  }
}