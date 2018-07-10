package pl.coderstrust.accounting.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.accounting.database.InMemoryDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

  private static Logger logger = LoggerFactory.getLogger(InMemoryDatabase.class);
  private static final String FILE_PATH_CANNOT_BE_NULL_MESSAGE = "File path cannot be null";
  private static final String LINE_CANNOT_BE_NULL_MESSAGE = "Line cannot be null";

  public static void writeToFile(List<String> lines, String filePath) throws IOException {
    if (lines == null) {
      logger.error(LINE_CANNOT_BE_NULL_MESSAGE);
      throw new IllegalArgumentException(LINE_CANNOT_BE_NULL_MESSAGE);
    }
    if (filePath == null) {
      logger.error(FILE_PATH_CANNOT_BE_NULL_MESSAGE);
      throw new IllegalArgumentException(FILE_PATH_CANNOT_BE_NULL_MESSAGE);
    }
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
      for (String line : lines) {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }
    }
  }

  public static List<String> readFromFile(String file) throws IOException {
    ArrayList<String> lines = new ArrayList<>();
    if (new File(file).exists()) {
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
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
      logger.error(LINE_CANNOT_BE_NULL_MESSAGE);
      throw new IllegalArgumentException(LINE_CANNOT_BE_NULL_MESSAGE);
    }
    if (filePath == null) {
      logger.error(FILE_PATH_CANNOT_BE_NULL_MESSAGE);
      throw new IllegalArgumentException(FILE_PATH_CANNOT_BE_NULL_MESSAGE);
    }
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
      bufferedWriter.append(line);
      bufferedWriter.newLine();
    }
  }

}