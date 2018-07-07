package pl.coderstrust.accounting.database.impl.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.helpers.FileHelper;
import pl.coderstrust.accounting.helpers.FileInvoiceHelper;
import pl.coderstrust.accounting.model.Company;
import pl.coderstrust.accounting.model.Invoice;
import pl.coderstrust.accounting.model.InvoiceEntry;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InFileDatabase implements Database {

  private String databaseFilePath;
  private String idFilePath;
  private int id;
  private Set<Invoice> searchResult = new HashSet<>();

  private List<Invoice> invoices = new ArrayList<>();

  @Autowired
  public InFileDatabase(@Value("database.file.databaseFilePath") String databaseFilePath,
      @Value("database.file.idFilePath") String idFilePath) {
    if (databaseFilePath == null || "".equals(databaseFilePath)) {
      throw new IllegalArgumentException("Database filepath can't be empty");
    }
    if (idFilePath == null || "".equals(idFilePath)) {
      throw new IllegalArgumentException("ID filepath can't be empty");
    }
    this.databaseFilePath = databaseFilePath;
    this.idFilePath = idFilePath;
  }

  @Override
  public int saveInvoice(Invoice invoice) {
    try {
      id = FileInvoiceHelper.getAndIncrementLastId(idFilePath);
    } catch (IOException ioex) {
      throw new RuntimeException("IOException when opening idFile " + idFilePath, ioex);
    }
    Invoice invoiceToWrite = new Invoice(id, invoice.getIdentifier(), invoice.getIssuedDate(),
        invoice.getBuyer(), invoice.getSeller(), invoice.getEntries());
    try {
      FileInvoiceHelper.writeInvoiceToFile(invoiceToWrite, databaseFilePath);
    } catch (IOException ioex) {
      throw new RuntimeException(ioex);
    }
    return id;
  }

  @Override
  public void updateInvoice(Invoice invoice) {
    try {
      FileHelper.updateInvoiceInFile(databaseFilePath, invoice);
    } catch (IOException ioex) {
      throw new RuntimeException(ioex);
    }
  }

  @Override
  public void removeInvoice(int id) {
    try {
      FileHelper.removeInvoiceFromFile(databaseFilePath, id);
    } catch (IOException ioex) {
      throw new RuntimeException(ioex);
    }
  }

  @Override
  public Invoice get(int id) {
    Invoice invoiceTaken = null;
    try {
      invoiceTaken = FileHelper.getInvoiceFromFileById(databaseFilePath, id);
    } catch (IOException ioex) {
      throw new RuntimeException(ioex);
    }
    return invoiceTaken;
  }

  @Override
  public Collection<Invoice> find(Invoice searchParams, LocalDate issuedDateFrom,
      LocalDate issuedDateTo) {
    searchResult = null;
    try {
      searchResult = new HashSet(FileInvoiceHelper.readInvoicesFromFile(databaseFilePath));
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    if (searchResult != null) {
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
    }
    return searchResult;
  }

  private LocalDate changeToSearchDateFrom(LocalDate issuedDateFrom) {
    return issuedDateFrom == null ? LocalDate.MIN : issuedDateFrom;
  }

  private LocalDate changeToSearchDateTo(LocalDate issuedDateTo) {
    return issuedDateTo == null ? LocalDate.MAX : issuedDateTo;
  }

  private Set<Invoice> findById(Integer id, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> id.equals(invoice.getId()));
  }

  private Set<Invoice> findByIdentifier(String identifier, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> identifier.equals(invoice.getIdentifier()));
  }

  private Set<Invoice> findByIssuedDate(LocalDate issuedDate, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> issuedDate.equals(invoice.getIssuedDate()));
  }

  private Set<Invoice> findByBuyer(Company buyer, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> buyer.equals(invoice.getBuyer()));
  }

  private Set<Invoice> findBySeller(Company seller, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> seller.equals(invoice.getSeller()));
  }

  private Set<Invoice> findByEntries(List<InvoiceEntry> entries, Set<Invoice> resultSchearching) {
    return findGeneric(invoice -> entries.equals(invoice.getEntries()));
  }

  private Set<Invoice> findByDateRange(Set<Invoice> inputList, LocalDate issuedDateFrom,
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

  @Override
  public Collection<Invoice> getAll() {
    try {
      invoices = FileInvoiceHelper.readInvoicesFromFile(databaseFilePath);
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }
    return invoices;
  }
}