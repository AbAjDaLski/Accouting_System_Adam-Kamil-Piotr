package pl.coderstrust.accounting.logic;

import static pl.coderstrust.accounting.model.Company.MY_COMPANY_TAX_ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;
import pl.coderstrust.accounting.model.validator.InvoiceValidator;

import java.math.BigDecimal;
import java.util.function.Function;

@Service
public class TaxCalculatorService {

  private final Database database;
  private final InvoiceValidator invoiceValidator;

  @Autowired
  public TaxCalculatorService(Database database,
      InvoiceValidator invoiceValidator) {
    this.database = database;
    this.invoiceValidator = invoiceValidator;
  }

  private BigDecimal calculateGeneric(Function<InvoiceEntry, BigDecimal> getValueFunction, Function<Invoice, Company> getCompanyFunction) {
    BigDecimal sum = BigDecimal.valueOf(0);

    for (Invoice invoice : database.getAll()) {
      for (InvoiceEntry entry : invoice.getEntries()) {
        if (getCompanyFunction.apply(invoice).getTaxId().equals(MY_COMPANY_TAX_ID)) {
          sum = sum
              .add(getValueFunction.apply(entry));
        }
      }
    }
    return sum;
  }

  private BigDecimal getVatValue(InvoiceEntry entry) {
    return entry.getPrice().multiply(
        (entry.getVat().getValue()).divide(BigDecimal.valueOf(100)));
  }

  private BigDecimal getIncomeValue(InvoiceEntry entry) {
    return entry.getPrice();
  }

  public BigDecimal getVatPayable() {
    return calculateGeneric(this::getVatValue, Invoice::getSeller);
  }

  public BigDecimal getRevenues() {
    return calculateGeneric(this::getIncomeValue, Invoice::getSeller);
  }

  public BigDecimal getVatReceivable() {
    return calculateGeneric(this::getVatValue, Invoice::getBuyer);
  }

  public BigDecimal getCosts() {
    return calculateGeneric(this::getIncomeValue, Invoice::getBuyer);
  }
}