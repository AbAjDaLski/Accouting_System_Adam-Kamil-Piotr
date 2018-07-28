package pl.coderstrust.accounting.database;

import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractDatabase {

  public Set<Invoice> searchResult = new HashSet<>();

  protected LocalDate changeToSearchDateFrom(LocalDate issuedDateFrom) {
    return issuedDateFrom == null ? LocalDate.MIN : issuedDateFrom;
  }

  protected LocalDate changeToSearchDateTo(LocalDate issuedDateTo) {
    return issuedDateTo == null ? LocalDate.MAX : issuedDateTo;
  }

  protected Set<Invoice> findById(Integer id, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> id.equals(invoice.getId()));
  }

  protected Set<Invoice> findByIdentifier(String identifier, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> identifier.equals(invoice.getIdentifier()));
  }

  protected Set<Invoice> findByIssuedDate(LocalDate issuedDate, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> issuedDate.equals(invoice.getIssuedDate()));
  }

  protected Set<Invoice> findByBuyer(Company buyer, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> buyer.equals(invoice.getBuyer()));
  }

  protected Set<Invoice> findBySeller(Company seller, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> seller.equals(invoice.getSeller()));
  }

  protected Set<Invoice> findByEntries(List<InvoiceEntry> entries, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> entries.equals(invoice.getEntries()));
  }

  protected Set<Invoice> findByDateRange(Set<Invoice> inputList, LocalDate issuedDateFrom,
      LocalDate issuedDateTo) {
    return inputList.stream()
        .filter(invoice -> invoice.getIssuedDate().isAfter(issuedDateFrom))
        .filter(invoice -> invoice.getIssuedDate().isBefore(issuedDateTo))
        .collect(Collectors.toCollection(HashSet::new));
  }

  private Set<Invoice> findGeneric(Predicate<? super Invoice> predicate) {
    return searchResult.stream()
        .filter(predicate)
        .collect(Collectors.toSet());
  }

}
