package pl.coderstrust.accounting.helpers;

import pl.coderstrust.accounting.model.Invoice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

  public static void checkIfFileExistOrIsEmpty(File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException("No file under given path");
    }
    if (file.length() == 0) {
      throw new IllegalArgumentException("List of invoices is empty");
    }
  }

  private static void closeBuffer(BufferedWriter bufferedWriter, BufferedReader bufferedReader)
      throws IOException {
    bufferedReader.close();
    bufferedWriter.close();
  }

  private static void renameFile(File file, File tempFile) {
    file.delete();
    tempFile.renameTo(file);
  }


  public static void writeToFile(List<String> lines, String filePath) throws IOException {
    if (lines == null) {
      throw new IllegalArgumentException("Parameter lines may not be null");
    }
    if (filePath == null) {
      throw new IllegalArgumentException("Parameter filePath may not be null");
    }
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
      for (String line : lines) {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
    }
  }

  public static List<String> readFromFile(String filePath) throws IOException {
    ArrayList<String> lines = new ArrayList<>();
    if (new File(filePath).exists()) {
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          lines.add(line);
        }
      }
    }
    return lines;
  }

  public static void appendToFile(String line, String filePath)
      throws IOException {
    if (line == null) {
      throw new IllegalArgumentException("Parameter line may not be null");
    }
    if (filePath == null) {
      throw new IllegalArgumentException("Parameter filePath may not be null");
    }
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
      bufferedWriter.append(line);
      bufferedWriter.newLine();
    }
  }

  public static void updateInvoiceInFile(String filePath, Invoice invoice) throws IOException {
    boolean invoiceUpdated = false;
    ArrayList<String> lines = new ArrayList<>();
    String updatedInvoice = JsonConverter.toJson(invoice);
    File file = new File(filePath);
    File tempFile = new File("tempFile.txt");
    checkIfFileExistOrIsEmpty(file);
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if ((JsonConverter.fromJson(line).getId() == invoice.getId())) {
        lines.add(updatedInvoice);
        invoiceUpdated = true;
        continue;
      }
      lines.add(line);
    }
    for (String listLine : lines) {
      bufferedWriter.write(listLine);
      bufferedWriter.newLine();
    }
    closeBuffer(bufferedWriter, bufferedReader);
    renameFile(file, tempFile);
    if (!invoiceUpdated) {
      throw new IllegalArgumentException("No invoice with given id in file");
    }
  }

  public static void removeInvoiceFromFile(String filePath, int id) throws IOException {
    boolean invoiceRemoved = false;
    File file = new File(filePath);
    checkIfFileExistOrIsEmpty(file);
    File tempFile = new File("tempFile.txt");
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if ((JsonConverter.fromJson(line).getId() == id)) {
        invoiceRemoved = true;
        continue;
      }
      bufferedWriter.write(line);
      bufferedWriter.newLine();
    }
    closeBuffer(bufferedWriter, bufferedReader);
    renameFile(file, tempFile);
    if (!invoiceRemoved) {
      throw new IllegalArgumentException("No invoice with given id in file");
    }
  }

  public static Invoice getInvoiceFromFileById(String filePath, int id) throws IOException {
    boolean invoiceInFile = false;
    File file = new File(filePath);
    Invoice invoiceFound = null;
    checkIfFileExistOrIsEmpty(file);
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (JsonConverter.fromJson(line).getId() == id) {
        invoiceInFile = true;
        invoiceFound = JsonConverter.fromJson(line);
      }
    }
    bufferedReader.close();
    if (!invoiceInFile) {
      throw new IllegalArgumentException("No invoice with given id in file");
    }
    return invoiceFound;
  }
}