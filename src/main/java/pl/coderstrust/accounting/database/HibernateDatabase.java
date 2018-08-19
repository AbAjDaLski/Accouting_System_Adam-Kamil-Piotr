package pl.coderstrust.accounting.database;

import org.springframework.stereotype.Repository;
import pl.coderstrust.accounting.database.hibernate.InvoiceRepository;
import pl.coderstrust.accounting.model.Invoice;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class HibernateDatabase extends AbstractDatabase {

  private InvoiceRepository invoiceRepository;

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
    Optional<Invoice> invoiceToUpdate = invoiceRepository.findById(invoice.getId());
    if (!invoiceToUpdate.isPresent()) {
      throw new IllegalStateException("Invoice with provided id does not exist");
    }
    invoiceRepository.save(invoice);
  }

  @Override
  public void removeInvoice(int id) {
    invoiceRepository.deleteById(id);
  }

  @Override
  public Optional<Invoice> get(int id) {
    return invoiceRepository.findById(id);
  }

  @Override
  public Collection<Invoice> getAll() {
    Iterable<Invoice> iterable = invoiceRepository.findAll();
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toList());
  }
}
