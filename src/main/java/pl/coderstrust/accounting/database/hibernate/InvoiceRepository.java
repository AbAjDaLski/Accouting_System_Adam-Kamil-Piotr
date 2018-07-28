package pl.coderstrust.accounting.database.hibernate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.coderstrust.accounting.model.Invoice;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

}
