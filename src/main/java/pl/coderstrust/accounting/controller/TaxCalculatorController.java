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

  public TaxCalculatorController (TaxCalculatorService taxCalculatorService) {
    this.taxCalculatorService = taxCalculatorService;
  }

  @GetMapping("/getIncome")
  public BigDecimal getIncome() {
    return taxCalculatorService.getIncome();
  }

  @GetMapping("/getOutcome")
  public BigDecimal getOutcome() {
    return taxCalculatorService.getOutcome();
  }

  @GetMapping("/getIncomeVat")
  public BigDecimal getIncomeVat() {
    return taxCalculatorService.getIncomeVat();
  }

  @GetMapping("/getOutcomeVat")
  public BigDecimal getOutcomeVat() {
    return taxCalculatorService.getOutcomeVat();
  }

  @GetMapping("/getFinancialResult")
  public BigDecimal getFinancialResult() {
    return taxCalculatorService.getIncomeVat().subtract(taxCalculatorService.getOutcome());
  }
}