package pl.coderstrust.accounting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.accounting.logic.TaxCalculatorService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/calculation")
public class TaxCalculatorController {

  private TaxCalculatorService taxCalculatorService;

  public TaxCalculatorController(TaxCalculatorService taxCalculatorService) {
    this.taxCalculatorService = taxCalculatorService;
  }

  @GetMapping("/getIncome")
  public BigDecimal getRevenues() {
    return taxCalculatorService.getRevenues();
  }

  @GetMapping("/getOutcome")
  public BigDecimal getCosts() {
    return taxCalculatorService.getCosts();
  }

  @GetMapping("/getVatPayable")
  public BigDecimal getIncomeVat() {
    return taxCalculatorService.getVatPayable();
  }

  @GetMapping("/getVatReceivable")
  public BigDecimal getOutcomeVat() {
    return taxCalculatorService.getVatReceivable();
  }

  @GetMapping("/getFinancialResult")
  public BigDecimal getFinancialResult() {
    return taxCalculatorService.getVatPayable().subtract(taxCalculatorService.getCosts());
  }
}