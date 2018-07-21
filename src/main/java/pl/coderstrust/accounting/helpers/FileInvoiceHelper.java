package pl.coderstrust.accounting.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.accounting.database.impl.file.InFileDatabase;
import pl.coderstrust.accounting.model.Invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileInvoiceHelper {

  private static ObjectMapper mapper = new ObjectMapper();
  private static Logger logger = LoggerFactory.getLogger(InFileDatabase.class);

  static {
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  public static void writeInvoiceToFile(Invoice invoiceToWrite, String filePath)
      throws IOException {
    String convertedInvoice = mapper.writeValueAsString(invoiceToWrite);
    FileHelper.appendToFile(convertedInvoice, filePath);
  }

  public static List<Invoice> readInvoicesFromFile(String filePath) throws IOException {
    List<Invoice> invoices = new ArrayList<>();
    List<String> result = FileHelper.readFromFile(filePath);
    for (String line : result) {
      Invoice invoice = mapper.readValue(line, Invoice.class);
      invoices.add(invoice);
    }
    return invoices;
  }

  public static int getAndIncrementLastId(String filePath) throws IOException {
    List<String> lines = null;
    lines = FileHelper.readFromFile(filePath);
    if (lines.size() > 1) {
      logger.error("File can't included more than 1 id");
      throw new IllegalStateException(
          "File can't include more than 1 id");
    }
    if (lines.isEmpty()) {
      FileHelper.writeToFile(Collections.singletonList("1"), filePath);
      return 1;
    }
    int id = Integer.valueOf(lines.get(0)) + 1;
    List<String> idToSave = Arrays.asList(String.valueOf(id));
    FileHelper.writeToFile(idToSave, filePath);
    return id;
  }
}