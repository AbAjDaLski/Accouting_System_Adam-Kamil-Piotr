package pl.coderstrust.accounting.database;

import org.springframework.stereotype.Repository;
import pl.coderstrust.accounting.database.hibernate.InvoiceRepository;
import pl.coderstrust.accounting.model.Invoice;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class HibernateDatabase extends AbstractDatabase implements Database {

  private InvoiceRepository invoiceRepository;
  private Set<Invoice> searchResult = new HashSet<>();

  public HibernateDatabase(
      InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  @Override
  public int saveInvoice(Invoice invoice) {
    return invoiceRepository.save(invoice).getId();
  }

  @Override
  public void updateInvoice(Invoice invoice) {
    Invoice temInvoice = invoiceRepository.findById(invoice.getId()).orElse(null);
    if (temInvoice != null && temInvoice.getId().equals(invoice.getId())) {
      invoiceRepository.save(invoice);
    }
  }

  @Override
  public void removeInvoice(int id) {
    invoiceRepository.deleteById(id);
  }

  @Override
  public Invoice get(int id) {
    return invoiceRepository.findById(id).orElse(null);
  }

  @Override
  public Collection<Invoice> find(Invoice searchParams, LocalDate issuedDateFrom,
      LocalDate issuedDateTo) {
    Set<Invoice> searchResult = new HashSet<>();
    invoiceRepository.findAll().forEach(searchResult::add);

    searchResult = findByDateRange(searchResult, changeToSearchDateFrom(issuedDateFrom),
        changeToSearchDateTo(issuedDateTo));
    if (searchParams != null) {
      if (searchParams.getId() != null) {
        searchResult = findById(searchParams.getId(), searchResult);
      }
      if (searchParams.getIdentifier() != null) {
        searchResult = findByIdentifier(searchParams.getIdentifier(), searchResult);
      }
      if (searchParams.getIssuedDate() != null) {
        searchResult = findByIssuedDate(searchParams.getIssuedDate(), searchResult);
      }
      if (searchParams.getBuyer() != null) {
        searchResult = findByBuyer(searchParams.getBuyer(), searchResult);
      }
      if (searchParams.getSeller() != null) {
        searchResult = findBySeller(searchParams.getSeller(), searchResult);
      }
      if (searchParams.getEntries() != null) {
        searchResult = findByEntries(searchParams.getEntries(), searchResult);
      }
    }
    return searchResult;
  }

  @Override
  public Collection<Invoice> getAll() {
    Iterable<Invoice> iterable = invoiceRepository.findAll();
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toList());
  }
}
