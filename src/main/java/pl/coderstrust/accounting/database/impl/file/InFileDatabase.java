package pl.coderstrust.accounting.database.impl.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import pl.coderstrust.accounting.database.AbstractDatabase;
import pl.coderstrust.accounting.database.Database;
import pl.coderstrust.accounting.helpers.FileHelper;
import pl.coderstrust.accounting.helpers.FileInvoiceHelper;
import pl.coderstrust.accounting.model.Invoice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class InFileDatabase extends AbstractDatabase implements Database {

  private String databaseFilePath;
  private String idFilePath;
  private int id;
  private List<Invoice> invoices = new ArrayList<>();
  private static Logger logger = LoggerFactory.getLogger(InFileDatabase.class);
  private static final String EXCEPTION_WHEN_OPENING_ID_FILE_MESSAGE = "IOException when opening"
      + " idFile ";
  private static final String EXCEPTION_WHEN_OPENING_DATABASE_FILE_MESSAGE = "IOException when"
      + " opening databaseFile";

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
      logger.error(EXCEPTION_WHEN_OPENING_ID_FILE_MESSAGE + idFilePath, ioex);
      throw new RuntimeException(EXCEPTION_WHEN_OPENING_ID_FILE_MESSAGE + idFilePath, ioex);
    }
    Invoice invoiceToWrite = new Invoice(id, invoice.getIdentifier(), invoice.getIssuedDate(),
        invoice.getBuyer(), invoice.getSeller(), invoice.getEntries());
    try {
      FileInvoiceHelper.writeInvoiceToFile(invoiceToWrite, databaseFilePath);
    } catch (IOException ieox) {
      logger.error(EXCEPTION_WHEN_OPENING_DATABASE_FILE_MESSAGE + databaseFilePath + ieox);
      throw new RuntimeException(ieox);
    }
    logger.info("Invoice saved with id = " + id);
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
  public Optional<Invoice> get(int id) {
    Invoice invoiceTaken = null;
    try {
      invoiceTaken = FileHelper.getInvoiceFromFileById(databaseFilePath, id);
    } catch (IOException ioex) {
      throw new RuntimeException(ioex);
    }
    return Optional.ofNullable(invoiceTaken);
  }

  @Override
  public Collection<Invoice> find(Invoice searchParams, LocalDate issuedDateFrom,
      LocalDate issuedDateTo) {
    searchResult = null;
    try {
      searchResult = new HashSet(FileInvoiceHelper.readInvoicesFromFile(databaseFilePath));
    } catch (IOException ioex) {

      logger.error(EXCEPTION_WHEN_OPENING_DATABASE_FILE_MESSAGE + databaseFilePath + ioex);
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
    logger.info("Invoice found");
    return searchResult;
  }

  @Override
  public Collection<Invoice> getAll() {
    try {
      invoices = FileInvoiceHelper.readInvoicesFromFile(databaseFilePath);
    } catch (IOException ioex) {
      logger.error(EXCEPTION_WHEN_OPENING_DATABASE_FILE_MESSAGE + databaseFilePath + ioex);
      ioex.printStackTrace();
    }
    logger.info("Invoices found");
    return invoices;
  }
}