package pl.coderstrust.accounting.logic;

import static pl.coderstrust.accounting.model.Company.MY_COMPANY_TAX_ID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;

import java.math.BigDecimal;
import java.util.function.Function;

@Service
public class TaxCalculatorService {

  private final Database database;

  @Autowired
  public TaxCalculatorService(@Qualifier("inMemoryDatabase") Database database) {
    this.database = database;
  }

  private BigDecimal calculateGeneric(Function<InvoiceEntry,BigDecimal> getValueFunction, Function<Invoice,Company> getCompanyFunction) {
    BigDecimal sumOutcomeVAT = BigDecimal.valueOf(0.00);

    for (Invoice invoice : database.getAll()) {
      for (InvoiceEntry entry : invoice.getEntries()) {
        if (getCompanyFunction.apply(invoice).getTaxId().equals(MY_COMPANY_TAX_ID)) {
          sumOutcomeVAT = sumOutcomeVAT
               .add(getValueFunction.apply(entry));
        }
      }
    }
    return sumOutcomeVAT;
  }

  private BigDecimal getVatValue(InvoiceEntry entry) {
    return entry.getPrice().multiply
        (BigDecimal.valueOf(
            (entry.getVat().getValue())
                / 100.00));
  }

  private BigDecimal getIncomeValue(InvoiceEntry entry) {
    return entry.getPrice();
  }

  public BigDecimal getIncomeVat() {
    return calculateGeneric(this::getVatValue, Invoice::getSeller);
  }

  public BigDecimal getIncome() {
    return calculateGeneric(this::getIncomeValue, Invoice::getSeller);
  }

  public BigDecimal getOutcomeVat() {
    return calculateGeneric(this::getVatValue, Invoice::getBuyer);
  }

  public BigDecimal getOutcome() {
    return calculateGeneric(this::getIncomeValue, Invoice::getBuyer);
  }
}